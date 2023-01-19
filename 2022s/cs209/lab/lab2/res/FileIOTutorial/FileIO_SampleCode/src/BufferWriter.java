import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;

public class BufferWriter {

	public static void main(String[] args) {
		try (FileOutputStream fos = new FileOutputStream(new File("output2_gb18030.txt"));
				OutputStreamWriter osw = new OutputStreamWriter(fos, "gb18030");
				BufferedWriter bWriter = new BufferedWriter(osw);){
			bWriter.write("你好！\n");
//			bWriter.write(100);
			bWriter.write("100");
			bWriter.write(" 分 \n");
			bWriter.write("送给你！\n");
			bWriter.flush();//bWriter.close();
			
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
