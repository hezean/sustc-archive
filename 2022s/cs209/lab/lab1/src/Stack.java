import java.util.Arrays;

public class Stack<T> {
    T[] element;
    int size;
    int CAPACITY = 10;

    public Stack(int capacity) {
        CAPACITY = capacity;
        this.element = (T[]) new Object[CAPACITY];
    }

    public void push(T t) {
        if (size >= CAPACITY) {
            return;
        }
        element[size++] = t;
    }

    public T pop() {
        if (size <= 0) {
            return null;
        }
        T elem = element[--size];
        element[size] = null;  // gc
        return elem;
    }

    public void showElements() {
        System.out.print(Arrays.asList(element).subList(0, size));
    }

    public static void main(String[] args) {
        Stack<Integer> stack = new Stack<>(5);
        stack.push(1);
        stack.push(2);
        stack.push(3);
        stack.push(4);
        stack.push(5);
        stack.push(6);
        System.out.println(stack.pop());
        System.out.println(stack.pop());
        stack.push(10);
        stack.showElements();
    }
}
