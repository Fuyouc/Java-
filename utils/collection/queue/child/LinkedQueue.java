package utils.collection.queue.child;

import utils.collection.list.child.DoubleLinkedList;
import utils.collection.queue.AbstractQueue;

import java.util.Iterator;

/**
 * 采用双向链表的队列
 * @param <E>
 */
public class LinkedQueue<E> extends AbstractQueue<E> {

    private DoubleLinkedList<E> list;

    public LinkedQueue() {
        list = new DoubleLinkedList<>();
    }

    public LinkedQueue(E... objects) {
        list = new DoubleLinkedList<>(objects);
    }

    @Override
    public boolean remove(E element) {
        return list.remove(element);
    }

    @Override
    public boolean contains(E element) {
        return list.contains(element);
    }

    @Override
    public void clear() {
        list.clear();
    }

    @Override
    public Iterator<E> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    @Override
    public boolean isEmpty() {
        return list.isEmpty();
    }

    @Override
    public boolean add(E element) {
        return list.add(element);
    }

    @Override
    public E remove() {
        if (isEmpty()) throw new RuntimeException("queue element size = 0");
        return list.remove(0);
    }

    @Override
    public E poll() {
        if (isEmpty()) throw new RuntimeException("queue element size = 0");
        return list.remove(0);
    }

    @Override
    public E peekFirst() {
        return list.first();
    }

    @Override
    public E peekLast() {
        return list.last();
    }


    @Override
    public String toString() {
        return list.toString();
    }
}
