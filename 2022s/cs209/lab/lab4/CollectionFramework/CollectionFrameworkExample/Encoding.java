public class Encoding {
    private char sign;
    private char letter;

    public Encoding(char s, char l) {
        sign = s;
        letter = l;
    }

    public char getSign() {
        return sign;
    }

    public char getLetter() {
        return letter;
    }

    public String toString() {
        return sign + "->" + letter;
    }
}

