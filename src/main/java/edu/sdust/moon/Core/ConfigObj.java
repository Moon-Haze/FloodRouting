package edu.sdust.moon.Core;

import java.util.ArrayList;

public class ConfigObj {

    private String name ="name";
    private long pkgLife =60*1000;
    private int pkgCount=10;
    private Address localAddress=new Address ("127.0.0.1",6868);
    private ArrayList<Address> nodes=new ArrayList<> ();

    public String getName () {
        return name;
    }

    public Address getLocalAddress () {
        return localAddress;
    }

    public int getPkgCount() {
        return pkgCount;
    }

    public void setPkgCount(int pkgCount) {
        this.pkgCount = pkgCount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLocalAddress (Address localAddress) {
        this.localAddress = localAddress;
    }

    public ArrayList<Address> getNodes () {
        return nodes;
    }

    public void setNodes (ArrayList<Address> nodes) {
        this.nodes = nodes;
    }

    public long getPkgLife() {
        return pkgLife;
    }

    public void setPkgLife(long pkgLife) {
        this.pkgLife = pkgLife;
    }

    public ConfigObj(){
        nodes.add (new Address ("127.0.0.1",8686));
    }
}
