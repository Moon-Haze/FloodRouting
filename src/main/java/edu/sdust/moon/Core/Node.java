package edu.sdust.moon.Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.SocketException;
import java.util.ArrayList;

public class Node {

    private Address address;
    private final ArrayList<Package> registerPackage = new ArrayList<>();
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

    public Address getAddress() {
        return address;
    }

    public void connectNode(Address address) throws IOException {
        if (!pool.containsKey(address)) {
            receivePackage(pool.put(address));
        } else {
            Start.getLogger().info("The Node( " + address + " ) had connected");
        }
    }

    public void disconnectNode(Address address) {
        pool.remove(address);
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
            try {
                ObjectInputStream ois = socket.getObjectInputStream();
                Package p = null;
                while ((!socket.isClosed()) && (p = (Package) ois.readObject()) != null) {
                    if (registerPackage.contains(p)) {
                        Start.getLogger().info("Node ( " + sockAddress + " ) had received a repeated Package from ( "
                                + p.getFrom() + " )\n" + p);
                    } else {
                        registerPackage.add(p);
                        if (getAddress().equals(p.getTo())) {
                            Start.getLogger().info("Node ( " + sockAddress + " ) had received a package to other node from ( "
                                    + p.getFrom() + " \n)" + p);
                        } else if (p.isUseful()) {
                            Start.getLogger().info("Node ( " + sockAddress + " ) had received a package  node from ( "
                                    + p.getFrom() + " \n)" + p);
                            p.countAdd();
                            sendPackage(p, sockAddress);
                        } else {
                            Start.getLogger().info("Node ( " + sockAddress + " ) had received a invalid Package"
                                    + " from ( " + p.getFrom() + " )");
                        }
                    }
                }
            } catch (SocketException e) {
                pool.remove(sockAddress);
//                System.out.println("Node receivePackage SocketException");
            } catch (ClassNotFoundException | IOException e) {
                Start.getLogger().error(e);
//                System.out.println("Node receivePackage ClassNotFoundException/IOException");
            }
        }).start();
    }

//    public boolean containsKey(Address key) {
//        return pool.containsKey(key);
//    }

    public void stop() {
        pool.removeAll();
        try {
            receiver.close();
        } catch (IOException e) {
            Start.getLogger().error(e);
//            System.out.println("error");
        }
        System.exit(0);
    }
}