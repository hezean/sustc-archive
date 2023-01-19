package cs102a.aeroplane.util;

import java.security.SecureRandom;

public class Dice {
    public static int roll() {
        SecureRandom sr = new SecureRandom();
        return 1 + sr.nextInt(6);
    }

    /**
     * @apiNote 修复了可能摇出相同点数的bug，专用于battle
     */
    public static int[] rollDices() {
        SecureRandom sr = new SecureRandom();
        int[] dice = new int[2];
        dice[0] = 1 + sr.nextInt(6);
        dice[1] = 1 + sr.nextInt(6);
        while (dice[0] == dice[1]) {
            dice[0] = 1 + sr.nextInt(6);
        }
        return dice;
    }
}
