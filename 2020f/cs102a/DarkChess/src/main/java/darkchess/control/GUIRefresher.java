package darkchess.control;

import darkchess.view.components.DroppingBoardView;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class GUIRefresher implements Refreshable {

    private static final List<GUIRefresher> hooks = new LinkedList<>();

    @Getter
    @Setter
    private static DroppingBoardView currentDroppingBoard;

    @Override
    public void register() {
        hooks.add(this);
    }

    public static void refreshAll() {
        hooks.forEach(r -> {
            try {
                r.refresh();
            } catch (Exception e) {
                log.error("fail to refresh gui task", e);
            }
        });
    }
}
