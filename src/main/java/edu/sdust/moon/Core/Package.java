package edu.sdust.moon.Core;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

public class Package implements Serializable {
    private int count = Start.getConfig().getPkgCount()-1;
    private String message;
    private Address from;
    private Address to;

    public Package() {
    }

    public Package(String message, Address from, Address to) {
        this.message = message;
        this.from = from;
        this.to = to;
    }

    public void countMinus() {
        count--;
    }

    public String getMessage() {
        return message;
    }

    public Address getFrom() {
        return from;
    }

    public Address getTo() {
        return to;
    }

    public boolean isUseful() {
        return count > 0;
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
        if (obj == null) { return false; }
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

    public byte[] getBytes() {
        return this.toString().getBytes(StandardCharsets.UTF_8);
    }
}
