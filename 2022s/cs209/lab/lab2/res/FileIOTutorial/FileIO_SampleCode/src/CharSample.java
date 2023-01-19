import java.io.UnsupportedEncodingException;

public class CharSample {

	public static void main(String[] args) {
		// UTF-16:赵-8d75 耀-8000, GB:赵-D5D4 耀-D2AB
		char c = '赵';
		int value = c;
		System.out.printf("%s\n", c);
		System.out.printf("%X\n", value);

		String str = "赵耀"; // UTF-16
		try {
			byte[] bytes1 = str.getBytes("GBK"); // or GBK
			for (byte b : bytes1) {
				System.out.printf("%2X ", b);
			}
			System.out.println();
			byte[] bytes2 = str.getBytes("UTF-16");
			for (byte b : bytes2) {
				System.out.printf("%02X ", b);
			}
			System.out.println();

			byte[] bytes3 = str.getBytes("UTF-16BE");
			for (byte b : bytes3) {
				System.out.printf("%02X ", b);
			}
			System.out.println();

			byte[] bytes4 = str.getBytes("UTF-16LE");
			for (byte b : bytes4) {
				System.out.printf("%02X ", b);
			}
			System.out.println();
		} catch (UnsupportedEncodingException e) {
			System.out.println("The Character Encoding is not supported.");
			e.printStackTrace();
		}
	}
}
