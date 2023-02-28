package testclass;

import dependency_injection.Inject;
import dependency_injection.Value;

import java.util.List;
import java.util.Map;

public class JImpl implements J {
    private E eDep;

    @Value(value = "bool-array", delimiter = "-")
    private boolean[] boolArray;

    @Value(value = "j.integers", delimiter = "-")
    private int integer;

    @Value(value = "homo", delimiter = ",")
    private List<Integer> list;

    @Value(value = "horr", delimiter = ",")
    private Map<Integer, String> map;


    public List<Integer> getList() {
        return list;
    }

    public Map<Integer, String> getMap() {
        return map;
    }

    private String string;

    @Inject
    public JImpl(E eDep) {
        this.eDep = eDep;
    }

    @Override
    public E getEDep() {
        return eDep;
    }

    @Override
    public int getInt() {
        return integer;
    }

    @Override
    public String getString() {
        return string;
    }

    public boolean[] getBoolArray() {
        return boolArray;
    }
}
