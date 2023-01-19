package cs102a.aeroplane.frontend.model;

import javax.swing.*;
import java.awt.*;

public class BackgroundPanel extends JPanel {
    Image image;

    public BackgroundPanel(Image image) {
        this.image = image;
        this.setOpaque(true);
    }

    public void paintComponent(Graphics g) {
        super.paintComponents(g);
        g.drawImage(image, 0, 0, this.getWidth(), this.getHeight(), this);
    }
}