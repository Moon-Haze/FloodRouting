package edu.sdust.moon.Core;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SocketPool {

    private final HashMap<Address, StreamSocket> pool = new HashMap<>();
    private final ArrayList<StreamSocket> takenOut = new ArrayList<>();

    private SocketPool() {
    }

    public static SocketPool createPool() {
        return new SocketPool();
    }

    /**
     * 以 Address 创建 StreamSock 建立两个节点的连接
     *
     * @param key 与其相连的节点
     * @return 创建 StreamSock
     * @throws IOException
     */
    public StreamSocket put(Address key) throws IOException {
        StreamSocket value = new StreamSocket(key);
        pool.put(key, value);
        return value;
    }

    /**
     * 通过 其他节点的请求连接的 Socket 建立两个节点的连接
     *
     * @param socket 其他节点的请求连接的 Socket
     * @return 创建的 StreamSock
     * @throws IOException
     */
    public StreamSocket put(Socket socket) throws IOException {
        StreamSocket value = new StreamSocket(socket);
        Address key = value.getAddress();
        pool.put(key, value);
        return value;
    }

    public Collection<StreamSocket> values() {
        return pool.values();
    }

    public synchronized void remove(Address key) {
        new Thread() {
            @Override
            public void run() {
                StreamSocket socket = get(key);
                while (true) {
                    if (!takenOut.contains(socket)) {
                        try {
                            if (!socket.isClosed()) {
                                socket.close();
                            }
                            Start.getLogger().info(" Disconnect the node ( " + key + ") ");
                        } catch (IOException e) {
                            Start.getLogger().error(" Disconnect the node ( " + key + ") failed");
                        }
                        pool.remove(key, socket);
                        break;
                    }
                }
            }
        }.start();
    }

    public StreamSocket get(Address key) {
        synchronized (pool) {
            for (Address item : pool.keySet()) {
                if (item.equals(key)) {
                    return pool.get(item);
                }
            }
        }
        return null;
    }


    public boolean containsKey(Address key) {
        return get(key) != null;
    }

    public synchronized void remand(StreamSocket so) {
        takenOut.remove(so);
    }

    public synchronized void removeAll() {
        for (Address address : pool.keySet()) {
            StreamSocket value = get(address);
            while (true) {
                if (!takenOut.contains(value)) {
                    try {
                        value.close();
                        Start.getLogger().info(" Disconnect the node ( " + address + ")");
                    } catch (IOException e) {
                        Start.getLogger().error(" Disconnect the node ( " + address + ") failed");
                    }
                    pool.remove(address, value);
                    break;
                }
            }
        }
    }
}