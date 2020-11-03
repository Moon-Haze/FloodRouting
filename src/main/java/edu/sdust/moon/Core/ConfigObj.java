package edu.sdust.moon.Core;

import java.util.ArrayList;

public class ConfigObj {
    private String name ="name";
    private Address localAddress=new Address ("127.0.0.1",6868);
    private ArrayList<Address> nodes=new ArrayList<> ();

    public String getName () {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }

    public Address getLocalAddress () {
        return localAddress;
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
    public ConfigObj(){
        nodes.add (new Address ("127.0.0.1",8686));
    }
}
