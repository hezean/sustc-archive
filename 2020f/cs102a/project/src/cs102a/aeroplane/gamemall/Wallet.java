package cs102a.aeroplane.gamemall;

import cs102a.aeroplane.GameInfo;

public class Wallet {

    private static final float[] discountAsPercent = {0.80f, 1.00f, 0.90f, 0.95f};
    private static final float[] balance = {100, 200, 300, 400};


    public static float getDiscountAsPercent(int user) {
        return discountAsPercent[user];
    }


    public static void setDiscountAsPercent(int user, String discountAsPercent) throws AssertionError {
        assert GameInfo.isSuperUser() : "没权限给自己加钱，略略略";
        try {
            float discount = Float.parseFloat(discountAsPercent) / 100;
            if (!(discount <= 1.00f && discount >= 0.00f)) throw new AssertionError("优惠方案在0-100之间哦");
            Wallet.discountAsPercent[user] = discount;
        } catch (NumberFormatException e) {
            throw new AssertionError("格式不对哦，并没有进行修改");
        }

    }


    public static float getBalance(int user) {
        return balance[user];
    }


    public static void editBalance(int user, String balance) throws AssertionError {
        assert GameInfo.isSuperUser() : "没权限给自己加钱，略略略";
        try {
            float balanceInt = Float.parseFloat(balance);
            if (balanceInt < 0) throw new AssertionError("氪金，充的钱当然要是正数啊");
            Wallet.balance[user] = balanceInt;
        } catch (NumberFormatException e) {
            throw new AssertionError("格式不对哦，并没有进行修改");
        }
    }


    public static void spendBalance(int user, float moneyUsed) {
        Wallet.balance[user] -= moneyUsed;
    }
}
