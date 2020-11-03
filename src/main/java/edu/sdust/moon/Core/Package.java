package edu.sdust.moon.Core;

import java.io.Serializable;

public class Package implements Serializable {
    private static int upperLimit = 10;
    private int count = 0;
    private String message;
    private Address prevNode;
    private Address from;
    private Address to;

    public void countAdd() {
        count++;
    }

    public String getMessage() {
        return message;
    }

    public Address getPrevNode() {
        return prevNode;
    }

    public void setPrevNode(Address prevNode) {
        this.prevNode = prevNode;
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
    }

    public boolean isUseful() {
        return count <= upperLimit;
    }

    public static void setUpperLimit(int upperLimit) {
        Package.upperLimit = upperLimit;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setFrom(Address from) {
        this.from = from;
    }

    public void setTo(Address to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        Package p = (Package) obj;
        return this.to.equals(p.to) && this.from.equals(p.from) && this.message.equals(p.message);
    }

    @Override
    public String toString() {
        return "Package{" +
                "count=" + count +
                ", message: \"" + message + "\"" +
                ", from " + from +
                ", to " + to +
                '}';
    }
}
