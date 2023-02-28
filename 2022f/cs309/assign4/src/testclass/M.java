package testclass;

import dependency_injection.Inject;
import dependency_injection.Value;

import java.util.List;

public class M {

    private J j;

    private String s;

    private List<Boolean> list;

    public J getJ() {
        return j;
    }

    public String getS() {
        return s;
    }

    public List<Boolean> getList() {
        return list;
    }

    @Inject
    public M(J j, @Value(value = "test") String s, @Value(value = "bool-list",delimiter = "-") List<Boolean> list) {
        this.j = j;
        this.s = s;
        this.list = list;
    }
}
