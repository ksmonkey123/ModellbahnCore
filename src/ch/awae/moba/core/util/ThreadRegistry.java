package ch.awae.moba.core.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.threads.IThreaded;

public class ThreadRegistry {

	private static final HashMap<String, IThreaded> map = new HashMap<>();

	public static void register(String name, IThreaded thread) {
		map.put(name, thread);
	}

	public static @Nullable IThreaded getThread(String name) {
		return map.get(name);
	}

	public static List<String> getNames() {
		ArrayList<String> list = new ArrayList<>(map.keySet());
		list.sort(String.CASE_INSENSITIVE_ORDER);
		return list;
	}

}
