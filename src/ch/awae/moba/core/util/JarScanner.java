package ch.awae.moba.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import ch.awae.moba.core.operators.IOperator;

public class JarScanner {

	private static final Logger logger = Utils.getLogger();

	public static List<String> scanJarFile(String path) throws IOException, InterruptedException {
		List<String> output = new ArrayList<>();
		logger.info("reading jar toc");
		ProcessBuilder pb = new ProcessBuilder("/usr/bin/jar", "tf", path);
		pb.redirectErrorStream(true);
		Process p = pb.start();
		// p.waitFor(); // wait for process to finish then continue.
		BufferedReader bri = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line;
		while ((line = bri.readLine()) != null) {
			logger.fine(" > " + line);
			output.add(line);
		}
		return output;
	}

	@SuppressWarnings("null")
	public static List<Class<?>> scanJarForClasses(String path, String pkg) throws IOException, InterruptedException {
		List<String> raw = scanJarFile(path);
		List<Class<?>> output = new ArrayList<>();
		logger.info("searching classes");
		for (String line : raw) {
			if (line.endsWith(".class")) {
				String cname = line.substring(0, line.length() - 6).replace('/', '.');
				if (cname.startsWith(pkg)) {
					try {
						output.add(JarScanner.class.getClassLoader().loadClass(cname));
					} catch (ClassNotFoundException e) {
						logger.severe(e.toString());
					}
				}
			}
		}
		return output;
	}

	public static void loadOperators(String path, String pkg) throws IOException, InterruptedException {
		List<Class<?>> raw = scanJarForClasses(path, pkg);
		for (Class<?> c : raw) {
			if (IOperator.class.isAssignableFrom(c)) {
				try {
					c.getConstructor().newInstance();
					logger.info("loaded operator " + c.getName());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException | NoSuchMethodException | SecurityException e) {
					logger.severe(e.toString());
				}
			}
		}
	}

}
