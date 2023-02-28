package darkchess.model.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChessColor {

    RED("red"),

    BLACK("black"),
    ;

    private String assetPath;

    public ChessColor opponent() {
        return this == RED ? BLACK : RED;
    }
}
