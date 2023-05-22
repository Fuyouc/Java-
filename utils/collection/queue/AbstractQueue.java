package utils.collection.queue;

public abstract class AbstractQueue<E> implements Queue<E>{
    protected int size;

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }
}
