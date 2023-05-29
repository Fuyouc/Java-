package utils.collection.queue.child;

import utils.collection.queue.AbstractQueue;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 优先队列（采用堆实现）
 */
public class PriorityQueue<E> extends AbstractQueue<E> {

    /**
     * 比较器
     * 如果没有比较器，默认情况下采用最小堆存储
     */
    private Comparator<E> comparator;

    private E[] elements;

    //默认容量
    private static final int DEFAULT_CAPACITY = 11;

    /**
     * 记录当前堆的类型（true表示最大堆，false表示最小堆）
     */
    private static boolean heapType = false;

    public PriorityQueue() {
        this(null);
    }

    public PriorityQueue(Comparator<E> comparator){
        this(comparator,DEFAULT_CAPACITY);
    }

    public PriorityQueue(Comparator<E> comparator,int size) {
        this.elements = (E[]) new Object[size];
        this.comparator = comparator;
    }

    @Override
    public boolean remove(E element) {
        for (int i = 0; i < size; i++) {
            int cmp = compare(element,elements[i]);
            if (cmp == 0){
                elements[i] = null;
                if (i != size - 1){
                    //删除的不是最后一个节点
                    for (int j = i + 1; j < size; ++j){
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
    public boolean contains(E element) {
        for (int i = 0; i < size; i++) {
            int cmp = compare(element,elements[i]);
            if (cmp == 0){
                return true;
            }
        }
        return false;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            elements[i] = null;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new QueueIterator();
    }

    final class QueueIterator implements Iterator<E>{

        private int index;

        @Override
        public boolean hasNext() {
            return index < size;
        }

        @Override
        public E next() {
            return elements[index++];
        }
    }

    @Override
    public boolean add(E element) {
        if (isEmpty()){
            addRoot(element);
        }else {
            if (size == elements.length){
                //如果数组已经满了,动态扩容数组
                enlargeCapacity();
            }
            //记录当前节点的下标
            int index = size;
            //记录父节点下标
            int parentIndex = getParentIndex(index);
            ++size;
            elements[index] = element;
            //如果存在父节点，并且不是同一个元素
            while (parentIndex >= 0 && (index != parentIndex)){
                int cmp = compare(elements[index],elements[parentIndex]);
                if (cmp < 0){
                    swit(index,parentIndex);
                    index = parentIndex;
                    parentIndex = getParentIndex(index);
                }else {
                    heapType = true;
                    break;
                }
            }
        }
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

    private void swit(int i1,int i2){
        E temp = elements[i1];
        elements[i1] = elements[i2];
        elements[i2] = temp;
    }

    private void addRoot(E element){
        elements[size++] = element;
    }

    @Override
    public E remove() {
        return poll();
    }

    @Override
    public E poll() {
        if (isEmpty()) return null;

        E removeElement = elements[0];
        elements[0] = elements[size - 1];
        elements[size - 1] = null;
        --size;

        if (heapType){
            repairMaxHeapProperty();
        }else {
            repairMinHeapProperty();
        }

        return removeElement;
    }

    /**
     * 删除后修补最大堆性质
     */
    private void repairMaxHeapProperty(){
        int index = 0;
        while (index < size){
            if (isLeftThanRight(index)){
                //左节点大于右节点
                swit(index,getLeftChildIndex(index));
                index =getLeftChildIndex(index);
            }else {
                if (isRightChild(index)){
                    swit(index,getRightChildIndex(index));
                    index = getRightChildIndex(index);
                }else break;
            }
        }
    }

    /**
     * 删除后修补最小堆性质
     */
    private void repairMinHeapProperty(){
        int index = 0;
        while (index < size){
            if (isRightThanLeft(index)){
                //左节点大于右节点
                swit(index,getLeftChildIndex(index));
                index =getLeftChildIndex(index);
            }else {
                if (isRightChild(index)){
                    swit(index,getRightChildIndex(index));
                    index = getRightChildIndex(index);
                }else break;
            }
        }
    }

    @Override
    public E peekFirst() {
        return elements[0];
    }

    @Override
    public E peekLast() {
        if (isEmpty()) return null;
        return elements[size - 1];
    }

    //计算当节点的左子树下标
    private int getLeftChildIndex(int index){
        return (2 * index) + 1;
    }

    //计算当前节点的右子树下标
    private int getRightChildIndex(int index){
        return (2 * index) + 2;
    }

    //计算当前节点的父节点下标
    private int getParentIndex(int index){
        return (index - 1) / 2;
    }

    //判断是否有左子树
    private boolean isLeftChild(int index){
        return getLeftChildIndex(index) <= size - 1;
    }

    //判断是否有右子树
    private boolean isRightChild(int index){
        return getRightChildIndex(index) <= size - 1;
    }

    /**
     * 左节点比右节点大
     * @return
     */
    private boolean isLeftThanRight(int index){
        if (isLeftChild(index)){
            return isRightChild(index) ? compare(elements[getLeftChildIndex(index)],elements[getRightChildIndex(index)]) < 0 : true;
        }else {
            return false;
        }
    }

    /**
     * 右子节点比左节点大
     * @return
     */
    private boolean isRightThanLeft(int index){
        if (isRightChild(index)){
            return isLeftChild(index) ? compare(elements[getRightChildIndex(index)],elements[getLeftChildIndex(index)]) > 0 : true;
        }else return false;
    }



    private int compare(E e1,E e2){
        if (comparator != null) return comparator.compare(e1,e2);
        return ((Comparable)e1).compareTo(e2);
    }
}
