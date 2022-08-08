package org.inurl.jethack;

import org.inurl.jethack.app.App;
import org.inurl.jethack.app.Apps;
import org.inurl.jethack.app.MacAppFinder;

import java.io.File;
import java.lang.instrument.Instrumentation;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarFile;

public class Jethack {
    public static final String VERSION = "1.2";
    private static final String FLAG_FILE = "/where_is_jar.flag";
    public static String SELF_PATH;
    private static final String CONFIG_MARK = "### JET-HACK ###";

    static {
        SELF_PATH = getJarFile().getPath();
    }

    public static void main(String[] args) throws Exception {
        printBanner();
        if (!System.getProperty("os.name").contains("Mac")) {
            System.out.printf("Add line [-javaagent:%s] to enable%n", SELF_PATH, SELF_PATH);
            return;
        }
        List<App> apps = new MacAppFinder().list();
        if (apps == null || apps.size() == 0) {
            return;
        }
        for (int i = 0; i < apps.size(); i++) {
            App app = apps.get(i);
            System.out.printf("[%d] %s [%s] - %s%n", i + 1, String.format("%-14s", app.name), app.version, app.configPath);
        }
        System.out.printf("%nFound %s app(s), please type [1-%d]%n", apps.size(), apps.size());
        int n = new Scanner(System.in).nextInt();
        if (n < 0 || n > apps.size()) {
            System.err.println("Type error [" + n + "]");
            System.exit(0);
        }
        App app = apps.get(n - 1);
        File configFile = new File(app.configPath);
        if (configFile.exists()) {
            List<String> lines = new ArrayList<>();
            String[] items = Apps.readFileToString(configFile).split("\n");
            boolean update = false;
            for (int i = 0; i < items.length; i++) {
                if (items[i].startsWith("-javaagent:")) {
                    if (i > 0 && items[i - 1].startsWith(CONFIG_MARK)) {
                        lines.add("-javaagent:" + SELF_PATH);
                        update = true;
                    } else {
                        lines.add("# " + items[i]);
                    }
                } else {
                    lines.add(items[i]);
                }
            }
            if (!update) {
                lines.add(getConfigContent());
            }
            Apps.writeStringToFile(configFile, String.join("\n", lines));
        } else {
            Apps.writeStringToFile(configFile, getConfigContent());
        }
        System.out.printf("%nConfig updated [%s]%n", app.configPath);
        System.out.println("Enjoy it ~");
    }

    public static void premain(String args, Instrumentation inst) throws Exception {
        printBanner();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(SELF_PATH));
        inst.addTransformer(new JethackClassFileTransformer(), true);
    }

    private static String getConfigContent() {
        return "\n" + CONFIG_MARK + "\n" + "-javaagent:" + SELF_PATH + "\n" + CONFIG_MARK + "\n";
    }

    private static URI getJarFile() {
        URL url = Jethack.class.getResource(FLAG_FILE);
        if (url == null) {
            Logger.fatal("Can not locate resource file");
        }
        String path = url.getPath();
        if (!path.endsWith("!" + FLAG_FILE)) {
            Logger.fatal("Can not locate resource file");
        }

        path = path.substring(0, path.length() - FLAG_FILE.length() - 1);
        try {
            return new URI(path);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static void printBanner() {
        System.out.println(
            "\n" +
            "       ______________  __  _____   ________ __\n" +
            "      / / ____/_  __/ / / / /   | / ____/ //_/\n" +
            " __  / / __/   / /___/ /_/ / /| |/ /   / ,<   \n" +
            "/ /_/ / /___  / /___/ __  / ___ / /___/ /| |         by: raylax\n" +
            "\\____/_____/ /_/   /_/ /_/_/  |_\\____/_/ |_|    version: " + VERSION + "\n"
        );
    }

}