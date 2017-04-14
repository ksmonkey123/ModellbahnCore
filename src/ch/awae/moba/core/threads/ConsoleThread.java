package ch.awae.moba.core.threads;

import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.jdt.annotation.Nullable;

import ch.awae.moba.core.model.Model;
import ch.awae.moba.core.model.Path;
import ch.awae.moba.core.util.Controllable;
import ch.awae.moba.core.util.Registries;
import ch.awae.moba.core.util.Registry;
import ch.awae.moba.core.util.Utils;

public class ConsoleThread extends Thread {

    private final Logger logger = Utils.getLogger();

    private final Level[] levels = { Level.OFF, Level.SEVERE, Level.WARNING, Level.INFO,
            Level.CONFIG, Level.FINE, Level.FINER, Level.FINEST, Level.ALL };

    private final Scanner scanner = new Scanner(System.in);

    private Model model;

    public ConsoleThread(Model model) {
        this.model = model;
        this.setDaemon(true);
    }

    @SuppressWarnings("null")
    @Override
    public void run() {
        while (true) {
            try {
                System.out.print(">");
                String command = this.scanner.nextLine();

                if (command.equals("exit"))
                    doExit();
                else if (command.equals("list threads"))
                    doList("Thread", Registries.threads);
                else if (command.startsWith("stop thread "))
                    doHalt("thread", command.substring(12), Registries.threads);
                else if (command.startsWith("start thread "))
                    doStart("thread", command.substring(13), Registries.threads);
                else if (command.equals("list operators"))
                    doList("Operator", Registries.operators);
                else if (command.startsWith("stop operator "))
                    doHalt("operator", command.substring(14), Registries.operators);
                else if (command.startsWith("start operator "))
                    doStart("operator", command.substring(15), Registries.operators);
                else if (command.equals("loglevel"))
                    System.out.println(
                            "current log level: " + this.logger.getLevel().getName());
                else if (command.equals("list loglevels"))
                    doLevelList();
                else if (command.startsWith("set loglevel "))
                    setLevel(command.substring(13));
                else if (command.equals("stop all threads"))
                    stopAll("thread", Registries.threads);
                else if (command.equals("stop all operators"))
                    stopAll("operator", Registries.operators);
                else if (command.equals("start all threads"))
                    startAll("thread", Registries.threads);
                else if (command.equals("start all operators"))
                    startAll("operator", Registries.operators);
                else if (command.equals("list all paths"))
                    listAllPaths();
                else if (command.equals("list paths"))
                    listActivePaths();
                else if (command.startsWith("path + "))
                    doRegisterPath(command.substring(7));
                else if (command.startsWith("path - "))
                    doUnregisterPath(command.substring(7));
                else if (command.equals("reboot"))
                    Utils.doReboot(this.model);
                else
                    System.out.println("unknown command: " + command);

            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private void doUnregisterPath(String substring) {
        for (Path p : Path.values()) {
            if (p.title.equals(substring)) {
                synchronized (this.model.paths) {
                    if (!this.model.paths.isRegistered(p))
                        System.out.println("path '" + p.title + "' not active");
                    else {
                        this.model.paths.unregister(p);
                        System.out.println("removing path '" + p.title + "'");
                    }
                }
                return;
            }
        }
        System.out.println("path '" + substring + "' not found");
    }

    private void doRegisterPath(String substring) {
        for (Path p : Path.values()) {
            if (p.title.equals(substring)) {
                synchronized (this.model.paths) {
                    if (this.model.paths.isRegistered(p))
                        System.out.println("path '" + p.title + "' already active");
                    else {
                        this.model.paths.register(p);
                        System.out.println("registering path '" + p.title + "'");
                    }
                }
                return;
            }
        }
        System.out.println("path '" + substring + "' not found");
    }

    private void listAllPaths() {
        System.out.println("Full Path Listing");
        System.out.println("=================");
        Path[] paths = Path.values();
        for (int i = 0; i < paths.length; i += 3) {
            Path p1 = paths[i];
            Path p2 = (i + 1) < paths.length ? paths[i + 1] : null;
            Path p3 = (i + 2) < paths.length ? paths[i + 2] : null;
            String line = strech(p1 == null ? ""
                    : ((this.model.paths.isRegistered(p1) ? " * " : "   ") + p1.title),
                    20)
                    + strech(p2 == null ? ""
                            : ((this.model.paths.isRegistered(p2) ? " * " : "   ")
                                    + p2.title),
                            20)
                    + strech(p3 == null ? ""
                            : ((this.model.paths.isRegistered(p3) ? " * " : "   ")
                                    + p3.title),
                            20);
            System.out.println(line);
        }
        System.out.println("==============");
    }

    @SuppressWarnings("null")
    private void listActivePaths() {
        System.out.println("Active Path Listing");
        System.out.println("=================");
        List<Path> paths = this.model.paths.getAllPaths();
        for (int i = 0; i < paths.size(); i += 3) {
            Path p1 = paths.get(i);
            Path p2 = (i + 1) < paths.size() ? paths.get(i) : null;
            Path p3 = (i + 2) < paths.size() ? paths.get(i) : null;
            String line = strech(p1 == null ? "" : (" * " + p1.title), 20)
                    + strech(p2 == null ? "" : (" * " + p2.title), 20)
                    + strech(p3 == null ? "" : (" * " + p3.title), 20);
            System.out.println(line);
        }
        System.out.println("==============");
    }

    @SuppressWarnings("null")
    private String strech(String string, int i) {
        String text = string;
        if (text.length() > i)
            return text.substring(0, i);
        while (text.length() < i)
            text += " ";
        return text;
    }

    private void stopAll(String title, Registry<? extends Controllable> reg) {
        for (String s : reg.getNames())
            doHalt(title, s, reg);
    }

    private void startAll(String title, Registry<? extends Controllable> reg) {
        for (String s : reg.getNames())
            doStart(title, s, reg);
    }

    private void setLevel(String level) {
        @Nullable
        Level n = null;
        if (level.startsWith("#")) {
            int index = Integer.parseInt(level.substring(1), 10);
            if (index >= 0 && index < this.levels.length)
                n = this.levels[index];
            else {
                System.out.println("no level found for index #" + index);
                return;
            }
        } else {
            for (int i = 0; i < this.levels.length; i++) {
                if (this.levels[i].getName().equals(level)) {
                    n = this.levels[i];
                    break;
                }
            }
            if (n == null) {
                System.out.println("level '" + level + "' not found");
                return;
            }
        }
        assert n != null;
        this.logger.setLevel(n);
        System.out.println("set log level " + n.getName());
    }

    private void doLevelList() {
        System.out.println("Supported Log Levels");
        System.out.println("====================");
        for (int i = 0; i < this.levels.length; i++)
            System.out.println(" #" + i + ": "
                    + (this.levels[i].equals(this.logger.getLevel()) ? ">" : " ") + " "
                    + this.levels[i].getName());
        System.out.println("====================");
    }

    @SuppressWarnings("null")
    private void doList(String title, Registry<? extends Controllable> reg) {
        System.out.println(title + " List");
        System.out.println("===================");
        List<String> names = reg.getNames();
        for (int i = 0; i < names.size(); i++) {
            System.out.println(
                    " #" + i + ": " + (reg.get(names.get(i)).isActive() ? ">" : " ") + " "
                            + names.get(i));
        }
        System.out.println("===================");
    }

    private void doStart(String title, String name,
            Registry<? extends Controllable> reg) {
        @Nullable
        String n = null;
        if (name.startsWith("#")) {
            int index = Integer.parseInt(name.substring(1), 10);
            List<String> names = reg.getNames();
            if (index >= 0 && index < names.size())
                n = names.get(index);
            else {
                System.out.println("no " + title + " found for index #" + index);
                return;
            }
        }
        if (n == null)
            n = name;
        Controllable thread = reg.get(n);
        if (thread != null) {
            try {
                thread.start();
                System.out.println("started " + title + " '" + n + "'");
            } catch (@SuppressWarnings("unused") IllegalStateException ise) {
                System.out.println(title + " '" + n + "' is already active");
            }

        } else {
            System.out.println(title + " '" + n + "' not found");
        }
    }

    private void doHalt(String title, String name, Registry<? extends Controllable> reg) {
        @Nullable
        String n = null;
        if (name.startsWith("#")) {
            int index = Integer.parseInt(name.substring(1), 10);
            List<String> names = reg.getNames();
            if (index >= 0 && index < names.size())
                n = names.get(index);
            else {
                System.out.println("no " + title + " found for index #" + index);
                return;
            }
        }
        if (n == null)
            n = name;
        Controllable thread = reg.get(n);
        if (thread != null) {
            try {
                thread.halt();
                System.out.println("halted " + title + " '" + n + "'");
            } catch (@SuppressWarnings("unused") IllegalStateException ise) {
                System.out.println(title + " '" + n + "' is already halted");
            }

        } else {
            System.out.println(title + " '" + n + "' not found");
        }
    }

    private void doExit() {
        System.out.println("stopping ...");
        for (String name : Registries.threads.getNames()) {
            try {
                doHalt("thread", name, Registries.threads);
            } catch (RuntimeException e) {
                System.out.println(e);
            }
        }
        System.exit(0);
    }

}
