package testclass;


import dependency_injection.Inject;
import dependency_injection.Value;

import java.util.*;

public class I {

    @Value(value = "[2345,360,kingsoft,]")
    private List<String> rogue3Software;

    @Value(value = "{hatsune-miku-39-39-39c5bb}", delimiter = "-")
    private Set<Integer> mikuSet;

    @Value(value = "{never-gonna:give-you-up,never-gonna:let-you-down}", delimiter = ",")
    private Map<String, String> swindle;

    @Value(value = "{tRUe:955,fALsE:icu,Yes:955,No:996}", delimiter = ",")
    private Map<Boolean, Integer> work;

    public List<String> getRogue3Software() {
        return rogue3Software;
    }

    public Set<Integer> getMikuSet() {
        return mikuSet;
    }

    public Map<String, String> getSwindle() {
        return swindle;
    }

    public Map<Boolean, Integer> getWork() {
        return work;
    }
}
