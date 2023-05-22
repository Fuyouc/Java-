package utils.collection.list.child;

import utils.collection.list.AbstractList;
import utils.objects.ObjectUtils;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 动态数组
 * @param <E>
 */
public class ArrayList<E> extends AbstractList<E> {

    /**
     * 存储元素
     */
    protected E[] elements;

    /**
     * 数组的默认容量
     */
    protected static final int DEFAULT_CAPACITY = 64;


    public ArrayList() {
        this(DEFAULT_CAPACITY);
    }

    public ArrayList(int size) {
        this.elements = (E[]) new Object[size];
    }

    public ArrayList(E[] array){
        this.elements = array;
    }

    @Override
    public boolean add(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        if (size == elements.length){
            //如果数组已经满了,动态扩容数组
            enlargeCapacity();
        }
        elements[size++] = element;
        return true;
    }

    /**
     * 动态扩容
     */
    private void enlargeCapacity(){
        int oldCapacity = elements.length;
        //动态扩容原先容量的1.5倍
        int newCapacity = oldCapacity + (oldCapacity >> 1);
        elements = Arrays.copyOf(elements, newCapacity);
    }

    @Override
    public boolean remove(E element) {
        for (int i = 0; i < size; i++) {
            if (element.equals(elements[i])){
                //如果找到删除的元素
                if (i == (size - 1)){
                    //如果删除的是最后一个元素
                    elements[i] = null;
                }else{
                    for (int j = i + 1;j < size; ++j){
                        //将数组后面的元素向前移动
                        elements[j - 1] = elements[j];
                    }
                }
                --size;
                return true;
            }
        }
        return false;
    }

    @Override
    public E remove(int index) {
        if (index >= size) return null;
        E removeElement = elements[index];
        ++index;
        for (;index < size; ++index){
            elements[index - 1] = elements[index];
        }
        --size;
        return removeElement;
    }

    @Override
    public E get(int index) {
        if (index >= size) return null;
        return elements[index];
    }

    @Override
    public void reversal() {
        if (isEmpty() || size == 1) return;
        E[] tempElement = (E[]) new Object[size];
        int length = size - 1;
        for (int i = 0; i < tempElement.length; i++) {
            tempElement[i] = elements[length--];
        }
        elements = tempElement;
    }

    @Override
    public boolean contains(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        for (int i = 0; i < size; i++) {
            if (element.equals(elements[i])) return true;
        }
        return false;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < elements.length; i++) {
            elements[i] = null;
        }
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new ArrayIterator<>(elements);
    }

    private static class ArrayIterator<E> implements Iterator<E>{

        private E[] elements;
        private int index;

        public ArrayIterator(E[] elements) {
            this.elements = elements;
            this.index = 0;
        }

        @Override
        public boolean hasNext() {
            return index < elements.length && elements[index] != null;
        }

        @Override
        public E next() {
            return elements[index++];
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i] + ",");
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("]");
        return sb.toString();
    }
}
