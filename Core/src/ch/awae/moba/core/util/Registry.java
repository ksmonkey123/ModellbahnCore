package ch.awae.moba.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;

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

    public A find(Predicate<A> test) {
        for (A e : this.map.values()) {
            if (test.test(e))
                return e;
        }
        return null;
    }
}
