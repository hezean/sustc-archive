package darkchess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import darkchess.control.GUIRefresher;
import darkchess.model.utils.*;
import darkchess.view.components.ChessView;
import darkchess.view.utils.SoundEffects;
import javafx.scene.control.Alert;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.management.InvalidAttributeValueException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

@Data
@Slf4j
public class Chessboard {

    @JsonIgnore
    private static final ObjectMapper mapper = new ObjectMapper();

    @NonNull
    private Chess[][] chessboard = new Chess[Constants.BOARD_ROWS][Constants.BOARD_COLS];

    @ToString.Exclude
    private List<Chess> downBoard = new LinkedList<>();

    @NonNull
    private Integer step = 0;

    @Nullable
    private ChessColor firstPlayerColor;

    private String p1Name;

    private String p2Name;

    private int p1Score;

    private int p2Score;

    private Stack<Snapshot> history = new Stack<>();

    private Stack<Snapshot> undoHistory = new Stack<>();

    public static Chessboard newGame(String p1, String p2) {
        ChessView.getViews().clear();
        Chessboard mod = new Chessboard();
        mod.p1Name = p1;
        mod.p2Name = p2;
        List<Chess> chessMods = new ArrayList<>(Constants.BOARD_ROWS * Constants.BOARD_COLS);
        for (ChessColor color : ChessColor.values()) {
            for (ChessPiece piece : ChessPiece.values()) {
                for (int i = 0; i < piece.getNumber(); i++) {
                    Chess chess = Chess.builder()
                            .color(color)
                            .piece(piece)
                            .identifier(i)
                            .state(ChessState.FACE_DOWN)
                            .build();
                    chessMods.add(chess);
                }
            }
        }
        Collections.shuffle(chessMods);
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                mod.chessboard[r][c] = chessMods.get(r * Constants.BOARD_COLS + c);
            }
        }
        mod.backupHistoryForOperation();
        return mod;
    }

    public static Chessboard recoverFrom(File file) throws IOException, InvalidAttributeValueException {
        Chessboard res;
        try {
            res = mapper.readValue(file, Chessboard.class);
            log.info("read from file  {}", res);
        } catch (DatabindException e) {
            throw new InvalidAttributeValueException("[error 101] Wrong data format: " + e.getMessage());
        }
        return res.verified();
    }

    public void saveTo(File file) throws IOException {
        mapper.writerWithDefaultPrettyPrinter().writeValue(file, this);
    }

    @SneakyThrows
    public void recoverFrom(Snapshot snapshot) {
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                chessboard[r][c] = snapshot.chessboard[r][c];
            }
        }
        downBoard = new LinkedList<>(snapshot.downBoard);
        p1Score = snapshot.p1score;
        p2Score = snapshot.p2score;
        step = snapshot.step;
        if (step == 0) {
            firstPlayerColor = null;
        }
        step += 1;
    }

    private Chessboard verified() throws InvalidAttributeValueException {
        if (Objects.isNull(chessboard)) {
            throw new InvalidAttributeValueException("[error 102] Chessboard is missing");
        }
        if (chessboard.length != 8) {
            throw new InvalidAttributeValueException("[error 102] Invalid chessboard, expected 8*4, got " + chessboard.length + "*n");
        }
        for (int i = 0; i < 8; i++) {
            if (chessboard[i].length != 4) {
                throw new InvalidAttributeValueException("[error 102] Invalid chessboard, " + i + "th row should have 4 cols, but got " + chessboard[i].length);
            }
        }
        List<Chess> ccs = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                ccs.add(chessboard[i][j]);
            }
        }
        ccs.addAll(downBoard);
        Map<ChessColor, Map<ChessPiece, Integer>> res = new EnumMap<>(ChessColor.class);
        res.put(ChessColor.BLACK, new EnumMap<>(ChessPiece.class));
        res.put(ChessColor.RED, new EnumMap<>(ChessPiece.class));
        for (var c : ccs) {
            if (c == null) {
                continue;
            }
            if (c.getColor() == null || c.getPiece() == null) {
                throw new InvalidAttributeValueException("[error 103] Chess error");
            }
        }
        ccs.stream()
                .filter(Objects::nonNull)
                .forEach(c ->
                        res.get(c.getColor()).put(
                                c.getPiece(),
                                res.get(c.getColor()).computeIfAbsent(c.getPiece(), chessPiece -> 0) + 1
                        )
                );
        for (var pcs : res.values()) {
            for (var p : ChessPiece.values()) {
                if (pcs.get(p) != p.getNumber()) {
                    throw new InvalidAttributeValueException("[error 103] Chess error");
                }
            }
        }
        if (firstPlayerColor == null && step != 0) {
            throw new InvalidAttributeValueException("[error 104] Don't know the current player");
        }

        for (int i = 0; i < history.size() - 1; i++) {
            if (!canTransfer(history.get(i), history.get(i + 1), firstPlayerColor)) {
                throw new InvalidAttributeValueException("[error 105] Step " + i + " is illegal");
            }
        }

        return this;
    }

    private boolean canTransfer(Snapshot from, Snapshot next, @NotNull ChessColor first) {
        var currentPlayer = from.step % 2 == 0 ? first : first.opponent();
        Position diff = null;
        Position diffTo = null;
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 4; j++) {
                var f = from.chessboard[i][j];
                var t = next.chessboard[i][j];
                if (f != null && f.equals(t) && f.getState() == t.getState()) {
                } else {
                    if (diff != null) {
                        if (t == null && f != null) {
                            diffTo = diff;
                            diff = new Position(i, j);
                        }
                    } else {
                        diff = new Position(i, j);
                    }
                }
            }
        }
        if (diff == null) {
            return false;
        }
        if(diffTo==null){
            return true;  // single flip
        }
        if(from.chessboard[diff.row()][diff.col()].getColor()!=currentPlayer) {
            return false;
        }
        return from.chessboard[diff.row()][diff.col()].getPiece().canCapture(next.chessboard[diffTo.row()][diffTo.col()].getPiece());
    }

    public boolean canUndo() {
        return history.size() > 1;
    }

    public boolean canRedo() {
        return !undoHistory.empty();
    }

    public void undo() {
        undoHistory.push(history.pop());
        recoverFrom(history.peek());
    }

    public void redo() {
        Snapshot lastUndo = undoHistory.pop();
        this.recoverFrom(lastUndo);
        history.push(lastUndo);
    }

    public void backupHistoryForOperation() {
        history.push(new Snapshot(this));
        log.info("backing up step {}, len(history)={}", history.peek().step, history.size());
        step += 1;
        undoHistory.clear();
    }

    public Optional<Chess> selectedChess() {
        return Arrays.stream(chessboard)
                .flatMap(Arrays::stream)
                .filter(c -> ChessView.of(c).isSelecting())
                .findFirst();
    }

    @Nullable
    public Position selectedChessPos() {
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                var chs = chessboard[r][c];
                if (ChessView.of(chs).isSelecting()) {
                    return new Position(r, c);
                }
            }
        }
        return null;
    }

    public Optional<Position> positionOf(@NotNull Chess chess) {
        for (int r = 0; r < Constants.BOARD_ROWS; r++) {
            for (int c = 0; c < Constants.BOARD_COLS; c++) {
                if (chess.equals(chessboard[r][c])) {
                    return Optional.of(new Position(r, c));
                }
            }
        }
        return Optional.empty();
    }

    public void doMove(Position to) {
        tryTerminateGame();
        Chess sel = selectedChess().get();
        Position selPos = selectedChessPos();
        if (chessboard[to.row()][to.col()] != null) {
            downBoard.add(chessboard[to.row()][to.col()]);
            SoundEffects.CAPTURE.playOnce();
            if (chessboard[to.row()][to.col()].getColor() == sel.getColor().opponent()) {
                if (firstPlayerColor == sel.getColor()) {
                    p1Score += chessboard[to.row()][to.col()].getPiece().getPoints();
                } else {
                    p2Score += chessboard[to.row()][to.col()].getPiece().getPoints();
                }
            }
        } else {
            SoundEffects.MOVE.playOnce();
        }
        chessboard[to.row()][to.col()] = sel;
        chessboard[selPos.row()][selPos.col()] = null;
        ChessView.of(sel).unselect();
        backupHistoryForOperation();
        GUIRefresher.refreshAll();
        tryTerminateGame();
    }

    public void tryTerminateGame() {
        if (p1Score < 60 && p2Score < 60) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Game end");
        if (p1Score > p2Score) {
            alert.setContentText("[P1: " + p1Score + "pts] " + p1Name + " win against [P2: " + p2Score + "pts] " + p2Name);
        } else if (p1Score < p2Score) {
            alert.setContentText("[P2: " + p2Score + "pts] " + p2Name + " win against [P1: " + p1Score + "pts] " + p1Name);
        } else {
            alert.setContentText("Game draw");  // no use?
        }
        alert.show();
        try {
            GUIRefresher.getCurrentDroppingBoard().bind(Chessboard.newGame(p1Name, p2Name));
        } catch (NullPointerException ignored) {
        }
    }
}
