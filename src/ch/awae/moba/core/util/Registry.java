package ch.awae.moba.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Registry<A> {

    private final HashMap<String, A> map = new HashMap<>();

    public void register(String name, A item) {
        this.map.put(name, item);
    }

    public A get(String name) {
        return this.map.get(name);
    }

    public List<String> getNames() {
        ArrayList<String> list = new ArrayList<>(this.map.keySet());
        list.sort(String.CASE_INSENSITIVE_ORDER);
        return list;
    }

}
