package cs102a.aeroplane.frontend.model;

import cs102a.aeroplane.util.SystemSelect;

import javax.swing.*;

//根据骰子点数选择图片
public class MatchDicePicture {

    public static ImageIcon getImage(int point) {
        String path = SystemSelect.getImagePath();
        ImageIcon pic1 = new ImageIcon(path + "dice_1.png");
        ImageIcon pic2 = new ImageIcon(path + "dice_2.png");
        ImageIcon pic3 = new ImageIcon(path + "dice_3.png");
        ImageIcon pic4 = new ImageIcon(path + "dice_4.png");
        ImageIcon pic5 = new ImageIcon(path + "dice_5.png");
        ImageIcon pic6 = new ImageIcon(path + "dice_6.png");

        switch (point) {
            case 1:
                return pic1;
            case 2:
                return pic2;
            case 3:
                return pic3;
            case 4:
                return pic4;
            case 5:
                return pic5;
            default:
                return pic6;
        }
    }
}

