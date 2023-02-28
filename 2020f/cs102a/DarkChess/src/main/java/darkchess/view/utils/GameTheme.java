package darkchess.view.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@AllArgsConstructor
public enum GameTheme {

    DEFAULT("default", "#f6e0b5", "#a5822a"),

    CHILL("chill", "#f6e0b5", "#f6e0b5"),

    MYSTERY("mystery", "#f6e0b5", "#f6e0b5"),
    ;

    private String assetsPath;

    private String chessboardGridColor;

    private String chessboardGapColor;

    public String getName() {
        return StringUtils.capitalize(assetsPath);
    }
}
