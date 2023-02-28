import java.awt.*;

public class BlueBall extends Ball {

    public BlueBall(int xSpeed, int ySpeed, int ballSize) {
        super(Color.BLUE, xSpeed, ySpeed, ballSize);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Character move) {
            switch (move) {
                case 'a', 'd' -> setXSpeed(-getXSpeed());
                case 'w', 's' -> setYSpeed(-getYSpeed());
            }
        }

        if (o instanceof WhiteRandomBall wrb) {
            updateVisible(wrb);
        }
    }
}
