package ch.awae.moba.core.threads;

import java.util.List;
import java.util.Scanner;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.util.ThreadRegistry;

public class ConsoleThread extends Thread {

	private final Scanner scanner = new Scanner(System.in);

	@SuppressWarnings("null")
	@Override
	public void run() {
		while (true) {
			try {
				String command = scanner.nextLine();

				if (command.equals("exit"))
					doExit();
				else if (command.equals("list threads"))
					doThreadList();
				else if (command.startsWith("stop thread "))
					doThreadStop(command.substring(12));
				else if (command.startsWith("start thread "))
					doThreadStart(command.substring(13));
				else
					System.out.println("unknown command: " + command);

			} catch (Exception e) {
				System.out.println(e);
			}
		}
	}

	private void doThreadList() {
		System.out.println("Thread List");
		System.out.println("===========");
		List<String> names = ThreadRegistry.getNames();
		for (int i = 0; i < names.size(); i++) {
			System.out.println(" #" + i + ": " + names.get(i));
		}
		System.out.println("===========");
	}

	private void doThreadStart(String name) {
		@Nullable
		String n = null;
		if (name.startsWith("#")) {
			int index = Integer.parseInt(name.substring(1), 10);
			List<String> names = ThreadRegistry.getNames();
			if (index >= 0 && index < names.size())
				n = names.get(index);
			else {
				System.out.println("no thread found for index #" + index);
				return;
			}
		}
		if (n == null)
			n = name;
		IThreaded thread = ThreadRegistry.getThread(n);
		if (thread != null) {
			try {
				thread.start();
				System.out.println("started thread '" + n + "'");
			} catch (IllegalStateException ise) {
				System.out.println("thread '" + n + "' is already running");
			}

		} else {
			System.out.println("thread '" + n + "' not found");
		}
	}

	private void doThreadStop(String name) throws InterruptedException {
		@Nullable
		String n = null;
		if (name.startsWith("#")) {
			int index = Integer.parseInt(name.substring(1), 10);
			List<String> names = ThreadRegistry.getNames();
			if (index >= 0 && index < names.size())
				n = names.get(index);
			else {
				System.out.println("no thread found for index #" + index);
				return;
			}
		}
		if (n == null)
			n = name;
		IThreaded thread = ThreadRegistry.getThread(n);
		if (thread != null) {
			try {

				thread.stop();
				System.out.println("stopped thread '" + n + "'");
			} catch (IllegalStateException ise) {
				System.out.println("thread '" + n + "' is already halted");
			}

		} else {
			System.out.println("thread '" + n + "' not found");
		}
	}

	private void doExit() {
		System.out.println("stopping ...");
		for (String name : ThreadRegistry.getNames()) {
			try {
				doThreadStop(name);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.exit(0);
	}

}
