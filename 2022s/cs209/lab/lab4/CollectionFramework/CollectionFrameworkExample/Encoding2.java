import java.util.Objects;

public class Encoding2 {
	private char sign;
	private char letter;

	public Encoding2(char s, char l) {
		sign = s;
		letter = l;
	}

	public char getSign() {
		return sign;
	}

	public char getLetter() {
		return letter;
	}

	public void setChar(char sign) {
		this.sign = sign;
	}

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
			Encoding2 e = (Encoding2) o;
			return (this.sign == e.sign) && (this.letter == e.letter);
		}
		return false;
	}
}
