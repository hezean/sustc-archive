import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class BufferReader {

	public static void main(String[] args) {
		try (FileInputStream fis = new FileInputStream(new File("sample.txt"));
				InputStreamReader isr = new InputStreamReader(fis, "gb18030");
				BufferedReader bReader = new BufferedReader(isr);){
			

			char[] cbuf = new char[65535];
			int file_len = bReader.read(cbuf);

			System.out.println(file_len);
			System.out.println(cbuf);
			
		} catch (FileNotFoundException e) {
			System.out.println("The pathname does not exist.");
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			System.out.println("The Character Encoding is not supported.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed or interrupted when doing the I/O operations");
			e.printStackTrace();
		}
	}
}
