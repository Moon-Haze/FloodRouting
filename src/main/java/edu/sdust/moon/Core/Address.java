package edu.sdust.moon.Core;

import java.io.Serializable;

public class Address implements Serializable {
    private String ip;
    private int port;

    public static Address createAddress(String path) {
        String[] ad = path.split(":");
        return new Address(ad[0], Integer.parseInt(ad[1]));
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Address() {
    }

    public Address(String host, int port) {
        this.ip = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Address address = (Address) obj;
        return this.ip.equals(address.ip) && this.port == address.port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
