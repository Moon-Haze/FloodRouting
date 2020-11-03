package edu.sdust.moon.Core;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class StreamSocket {
    private final Socket socket;
    private final ObjectOutputStream objectOutputStream;


    private final ObjectInputStream objectInputStream;
    private Address address;

    public StreamSocket(Address address) throws IOException {
        socket = new Socket(address.getHost(), address.getPort());
        this.address = address;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectOutputStream.writeObject(Start.getConfig().getLocalAddress());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
    }

    public StreamSocket(Socket socket) throws IOException {
        this.socket = socket;
        objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
        objectInputStream = new ObjectInputStream(socket.getInputStream());
        try {
            address = (Address) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Socket getSocket() {
        return socket;
    }

    public ObjectInputStream getObjectInputStream() {
        return objectInputStream;
    }

    public ObjectOutputStream getObjectOutputStream() {
        return objectOutputStream;
    }

    public Address getAddress() {
        return address;
    }

    public synchronized void close() throws IOException {
        objectInputStream.close();
        objectOutputStream.close();
        socket.close();
    }

    public boolean isClosed() {
        return socket.isClosed();
    }

}