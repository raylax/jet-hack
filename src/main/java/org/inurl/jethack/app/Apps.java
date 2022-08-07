package org.inurl.jethack.app;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Apps {

    private static int READ_BUF_SIZE = 4096;

    private static final List<AppPath> LIST = new ArrayList<>();

    static {
        LIST.add(new AppPath("IntelliJ IDEA", "IntelliJIdea", "idea.vmoptions"));
        LIST.add(new AppPath("CLion", "CLion", "clino.vmoptions"));
        LIST.add(new AppPath("GoLand", "GoLand", "goland.vmoptions"));
        LIST.add(new AppPath("PyCharm", "PyCharm", "pycharm.vmoptions"));
        LIST.add(new AppPath("WebStorm", "WebStorm", "webstorm.vmoptions"));
    }

    public static List<AppPath> getList() {
        return LIST;
    }

    public static class AppPath {
        public String name;
        public String configDir;
        public String configFile;

        public AppPath(String name, String configDir, String configFile) {
            this.name = name;
            this.configDir = configDir;
            this.configFile = configFile;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return name.equals(obj);
        }
    }

    public static String readFileToString(File file) {
        try (InputStream in = new BufferedInputStream(new FileInputStream(file))) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[READ_BUF_SIZE];
            int n;
            while ((n = in.read(buf)) != -1) {
                out.write(buf, 0, n);
            }
            return out.toString("UTF-8");
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void writeStringToFile(File file, String content) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            out.write(content.getBytes(StandardCharsets.UTF_8));
            out.flush();
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
