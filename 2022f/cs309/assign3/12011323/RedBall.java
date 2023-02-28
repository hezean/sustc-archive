import java.awt.*;
import java.util.Random;

public class RedBall extends Ball {

    private Random random;

    public RedBall(int xSpeed, int ySpeed, int ballSize) {
        super(Color.RED, xSpeed, ySpeed, ballSize);
        random = new Random();
    }

    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof Character move) {
            switch (move) {
                case 'a' -> setXSpeed(-random.nextInt(10) - 1);
                case 'd' -> setXSpeed(random.nextInt(10) + 1);
                case 's' -> setYSpeed(random.nextInt(10) + 1);
                case 'w' -> setYSpeed(-random.nextInt(10) - 1);
            }
        }

        if (o instanceof WhiteRandomBall wrb) {
            updateVisible(wrb);
        }
    }
}
