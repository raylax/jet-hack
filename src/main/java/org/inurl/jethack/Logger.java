package org.inurl.jethack;

public class Logger {

    public static void info(String format, Object... args) {
        print("I", format, args);
    }

    public static void fatal(String format, Object... args) {
        print("F", format, args);
        throw new RuntimeException(String.format(format, args));
    }

    public static void error(String format, Object... args) {
        print("E", format, args);
    }

    private static synchronized void print(String level, String format, Object... args) {
        System.out.printf("[***] [%s] - %s%n", level, String.format(format, args));
    }

}
