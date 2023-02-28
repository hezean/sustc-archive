package darkchess.view.utils;

import darkchess.control.ResourceRefresher;
import darkchess.model.Config;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Delegate;

import java.net.URL;

@AllArgsConstructor
public enum SoundEffects {

    BGM(new PlayableMusic("background.mp3")),

    FLIP(new PlayableMusic("flip.mp3")),

    MOVE(new PlayableMusic("move.mp3")),

    CAPTURE(new PlayableMusic("capture.mp3")),
    ;

    @Delegate
    PlayableMusic music;

    public static class PlayableMusic {

        private MediaPlayer player;

        @SneakyThrows
        public PlayableMusic(String filename) {
            URL mediaFile = getClass().getClassLoader()
                    .getResource("assets/" + Config.INSTANCE.getTheme().getAssetsPath() + "/sounds/" + filename);
            Media media = new Media(mediaFile.toURI().toString());
            player = new MediaPlayer(media);
            player.setMute(Config.INSTANCE.isMuted());

            new ResourceRefresher() {
                @Override
                public void refresh() {
                    URL mediaFile = getClass().getClassLoader()
                            .getResource("assets/" + Config.INSTANCE.getTheme().getAssetsPath() + "/sounds/" + filename);
                    Media media = new Media(mediaFile.getFile());
                    boolean isPlaying = isPlaying();
                    boolean isInfLoop = player.getCycleCount() < 0;
                    player.stop();
                    player = new MediaPlayer(media);
                    player.setMute(Config.INSTANCE.isMuted());
                    if (isPlaying & isInfLoop) {
                        startInf();
                    }
                }
            }.register();
        }

        public void setMute(boolean toBe) {
            player.setMute(toBe);
        }

        public boolean isPlaying() {
            return player.getStatus().equals(MediaPlayer.Status.PLAYING);
        }

        public void startInf() {
            playFor(MediaPlayer.INDEFINITE);
        }

        public void playOnce() {
            playFor(1);
        }

        public void playFor(int times) {
            player.stop();
            player.setCycleCount(times);
            player.setStartTime(Duration.ZERO);
            player.seek(player.getStartTime());
            player.play();
        }

        public void stop() {
            player.stop();
        }
    }
}
