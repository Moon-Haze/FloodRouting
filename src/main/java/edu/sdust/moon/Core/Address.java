package edu.sdust.moon.Core;

import java.io.Serializable;

public class Address implements Serializable {
    private String host;
    private int port;

    public static Address createAddress(String path) {
        String[] ad = path.split(":");
        return new Address(ad[0], Integer.parseInt(ad[1]));
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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
        this.host = host;
        this.port = port;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Address address = (Address) obj;
        return this.host.equals(address.host) && this.port == address.port;
    }

    @Override
    public String toString() {
        return host + ":" + port;
    }
}
