import java.awt.*;
import java.util.LinkedList;
import java.util.List;

public class WhiteRandomBall extends Ball implements Observable {

    List<Observer> subscribers;

    public WhiteRandomBall(int xSpeed, int ySpeed, int ballSize) {
        super(Color.WHITE, xSpeed, ySpeed, ballSize);
        subscribers = new LinkedList<>();
    }

    public void register(Observer o) {
        subscribers.add(o);
    }

    public void deregister(Observer o) {
        subscribers.remove(o);
    }

    public void notifyAllSubscribers(Object arg) {
        subscribers.parallelStream().forEach(sub -> sub.update(this, arg));
    }

    public void notifyAllSubscribers() {
        notifyAllSubscribers(null);
    }

    public void update(Observable o, Object arg) {
        // pass
    }

    @Override
    public void move() {
        super.move();
        notifyAllSubscribers();
    }
}
