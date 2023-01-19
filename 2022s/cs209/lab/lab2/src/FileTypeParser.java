import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

public class FileTypeParser {
    private static byte[] PNG = new byte[]{(byte) 0x89, 0x50, 0x4e, 0x47};
    private static byte[] ZIP_JAR = new byte[]{0x50, 0x4b, 0x03, 0x04};
    private static byte[] CLASS = new byte[]{(byte) 0xca, (byte) 0xfe, (byte) 0xba, (byte) 0xbe};

    public static void main(String[] args) {
        if (args.length != 1) {
            System.err.println("args.length != 1");
            return;
        }

        byte[] header = new byte[4];
        try (FileInputStream fis = new FileInputStream(args[0])) {
            fis.read(header);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
            return;
        }
        String fileType;

        if (Arrays.equals(header, PNG)) {
            fileType = "png";
        } else if (Arrays.equals(header, ZIP_JAR)) {
            fileType = "zip or jar";
        } else if (Arrays.equals(header, CLASS)) {
            fileType = "class";
        } else {
            fileType = "unknown";
        }
        System.out.println("Filename: " + args[0]);
        System.out.println("File Header (Hex): " + toHexString(header));
        System.out.println("File Type: " + fileType);
    }

    private static String toHexString(byte[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for(byte hex:arr) {
            sb.append(String.format("%x", hex))
                    .append(", ");
        }
        sb.replace(sb.length()-2,sb.length(),"]");
        return sb.toString();
    }
}
