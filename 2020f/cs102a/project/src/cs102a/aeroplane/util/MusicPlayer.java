package cs102a.aeroplane.util;

import javax.sound.sampled.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MusicPlayer {

    private final File file;            // wav文件的路径
    private boolean isLoop = false;     // 是否循环播放
    private boolean isPlaying;          // 是否正在播放
    private int volume = 6;             // FloatControl.Type.MASTER_GAIN的值(可用于调节音量)

    private playSoundThread playSoundThread;

    public MusicPlayer(String musicPath) {
        this.file = new File(musicPath);
        playSoundThread = new playSoundThread();
    }

    // 播放音乐
    public void play() {
        playSoundThread = new playSoundThread();
        playSoundThread.start();
    }

    // 结束音乐
    public void over() {
        isPlaying = false;
        if (playSoundThread != null) {
            playSoundThread.stop();
            System.gc();
        }
    }

    // 设置循环播放
    public void setLoop(boolean isLoop) {
        this.isLoop = isLoop;
    }

    public float getVolume() {
        return volume;
    }

    // -80.0~6测试,越小音量越小
    public void setVolume(int volume) {
        this.volume = volume;
    }

    // 异步播放线程
    private class playSoundThread extends Thread {

        @Override
        public void run() {
            isPlaying = true;
            do {
                SourceDataLine sourceDataLine = null;
                BufferedInputStream bufferedInputStream = null;
                AudioInputStream audioInputStream = null;
                try {
                    bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
                    audioInputStream = AudioSystem.getAudioInputStream(bufferedInputStream);

                    AudioFormat format = audioInputStream.getFormat();
                    sourceDataLine = AudioSystem.getSourceDataLine(format);
                    sourceDataLine.open();

                    FloatControl control = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                    control.setValue(volume);

                    sourceDataLine.start();
                    byte[] buf = new byte[512];
                    int len;
                    while (isPlaying && (len = audioInputStream.read(buf)) != -1) {
                        sourceDataLine.write(buf, 0, len);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {

                    if (sourceDataLine != null) {
                        sourceDataLine.drain();
                        sourceDataLine.close();
                    }
                    try {
                        if (bufferedInputStream != null) {
                            bufferedInputStream.close();
                        }
                        if (audioInputStream != null) {
                            audioInputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } while (isPlaying && isLoop);
        }
    }
}
