package testclass;


import dependency_injection.Value;

public class H {

    @Value(value = "homo:9999999999999:114514", delimiter = ":")
    private int homo;

    @Value(value = "deadbeef deadc0de TrUee false", delimiter = " ")
    private boolean magic;

    @Value(value = "never gonna give you up")
    private String lyric;

    public int getHomo() {
        return homo;
    }

    public boolean isMagic() {
        return magic;
    }

    public String getLyric() {
        return lyric;
    }
}
