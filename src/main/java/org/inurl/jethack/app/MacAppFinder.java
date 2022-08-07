package org.inurl.jethack.app;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MacAppFinder implements AppFinder {

    private static final Pattern PLIST_EXE_REGEX = Pattern.compile(
            "<key>idea\\.executable<\\/key>\\s+<string>(?<exe>.*?)<\\/string>.*?");
    private static final Pattern PLIST_PATH_REGEX = Pattern.compile(
            "<key>idea\\.paths\\.selector<\\/key>\\s+<string>(?<path>.*?)<\\/string>");
    private static final Pattern PLIST_VERSION_REGEX = Pattern.compile(
            "<key>CFBundleShortVersionString<\\/key>\\s+<string>(?<version>.*?)<\\/string>");

    private static final String APP_PATH = "/Applications";
    private static final String APP_PLIST_PATH = "Contents/Info.plist";
    private static final String APP_CONFIG_PATH = System.getProperty("user.home") + "/Library/Application Support/JetBrains";

    @Override
    public List<App> list() {
        File appsDir = new File(APP_PATH);
        File[] files = appsDir.listFiles();
        if (files == null) {
            return null;
        }
        List<Apps.AppPath> apps = Apps.getList();
        List<App> result = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();
            if (!name.endsWith(".app")) {
                continue;
            }
            name = name.substring(0, name.length() - 4);
            for (Apps.AppPath app : apps) {
                if (app.name.equals(name)) {
                    result.add(getAppPath(app, file));
                    break;
                }
            }
        }
        return result;
    }

    private static App getAppPath(Apps.AppPath path, File file) {
        String plist = Apps.readFileToString(new File(file, APP_PLIST_PATH));
        Matcher exeMatcher = PLIST_EXE_REGEX.matcher(plist);
        Matcher pathMatcher = PLIST_PATH_REGEX.matcher(plist);
        Matcher versionMatcher = PLIST_VERSION_REGEX.matcher(plist);
        if (!exeMatcher.find() || !pathMatcher.find() || !versionMatcher.find()) {
            throw new RuntimeException("Can not resole plist [" + path.name + "]");
        }
        return new App(
                path.name,
                APP_CONFIG_PATH + "/" +
                    pathMatcher.group("path") + "/" +
                    exeMatcher.group("exe") + ".vmoptions",
                versionMatcher.group("version")
        );
    }

}
