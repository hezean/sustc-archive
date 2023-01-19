package cs102a.aeroplane;

import cs102a.aeroplane.online.Client;
import cs102a.aeroplane.online.Server;

public class GameInfo {

    private static final String serverIp = null;
    private static final String[] playerName = {"A", "B", "C", "D"};
    public static Client localClient;
    public static Server localServer = new Server();
    private static boolean superUser = false;
    private static boolean isCheatMode = false;
    private static int theme = 1;
    private static int HumanPlayerCnt = 4;
    private static boolean isOnlineGame = false;

    public static String[] getPlayerName() {
        return playerName;
    }

    public static boolean isSuperUser() {
        return superUser;
    }

    public static void setSuperUser(boolean superUser) {
        GameInfo.superUser = superUser;
    }

    public static boolean isIsCheatMode() {
        return isCheatMode;
    }

    public static void setIsCheatMode(boolean isCheatMode) {
        GameInfo.isCheatMode = isCheatMode;
    }

    public static int getHumanPlayerCnt() {
        return HumanPlayerCnt;
    }

    public static void setHumanPlayerCnt(int humanPlayerCnt) {
        HumanPlayerCnt = humanPlayerCnt;
    }

    public static boolean isIsOnlineGame() {
        return isOnlineGame;
    }

    public static void setIsOnlineGame(boolean isOnlineGame) {
        GameInfo.isOnlineGame = isOnlineGame;
    }

    public static int getTheme() {
        return theme;
    }

    public static void setTheme(int theme) {
        GameInfo.theme = theme;
    }
}
