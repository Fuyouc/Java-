package utils.collection.list.order;

import utils.collection.list.List;

/**
 * 有序列表
 */
public interface OrderedList<E extends Comparable<E>> extends List<E> {

    /**
     * 添加元素
     * 有序列表在对元素重复的情况下进行去重，如果内部的 element.equals(newElement) = true,那么会直接跳过本次添加
     * 如果是 element.compareTo(newElement) = 0,有序列表认为这两个对象并不一样，支持添加
     * 所以：要求模板类实现 compareTo() 方法与 equals() 方法，要在这两个方法中区分元素是否相等
     * 例：
     *   class User implements Comparable<User>{
     *       public int number;
     *       public String name;
     *
     *       //如果只是 number 相等，内部的name不相等，有序列表认为这不是一个相等的对象，会添加到列表中
     *       public int compareTo(User o){
     *           return this.number - o.number;
     *       }
     *
     *       public boolean equals(Object obj){
     *           if (this == o) return true;
     *           if (o == null || getClass() != o.getClass()) return false;
     *           User user = (User) o;
     *           return score == user.score && name.equals(user.name);
     *       }
     *   }
     */
    boolean add(E element);

    /**
     * @return 根据下标索引获取元素
     */
    E get(int index);

    /**
     * 修改指定的元素（不可以修改参与 compareTo 比较的属性，否则无效）
     * @param oldElement 旧元素
     * @param newElement 新元素
     * @return 返回旧元素信息
     */
    E set(E oldElement,E newElement);

    /**
     * @return 返回 element.compareTo(newElement) = 0 的元素集合
     */
    List<E> get(E element);

    /**
     * 根据下表删除元素
     * @return 返回删除的元素
     */
    E remove(int index);

    /**
     * 删除 element.equals(newElement) = true 的指定元素
     * @return 返回是否删除成功
     */
    boolean remove(E element);

    /**
     * 删除 element.compareTo(newElement) = 0 的元素集合
     * @return 返回删除的个数
     */
    int removeAll(E element);

    /**
     * 删除 start - end 之间的元素
     * @return 返回删除的元素集合
     */
    List<E> removeRange(E start,E end);

    /**
     * 搜索 start - end 之间的元素
     * @return 返回搜索的元素集合
     */
    List<E> searchRange(E start,E end);

    /**
     * 返回列表中 > element 的元素集合
     * @param contain 是否包含 = element 的元素
     */
    List<E> ceiling(E element,boolean contain);

    /**
     * 返回列表中 < element 的元素集合
     * @param contain 是否包含 = element 的元素
     */
    List<E> floor(E element,boolean contain);

    /**
     * 获取队列中最大的值
     */
    E getMaxElement();

    /**
     * 获取队列中最小的值
     */
    E getMinElement();

    /**
     * 元素是否存在（equals进行比较）
     */
    boolean contains(E element);

    int size();

    boolean isEmpty();

    void clear();
}
