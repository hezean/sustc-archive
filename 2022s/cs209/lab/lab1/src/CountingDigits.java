import org.junit.jupiter.api.Test;

import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class CountingDigits {
    /**
     * count the number of digit 0 appearing in a long
     */
    public static int getNumberOfDigitZero1(long value) {
        return Long.SIZE - Long.bitCount(value);
    }

    public static int getNumberOfDigitZero2(long value) {
        String bin = Long.toBinaryString(value);
        int zeroBins = Long.SIZE - bin.length();
        for (int i = 0, l = bin.length(); i < l; ++i) {
            if (bin.charAt(i) == '0') ++zeroBins;
        }
        return zeroBins;
    }

    public static int getNumberOfDigitZero3(long value) {
        IntStream bin = Long.toBinaryString(value).chars();
        final int prefixZeros = Long.SIZE - Long.toBinaryString(value).length();
        return prefixZeros + (int) bin.filter(c -> c == '0').count();
    }

    public static int getNumberOfDigitZero4(long value) {
        int ones = 0;
        for (int i = 0; i < Long.SIZE; i++) {
            if ((value & (0x1L << i)) != 0) {
                ++ones;
            }
        }
        return Long.SIZE - ones;
    }
}


class CountingDigitsTest {
    @Test
    public void testImpl1() {
        assertEquals(64, CountingDigits.getNumberOfDigitZero1(0));
        assertEquals(0, CountingDigits.getNumberOfDigitZero1(-1));
        assertEquals(62, CountingDigits.getNumberOfDigitZero1(1280));
    }

    @Test
    public void testImpl2() {
        assertEquals(64, CountingDigits.getNumberOfDigitZero2(0));
        assertEquals(0, CountingDigits.getNumberOfDigitZero2(-1));
        assertEquals(62, CountingDigits.getNumberOfDigitZero2(1280));
    }

    @Test
    public void testImpl3() {
        assertEquals(64, CountingDigits.getNumberOfDigitZero3(0));
        assertEquals(0, CountingDigits.getNumberOfDigitZero3(-1));
        assertEquals(62, CountingDigits.getNumberOfDigitZero3(1280));
    }

    @Test
    public void testImpl4() {
        assertEquals(64, CountingDigits.getNumberOfDigitZero4(0));
        assertEquals(0, CountingDigits.getNumberOfDigitZero4(-1));
        assertEquals(62, CountingDigits.getNumberOfDigitZero4(1280));
    }
}
