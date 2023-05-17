package utils.collection.list.child;

import java.util.Comparator;

/**
 * 会排序的ArrayList
 */
public class SortArrayList<E> extends ArrayList<E> {

    /**
     * 比较器
     */
    private Comparator<E> comparator;

    public SortArrayList(){
        this(DEFAULT_CAPACITY);
    }

    public SortArrayList(Comparator<E> comparator){
        this(DEFAULT_CAPACITY);
        this.comparator = comparator;
    }

    public SortArrayList(int size){
        super(size);
    }

    @Override
    public boolean add(E element) {
        if (super.add(element)) {
            sort();
            return true;
        }
        return false;
    }

    private void sort(){
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size - i - 1;++j){
                E e1 = elements[j];
                E e2 = elements[j + 1];
                int compare = compare(e1, e2);
                if (compare > 0){
                    swat(j,j + 1);
                }
            }
        }
    }

    private void swat(int i1,int i2){
        E temp = elements[i1];
        elements[i1] = elements[i2];
        elements[i2] = temp;
    }

    private int compare(E e1,E e2){
        if (comparator != null) return comparator.compare(e1,e2);
        return ((Comparable)e1).compareTo(e2);
    }
}
