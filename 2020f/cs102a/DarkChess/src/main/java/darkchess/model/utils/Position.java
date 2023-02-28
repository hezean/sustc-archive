package darkchess.model.utils;

import java.util.stream.Stream;

public record Position(int row, int col) {

    public Stream<Position> surroundings(int step) {
        return Stream.of(
                new Position(row - step, col),
                new Position(row + step, col),
                new Position(row, col - step),
                new Position(row, col + step)
        ).filter(Position::isValid);
    }

    private boolean isValid() {
        return row >= 0 && col >= 0 && row < Constants.BOARD_ROWS && col < Constants.BOARD_COLS;
    }
}
