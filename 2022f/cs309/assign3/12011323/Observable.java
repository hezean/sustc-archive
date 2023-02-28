public interface Observable {

    void register(Observer o);

    void deregister(Observer o);

    void notifyAllSubscribers(Object arg);

    void notifyAllSubscribers();
}
