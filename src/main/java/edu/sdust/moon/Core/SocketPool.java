package edu.sdust.moon.Core;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

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
     */
    public StreamSocket put(Address key) {
        StreamSocket value = null;
        try {
            value = new StreamSocket(key);
            pool.put(key, value);
        } catch (IOException e) {
            Start.getLogger().error("connect the node( " + key + " ) failed");
        }
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
        pool.put(value.getAddress(), value);
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
                if (socket!=null) {
                    while (true) {
                        if (!takenOut.contains(socket)) {
                            try {
                                if (!socket.isClosed()) {
                                    socket.close();
                                    if (removeInPool(key) != null) {
                                        takenOut.remove(socket);
                                        Start.getConfig().getNodes().remove(key);
                                        Start.getLogger().info(" Disconnected the node ( " + key + " )");
                                        return;
                                    } else {
                                        System.out.println("error");
                                        Start.getLogger().error("The node ( " + key + ") had been disconnect.");
                                    }
                                }
                            } catch (IOException e) {
                                Start.getLogger().error(" Disconnect the node ( " + key + ") unsuccessfully.");
                            }
                        }
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

    public StreamSocket removeInPool(Object key) {
        StreamSocket socket=null;
        for (Address address: pool.keySet()) {
            if (address.equals(key)){
              socket=pool.remove(address);
              break;
            }
        }
        return socket;
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

    public int size() {
        return pool.size();
    }

    public Set<Address> keySet() {
        return pool.keySet();
    }
}