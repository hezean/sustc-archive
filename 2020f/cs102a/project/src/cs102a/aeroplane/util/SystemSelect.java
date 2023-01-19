package cs102a.aeroplane.util;

public class SystemSelect {
    static final public String baseLoad = System.getProperty("user.dir");
    // 判断操作系统，适配windows和macos
    private final static String OS = System.getProperty("os.name").toLowerCase();
    private final static String macHistoryPath = "/src/cs102a/aeroplane/savegame/history/";
    private final static String windowsHistoryPath = "\\src\\cs102a\\aeroplane\\savegame\\history\\";

    private final static String macMusicPath = "/src/cs102a/aeroplane/resources/audio/";
    private final static String windowsMusicPath = "\\src\\cs102a\\aeroplane\\resources\\audio\\";

    private final static String macImagePath = "/src/cs102a/aeroplane/resources/image/";
    private final static String windowsImagePath = "\\src\\cs102a\\aeroplane\\resources\\image\\";


    public static boolean isMacOS() {
        return OS.contains("mac");
    }

    public static String getHistoryPath() {
        return baseLoad + (isMacOS() ? macHistoryPath : windowsHistoryPath);
    }


    public static String getMusicPath() {
        return baseLoad + (isMacOS() ? macMusicPath : windowsMusicPath);
    }


    public static String getImagePath() {
        return baseLoad + (isMacOS() ? macImagePath : windowsImagePath);
    }

}
