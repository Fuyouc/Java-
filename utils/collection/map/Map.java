package utils.collection.map;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;
import utils.collection.set.Set;

import java.util.Iterator;

public interface Map<K,V>{
    /**
     * 添加键值对
     * @param key
     * @param value
     */
    void put(K key,V value);

    /**
     * 根据 key 查询Value
     * @param key
     * @return
     */
    V get(K key);

    /**
     * 删除键值对
     * @param key
     * @return
     */
    V remove(K key);

    /**
     * 查询Key是否存在
     * @return
     */
    boolean contains(K key);

    /**
     * 元素个数
     */
    int size();

    /**
     * List是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 清空List
     */
    void clear();

    /**
     * 批量添加
     * @param map
     * @return
     */
    boolean putAll(Map<? extends K,? extends V> map);

    /**
     * 迭代器
     * @return
     */
    Iterator<Entry<K,V>> iterator();

    /**
     * 迭代器元素实体
     * @param <K>
     * @param <V>
     */
    interface Entry<K,V>{
        /**
         * 获取 Key
         */
        K getKey();

        /**
         * 获取Value
         */

        V getValue();
    }

    /**
     * 将Map集合转换成Set集合
     * @return
     */
    Set<Entry<K,V>> entrySet();

    /**
     * 获取所有的Key
     * @return
     */
    Set<K> keySet();

    /**
     * 获取所有的Value
     * @return
     */
    Collection<V> values();

    /**
     * 批量添加集合元素
     * @param objects
     * @param <K>
     * @param <V>
     * @return
     */
    static <K,V> Map<? extends K,? extends V> of(Object... objects){
        return CollectionConstructor.buildMap(objects);
    }
}
