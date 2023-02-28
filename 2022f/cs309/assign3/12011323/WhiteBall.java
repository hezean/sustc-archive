import java.awt.*;

public class WhiteBall extends Ball {

    public WhiteBall(int xSpeed, int ySpeed, int ballSize) {
        super(Color.WHITE, xSpeed, ySpeed, ballSize);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Character move) {
            switch (move) {
                case 'a' -> setXSpeed(-8);
                case 'd' -> setXSpeed(8);
                case 'w' -> setYSpeed(-8);
                case 's' -> setYSpeed(8);
            }
        }

        if (o instanceof WhiteRandomBall wrb) {
            updateVisible(wrb);
        }
    }
}
