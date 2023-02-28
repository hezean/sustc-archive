package darkchess.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import darkchess.model.utils.ChessColor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.LinkedList;
import java.util.List;

@Data
@SuperBuilder
@RequiredArgsConstructor
public class Player {

    String name;

    List<Integer> historyScores = new LinkedList<>();
}
