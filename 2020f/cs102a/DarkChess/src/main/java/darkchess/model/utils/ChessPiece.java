package darkchess.model.utils;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ChessPiece {

    GENERAL("general.png", 30, 1, 6),

    ADVISOR("advisor.png", 10, 2, 5),

    MINISTER("minister.png", 5, 2, 4),

    CHARIOT("chariot.png", 5, 2, 3),

    HORSE("horse.png", 5, 2, 2),

    SOLDIER("soldier.png", 1, 5, 1),

    CANNON("cannon.png", 5, 2, 0),
    ;

    private String filename;

    private int points;

    private int number;

    @Getter(AccessLevel.NONE)
    private int priority;

    public boolean canCapture(ChessPiece op) {
        if (op == GENERAL) {
            return this == SOLDIER;
        }
        if (this == CANNON) {
            return true;
        }
        if (op == CANNON) {
            return this != SOLDIER;
        }
        return this.priority >= op.priority;
    }
}
