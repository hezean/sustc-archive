import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;


public class ByteReader {

	public static void main(String[] args) {
		try (FileInputStream fis = new FileInputStream("sample.txt")){
			
			byte[] buffer = new byte[65535];

			int byteNum = fis.read(buffer);
			for(int i = 0; i < byteNum; i++){
				System.out.printf("%02x ",buffer[i]);
			}
			System.out.println();								
			
		} catch (FileNotFoundException e) {
			System.out.println("The pathname does not exist.");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("Failed or interrupted when doing the I/O operations");
			e.printStackTrace();
		}
		
	}

}
