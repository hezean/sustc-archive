import java.util.Arrays;

public class ByteBuffParser {
    /**
     * convert the contents of the byteBuffer to int according to lengthOfBits
     *
     * @param byteBuffer   considered as a bit-string with length of 8 * `byteBuffer.length`
     * @param lengthOfBits stores the order and bits to be parsed
     * @return parsed from `unsigned bytes`
     */
    public static int[] ParseByteBuff(Byte[] byteBuffer, int[] lengthOfBits) {
        if (sumOfArray(lengthOfBits) != byteBuffer.length * Byte.SIZE) {
            return null;  // guard: throw Exception?
        }
        StringBuilder sb = new StringBuilder(byteBuffer.length * Byte.SIZE);
        for (var b : byteBuffer) {
            sb.append(toBinaryString(b));
        }
        String strByte = sb.toString();
        int[] res = new int[lengthOfBits.length];
        int idx = 0, fi = 0;
        while (idx < strByte.length()) {
            res[fi] = parseBinaryString(strByte.substring(idx, idx + lengthOfBits[fi]));
            idx += lengthOfBits[fi];
            fi++;
        }
        return res;
    }

    private static String toBinaryString(Byte b) {
        StringBuilder sb = new StringBuilder(Byte.SIZE);
        for (int i = Byte.SIZE - 1; i >= 0; i--) {
            sb.append((b & (0x1 << i)) == 0 ? '0' : '1');
        }
        return sb.toString();
    }

    private static int parseBinaryString(String s) {
        int res = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) == '1') {
                res += Math.pow(2, s.length() - 1 - i);
            }
        }
        return res;
    }

    public static int sumOfArray(int[] a) {
        int sum = 0;
        for (int e : a) {
            sum += e;
        }
        return sum;
    }

    public static void main(String[] args) {
        Byte[] buffer = {(byte) 128, (byte) 4};
        int[] lengthOfBits = {4, 4, 8}; //Notice:Each element <= 32
        // {(byte)128, (byte)4} ----> 10000000 00000100
        //                            |__||__| |______|
        //      split by lengthOfBits   4  4      8
        //                           1000 0000  00000100
        //               result:       8   0      4

        int[] result = ParseByteBuff(buffer, lengthOfBits);
        System.out.println(Arrays.toString(result));  // 8, 0, 4
    }
}
