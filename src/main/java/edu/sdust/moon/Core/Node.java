package edu.sdust.moon.Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Set;

public class Node {

    private Address address;
    private final TimeHashSet registerPackage = new TimeHashSet();
    private final SocketPool pool = SocketPool.createPool();
    private ServerSocket receiver;

    private Node() {
    }

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
                Start.getConfig().getNodes().add(address);
                Start.getLogger().info("connect the node( " + address + " ) successfully");
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
        for (StreamSocket socket : pool.values()) {
            if (!socket.getAddress().equals(address)) {
                try {
                    ObjectOutputStream ops = socket.getObjectOutputStream();
                    ops.writeObject(pkg);
                    ops.flush();
                    pool.remand(socket);
                    Start.getLogger().info("Node ( " + address + " ) has send a Package\n"
                            + pkg + "\nTo " + socket.getAddress());
                } catch (IOException e) {
                    Start.getLogger().error(e);
                }
            }
        }
    }

    public void sendData(String data, String address) {
        Package p = new Package();
        p.setMessage(data);
        p.setFrom(this.address);
        p.setTo(Address.createAddress(address));
        sendPackage(p, this.address);
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
            Start.getLogger().info("Connect the node ( " + sockAddress + " ) ");
            try {
                ObjectInputStream ois = socket.getObjectInputStream();
                Package p;
                while ((!socket.isClosed()) && (p = (Package) ois.readObject()) != null) {
                    if (registerPackage.contains(p)) {
                        Start.getLogger().info("Node ( " + sockAddress + " ) had received a repeated Package from ( "
                                + p.getFrom() + " )\n" + p);
                    } else {
                        registerPackage.add(p);
                        if (address.equals(p.getTo())) {
                            Start.getLogger().info("Node ( " + sockAddress + " ) had received a package  node from ( "
                                    + p.getFrom() + " )\n\n" + p + "\n");
                        } else if (p.isUseful()) {
                            Start.getLogger().info("Node ( " + sockAddress + " ) had received a package to other node from ( "
                                    + p.getFrom() + " )\n" + p);
                            p.countAdd();
                            sendPackage(p, sockAddress);
                        } else {
                            Start.getLogger().info("Node ( " + sockAddress + " ) had received a invalid Package"
                                    + " from ( " + p.getFrom() + " )\n" + p);
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
        } catch (IOException e) {
            Start.getLogger().error(e);
        }
        System.exit(0);
    }
}