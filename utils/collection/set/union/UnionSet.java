package utils.collection.set.union;

import utils.collection.set.Set;

import java.util.Iterator;
import java.util.List;

/**
 * 并查集接口
 */
public interface UnionSet<V> extends Set<V> {

    /**
     * 判断两个元素是否在一个集合中
     * @param a
     * @param b
     * @return
     */
    boolean isSameSet(V a,V b);


    /**
     * 合并两个元素的集合
     * @param a
     * @param b
     */
    void union(V a,V b);

    /**
     * 获取在同一组的其他元素
     * @param value
     * @return
     */
    List<V> getSameSetValue(V value);

    /**
     * 遍历指定某个集合
     * @param value
     * @return
     */
    Iterator<V> iterator(V value);
}
