package darkchess.view.utils;

import darkchess.control.ResourceRefresher;
import darkchess.model.Chess;
import darkchess.model.Config;
import darkchess.model.utils.ChessColor;
import darkchess.model.utils.ChessPiece;
import darkchess.model.utils.ChessState;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class ChessIcon {

    private static final Map<ChessColor, Map<ChessPiece, Image>> iconCache = new EnumMap(ChessColor.class);

    private static final Map<ChessColor, Map<ChessPiece, Image>> cheatingCache = new EnumMap(ChessColor.class);

    private static Image faceDown;

    static {
        iconCache.put(ChessColor.RED, new EnumMap<>(ChessPiece.class));
        iconCache.put(ChessColor.BLACK, new EnumMap<>(ChessPiece.class));
        cheatingCache.put(ChessColor.RED, new EnumMap<>(ChessPiece.class));
        cheatingCache.put(ChessColor.BLACK, new EnumMap<>(ChessPiece.class));
    }

    static {
        new ResourceRefresher() {
            @Override
            public void refresh() {
                faceDown = null;
                iconCache.put(ChessColor.RED, new EnumMap<>(ChessPiece.class));
                iconCache.put(ChessColor.BLACK, new EnumMap<>(ChessPiece.class));
                cheatingCache.put(ChessColor.RED, new EnumMap<>(ChessPiece.class));
                cheatingCache.put(ChessColor.BLACK, new EnumMap<>(ChessPiece.class));
            }
        }.register();
    }

    @SneakyThrows
    @Nullable
    public static Image of(@Nullable Chess chess) {
        if (Objects.isNull(chess)) {
            return null;
        }
        if (chess.getState() == ChessState.FACE_DOWN) {
            if (Config.INSTANCE.isTreating()) {
                return faceDown();
            } else {
                return cheatView(chess);
            }
        }
        return faceUp(chess);
    }

    @SneakyThrows
    public static Image mixed(ChessColor color, ChessPiece piece, double pct) {
        Image icon = iconCache.get(color).get(piece);
        if (icon == null) {
            icon = new Image(Path.of(
                    Objects.requireNonNull(ChessIcon.class.getClassLoader().getResource("")).getPath(),
                    "assets",
                    Config.INSTANCE.getTheme().getAssetsPath(),
                    "icons", "chess",
                    color.getAssetPath(),
                    piece.getFilename()
            ).toUri().toString());
            iconCache.get(color).put(piece, icon);
        }
        return overlay(faceDown(), icon, Math.min(1, pct + 0.1));
    }


    @SneakyThrows
    private static Image faceUp(@NotNull Chess chess) {
        Image cachedIcon = iconCache.get(chess.getColor()).get(chess.getPiece());
        if (Objects.nonNull(cachedIcon)) {
            return cachedIcon;
        }
        Image icon = new Image(Path.of(
                Objects.requireNonNull(ChessIcon.class.getClassLoader().getResource("")).getPath(),
                "assets",
                Config.INSTANCE.getTheme().getAssetsPath(),
                "icons", "chess",
                chess.getColor().getAssetPath(),
                chess.getPiece().getFilename()
        ).toUri().toString());
        iconCache.get(chess.getColor()).put(chess.getPiece(), icon);
        return icon;
    }

    private static Image cheatView(@NotNull Chess chess) {
        Image cachedIcon = cheatingCache.get(chess.getColor()).get(chess.getPiece());
        if (Objects.nonNull(cachedIcon)) {
            return cachedIcon;
        }
        Image mix = overlay(faceDown(), faceUp(chess));
        cheatingCache.get(chess.getColor()).put(chess.getPiece(), mix);
        return mix;
    }

    @SneakyThrows
    private static Image faceDown() {
        if (Objects.isNull(faceDown)) {
            synchronized (ChessIcon.class) {
                if (Objects.isNull(faceDown)) {
                    faceDown = new Image(Path.of(
                            Objects.requireNonNull(ChessIcon.class.getClassLoader().getResource("")).getPath(),
                            "assets",
                            Config.INSTANCE.getTheme().getAssetsPath(),
                            "icons", "chess",
                            "face-down.png"
                    ).toUri().toString());
                }
            }
        }
        return faceDown;
    }

    private static Image overlay(Image image1, Image image2, double pct) {
        int width = (int) Math.max(image1.getWidth(), image2.getWidth());
        int height = (int) Math.max(image1.getHeight(), image2.getHeight());

        WritableImage res = new WritableImage(width, height);
        PixelWriter writer = res.getPixelWriter();

        PixelReader reader1 = image1.getPixelReader();
        PixelReader reader2 = image2.getPixelReader();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                double x1 = image1.getWidth() / width * x;
                double x2 = image2.getWidth() / width * x;
                double y1 = image1.getHeight() / height * y;
                double y2 = image2.getHeight() / height * y;
                Color color1 = reader1.getColor((int) x1, (int) y1);
                Color color2 = reader2.getColor((int) x2, (int) y2);
                writer.setColor(x, y, color1.interpolate(color2, pct));
            }
        }
        return res;
    }

    private static Image overlay(Image image1, Image image2) {
        return overlay(image1, image2, 0.2);
    }
}
