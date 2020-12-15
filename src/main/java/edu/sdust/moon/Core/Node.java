package edu.sdust.moon.Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.util.Set;

public class Node {

    private Address address;
    private final TimeHashSet registerPackage = new TimeHashSet();
    private final SocketPool pool = SocketPool.createPool();
    private ServerSocket receiver;

    private Node() {}

    public static Node createNode(Address address) throws IOException {
        Node node = new Node();
        node.address = address;
        node.receiver = new ServerSocket(address.getPort());
        return node;
    }

    public Set<Address> getLinkNodes() {
        return pool.keySet();
    }

    public void connectNode(Address address) {
        if (!pool.containsKey(address)) {
            StreamSocket socket = pool.put(address);
            if (socket != null) {
                receivePackage(socket);
            }
        } else {
            Start.getLogger().info("The Node( " + address + " ) had been connected");
        }
    }

    public void disconnectNode(Address address) {
        if (pool.containsKey(address)) {
            pool.remove(address);
        } else {
            Start.getLogger().info("The Node( " + address + " ) is not connected");
        }
    }

    public void start() {
        receiveSocket();
    }

    public void sendPackage(Package pkg, Address address) {
        if (pool.size()==0){
            Start.getLogger().info("No node connecting with.");
            return;
        }
        for (StreamSocket socket : pool.values()) {
            if (!socket.getAddress().equals(address)) {
                try {
                    ObjectOutputStream ops = socket.getObjectOutputStream();
                    ops.writeObject(pkg);
                    ops.flush();
                    pool.remand(socket);
                    Start.getLogger().info("\033[1;36m "+"Node ( " + address + " ) has send a Package\n"
                            + pkg + "\nTo " + socket.getAddress() +"\033[0m");
                } catch (IOException e) {
                    Start.getLogger().error(e);
                }
            }
        }
    }

    public void sendData(String data, String address) {
        sendPackage(new Package(data, this.address, Address.createAddress(address)), this.address);
    }

    public void receiveSocket() {
        new Thread(() -> {
            while (true) {
                try {
                    receivePackage(pool.put(receiver.accept()));
                } catch (IOException e) {
                    Start.getLogger().error(e);
                }
            }
        }).start();
    }

    public void receivePackage(StreamSocket socket) {
        new Thread(() -> {
            Address sockAddress = socket.getAddress();
            Start.getLogger().info("Connect the node ( " + sockAddress + " ) successfully");
            try {
                ObjectInputStream ois = socket.getObjectInputStream();
                Package p;
                while ((!socket.isClosed()) && (p = (Package) ois.readObject()) != null) {
                    if (registerPackage.contains(p)) {
                        //this is color5
                        Start.getLogger().info("\033[1;33m "+"Node ( " + address + " ) had received a repeated Package from ( "
                                + sockAddress + " )\n" + p +"\033[0m");
                    } else {
                        registerPackage.add(p);
                        if (address.equals(p.getTo())) {
                            Start.getLogger().info("\033[1;34m "+"Node ( " + address + " ) had received a package  node from ( "
                                    + sockAddress + " )\n\n" + p+"\033[0m" );
                        } else if (p.isUseful()) {
                            Start.getLogger().info("\033[1;35m "+"Node ( " + address + " ) had received a package to other node from ( "
                                    + sockAddress + " )\n" + p+"\033[0m");
                            p.countMinus();
                            sendPackage(p, sockAddress);
                        } else {
                            Start.getLogger().info("\033[1;31m "+"Node ( " + address + " ) had received a invalid Package"
                                    + " from ( " + sockAddress + " )\n" + p+"\033[0m");
                        }
                    }
                }
            } catch (IOException e) {
                pool.remove(sockAddress);
            } catch (ClassNotFoundException e) {
                Start.getLogger().error(e);
            }
        }).start();
    }

    public void stop() {
        pool.removeAll();
        try {
            receiver.close();
        } catch (Exception e) {
            System.exit(0);
        }
    }
}
