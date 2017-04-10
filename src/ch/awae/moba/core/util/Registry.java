package ch.awae.moba.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

public class Registry<A> {

	private final HashMap<String, A> map = new HashMap<>();

	public void register(String name, A item) {
		map.put(name, item);
	}

	public @Nullable A get(String name) {
		return map.get(name);
	}

	public List<String> getNames() {
		ArrayList<String> list = new ArrayList<>(map.keySet());
		list.sort(String.CASE_INSENSITIVE_ORDER);
		return list;
	}
}
