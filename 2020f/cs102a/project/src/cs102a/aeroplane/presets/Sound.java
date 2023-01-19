package cs102a.aeroplane.presets;

import cs102a.aeroplane.util.MusicPlayer;
import cs102a.aeroplane.util.SystemSelect;

// play(boolean)
public class Sound {
    private static final String basePath = SystemSelect.getMusicPath();

    public final static Sound JUMP = new Sound(String.format("%sJump.wav", basePath));
    public final static Sound JET = new Sound(String.format("%sFly.wav", basePath));
    public final static Sound CRACK = new Sound(String.format("%sCrack.wav", basePath));
    public final static Sound FINISH_ONE_PLANE = new Sound(String.format("%sFinish.wav", basePath));
    public final static Sound GAMING_THEME1 = new Sound(String.format("%sBGM_Auamen.wav", basePath));
    public final static Sound GAMING_THEME2 = new Sound(String.format("%sBGM_Incarnation.wav", basePath));


    MusicPlayer player;


    public Sound(String musicPath) {
        player = new MusicPlayer(musicPath);
    }

    public final void play(boolean isLoop) {
        player.setVolume(6);
        player.setLoop(isLoop);
        player.play();
        if (!isLoop) System.gc();
    }

    // 结束音乐
    public final void end() {
        player.over();
    }

    // 静音按键调用，设置bgm静音
    public final void changeIsMute() {
        if (player.getVolume() == 6) player.setVolume(-80);
        else player.setVolume(6);
    }
}
