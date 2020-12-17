package edu.sdust.moon.Core;

import java.util.*;

public class TimeHashSet {
    private HashMap<Long, Packet> map = new HashMap<>();

    public TimeHashSet() {
        new Thread(() -> {
            while (true) {
                long nowTime =new Date(System.currentTimeMillis()).getTime();
                for (long item: map.keySet()){
                    if (nowTime-item>Start.getConfig().getPkgLife()){
                        map.remove(item);
                        Start.getLogger().info("delete a package, key is "+item);
                    }
                }
            }
        }).start();
    }

    public void add(Packet value) {
        map.put(new Date(System.currentTimeMillis()).getTime(), value);
    }

    public boolean contains(Packet p) {
        return  map.containsValue(p);
    }
}
