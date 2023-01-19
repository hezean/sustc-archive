import java.util.Objects;

// Make everything final so that it becomes immutable
public final class Encoding3 {
    private final char sign;
    private final char letter;

    public Encoding3(char s, char l) {
        sign = s;
        letter = l;
    }

    public char getSign() {
        return sign;
    }

    public char getLetter() {
        return letter;
    }

    //no setter

    public String toString() {
        return sign + "->" + letter;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sign, letter);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
          return false;
        }
        if (o.getClass() == this.getClass()) {
          Encoding3 e = (Encoding3)o;
          return (this.sign == e.sign) && (this.letter == e.letter);
        }
        return false;
    }
}

