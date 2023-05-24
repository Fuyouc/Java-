package utils.sparse.child;

import utils.objects.ObjectUtils;
import utils.sparse.SparseArray;

import java.lang.reflect.Array;

/**
 * 稀疏数组（将一个二维数组压缩成稀疏数组）
 * 注意：
 *     这里只是采用稀疏数组思想，底层实现还是采用单链表进行实现
 *     可用自行修改源码转换成真正的稀疏数组进行存储
 */
public final class LinkedSparseArray<E extends Object> implements SparseArray<E> {

    /**
     * 数组总共有几行
     */
    private int arrayRowCount;

    /**
     * 数组总共有几列
     */
    private int arrayColumnCount;

    /**
     * 数组总共有多少个元素
     */
    private int arrayElementCount;

    /**
     * 头部
     */
    private Entry<E> head;

    /**
     * 尾部
     */
    private Entry<E> last;

    /**
     * 记录当前数组是什么类型（在重新转成数组时，需要使用反射来创建数组）
     */
    private Class<? extends E> elementTypeClass;

    public LinkedSparseArray(E[][] array){
        if (ObjectUtils.isEmpty(array)){
            throw new RuntimeException("array = null");
        }
        /**
         * 通过 array.getClass 获取数组的Class对象
         * 在使用数组的Class.getComponentType()获取一维数组的Class对象
         * 最后在使用一维数组的Class.getComponentType()该类型
         */
        elementTypeClass = (Class<? extends E>) array.getClass().getComponentType().getComponentType();
        arrayRowCount = array.length;
        arrayColumnCount = array[0].length;
        for (int i = 0; i < array.length; i++) {
            for (int j = 0; j < array[i].length; j++) {
                if (!ObjectUtils.isEmpty(array[i][j])){
                    add(i,j,array[i][j]);
                }
            }
        }
    }

    @Override
    public E[][] array() {
        /**
         * 注意：由于Java的泛型擦除问题，导致没办法强行 new 泛型相关的数组
         * 如果是 new Object[][] 数组，那么在强制类型的时候会出现异常
         * 所以，我们需要采用反射来动态生成一个指定类型的数组
         */
        E[][] tarArray = (E[][]) Array.newInstance(elementTypeClass,arrayRowCount,arrayColumnCount);
        Entry<E> entry = head;
        for (;entry != null;entry = entry.next){
            tarArray[entry.row][entry.column] = entry.element;
        }
        return tarArray;
    }

    static final class Entry<E>{
        int row;    //第几行
        int column; //第几列
        E element;  //元素
        Entry<E> next;

        public Entry(int row, int column, E element) {
            this.row = row;
            this.column = column;
            this.element = element;
        }
    }

    private void add(int row,int column,E element){
        Entry<E> entry = new Entry<>(row,column,element);
        if (head == null){
            head = entry;
            last = head;
        }else {
            last.next = entry;
            last = entry;
        }
        ++arrayElementCount;
    }

    /**
     * 获取数组的行数
     */
    public int getRow(){
        return arrayRowCount;
    }

    /**
     * 获取数组的列数
     */
    public int getColumn(){
        return arrayColumnCount;
    }

    /**
     * 获取数组中有多少个可用元素
     */
    public int getElementCount(){
        return arrayElementCount;
    }


}
