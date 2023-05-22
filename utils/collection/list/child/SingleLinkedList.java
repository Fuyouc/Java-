package utils.collection.list.child;

import utils.collection.list.AbstractList;
import utils.objects.ObjectUtils;

import java.util.Iterator;

/**
 * 单向链表实现的List
 * @param <E>
 */
public class SingleLinkedList<E> extends AbstractList<E> {

    private static class Node<E>{
        E element;
        Node<E> next;

        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }
    }

    private Node<E> head;
    private Node<E> last;

    @Override
    public boolean add(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        Node<E> newNode = new Node<>(element,null);
        if (isEmpty()){
            head = newNode;
            last = head;
        }else {
            last.next = newNode;
            last = newNode;
        }
        ++size;
        return true;
    }

    @Override
    public boolean remove(E element) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return false;
        Node<E> node = head,pre = null;
        while (node != null){
            if (element.equals(node.element)){
                if (pre == null){
                    //删除的是头结点
                    if (last == head){
                        //如果只有一个头结点，直接删除即可
                        head = null;
                        last = null;
                    }else {
                        Node<E> temp = head;
                        head = head.next;
                        temp.next = null;
                    }
                }else {
                    if (node.next != null) pre.next = node.next;
                    else {
                        pre.next = null;
                        last = pre;
                    }
                }
                --size;
                return true;
            }
            pre = node;
            node = node.next;
        }
        return false;
    }

    @Override
    public E remove(int index) {
        if (index >= size) return null;

        if (index == 0){
            --size;
            E removeE = head.element;
            if (head.next != null){
                head = head.next;
            }else {
                head = null;
                last = null;
            }
            return removeE;
        }

        Node<E> pre = head;
        Node<E> removeNode = pre.next;

        for (int i = 1; i < index; ++i){
            pre = removeNode;
            removeNode = removeNode.next;
        }
        if (removeNode.next != null){
            pre.next = removeNode.next;
        }else {
            pre.next = null;
            last = pre;
        }
        --size;
        return removeNode.element;
    }

    @Override
    public E get(int index) {
        if (index >= size || isEmpty()) return null;
        Node<E> node = head;
        for (int i = 0; i < index; ++i){
            node = node.next;
        }
        return node.element;
    }

    @Override
    public void reversal() {
        if (isEmpty() || size == 1) return;
        Node<E> node = head.next,
                pre = head,
                prePre = null;
        while (node != null){
            Node<E> temp = node.next;
            node.next = pre;
            pre.next = prePre;
            prePre = pre;
            pre = node;
            node = temp;
        }
        head = pre;
    }

    @Override
    public boolean contains(E element) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return false;
        Node<E> node = head;
        while (node.next != null){
            if (element.equals(node.element)) return true;
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = null;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new LinkedIterator<E>(head);
    }

    private static class LinkedIterator<E> implements Iterator<E>{

        private Node<E> node;

        public LinkedIterator(Node<E> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            E element = node.element;
            node = node.next;
            return element;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        Iterator<E> iterator = iterator();
        while (iterator.hasNext()){
            sb.append(iterator.next() + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
