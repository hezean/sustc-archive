package darkchess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import darkchess.control.GUIRefresher;
import darkchess.control.ResourceRefresher;
import darkchess.view.utils.GameTheme;
import darkchess.view.utils.SoundEffects;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

@Slf4j
@Getter
@ToString
public final class Config {

    @NonNull
    Player player1 = Player.builder().name("Player1").build();

    @NonNull
    Player player2 = Player.builder().name("Player2").build();

    private GameTheme theme = GameTheme.DEFAULT;

    @Getter
    private boolean muted = false;

    @JsonIgnore
    private boolean isTreating;

    public void setMuted(boolean muted) {
        if (this.muted == muted) {
            return;
        }
        this.muted = muted;
        for (SoundEffects se : SoundEffects.values()) {
            se.setMute(muted);
        }
        ResourceRefresher.refreshAll();
    }

    public void setTreating(boolean treating) {
        isTreating = treating;
        ResourceRefresher.refreshAll();
        GUIRefresher.refreshAll();
    }

    public void setTheme(GameTheme theme) {
        this.theme = theme;
        ResourceRefresher.refreshAll();
    }

    @JsonIgnore
    private static final ObjectMapper mapper = YAMLMapper.builder()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .enable(YAMLGenerator.Feature.MINIMIZE_QUOTES)
            .enable(YAMLGenerator.Feature.INDENT_ARRAYS_WITH_INDICATOR)
            .build();

    @JsonIgnore
    private static final String SAVE_DIRNAME = "config";

    @JsonIgnore
    private static final String DEFAULT_CONFIG_FILENAME = "default.yml";

    @JsonIgnore
    public static Config INSTANCE = Config.load(DEFAULT_CONFIG_FILENAME);

    static {
        Runtime.getRuntime().addShutdownHook(new Thread(Config.INSTANCE::save, "ConfigSaver"));
    }

    private Config() {
    }

    private static Config load(String filename) {
        Config ctx;
        try {
            File file = Path.of(Objects.requireNonNull(Config.class.getClassLoader().getResource("")).getPath(), SAVE_DIRNAME, filename).toFile();
            ctx = mapper.readValue(file, Config.class);
            log.info("load saved {} successfully", ctx);
        } catch (FileNotFoundException e) {
            log.info("creating new config");
            ctx = new Config();
            ctx.save();
        } catch (IOException | IllegalArgumentException e) {
            log.error("cannot read saved context from '{}'", filename, e);
            ctx = new Config();
            ctx.save();
        }
        return ctx;
    }

    public static void reset() {
        String backUpName = String.format("%s.%d.bak", DEFAULT_CONFIG_FILENAME, System.currentTimeMillis());
        INSTANCE.saveAs(backUpName);
        INSTANCE = new Config();
        INSTANCE.save();
    }

    @SneakyThrows
    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void saveAs(String filename) {
        File file = Path.of(Objects.requireNonNull(this.getClass().getClassLoader().getResource("")).getPath(), SAVE_DIRNAME, filename).toFile();
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        mapper.writeValue(file, this);
        log.info("saved {} to file {}", this, file);
    }

    private void save() {
        saveAs(DEFAULT_CONFIG_FILENAME);
    }
}
