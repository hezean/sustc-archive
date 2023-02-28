package darkchess.control;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.NotImplementedException;

import java.util.LinkedList;
import java.util.List;

@Slf4j
public abstract class ResourceRefresher implements Refreshable {

    private static final List<ResourceRefresher> hooks = new LinkedList<>();

    @Override
    public void register() {
        hooks.add(this);
    }

    public static void refreshAll() {
        log.info("refresh resources");
        hooks.forEach(r -> {
            try {
                r.refresh();
            } catch (IllegalArgumentException ignored) {
            } catch (Exception e) {
                log.error("fail to refresh resource", e);
            }
        });
    }
}
