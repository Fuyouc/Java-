package utils.collection.map.hash;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;
import utils.collection.map.Map;
import utils.collection.set.Set;
import utils.objects.ObjectUtils;

import java.util.HashMap;
import java.util.Iterator;

public class HashTable<K,V> implements Map<K,V> {

    /**
     * 哈希表（采用数组+单向链表实现）
     */
    private transient Entry<K,V>[] table;

    /**
     * 元素个数
     */
    private transient int count;

    /**
     * 哈希表极限吞吐量，当 count >= threshold，表会进行动态扩容，并且重构该表
     * threshold=capacity(数组大小) * loadFactor
     */
    private int threshold;

    /**
     * 哈希表的加载因子（默认0.75）
     */
    private float loadFactor;

    /**
     * 默认初始化因子（哈希表大小）
     */
    private static final int DEFAULT_CAPACITY = 16;

    /**
     * 默认加载因子
     */
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * 要分配的数组的最大大小。有些虚拟机在数组中保留一些头字。
     * 尝试分配更大的数组可能会导致OutOfMemoryError:请求的数组大小超过虚拟机限制
     */
    private static final int MAX_ARRAY_SIZE = Integer.MAX_VALUE - 8;

    public HashTable(){
        this(DEFAULT_CAPACITY,DEFAULT_LOAD_FACTOR);
    }

    public HashTable(int initialCapacity, float loadFactor){
        if (initialCapacity < 0)
            throw new IllegalArgumentException("Illegal Capacity: "+
                    initialCapacity);
        if (loadFactor <= 0 || Float.isNaN(loadFactor))
            throw new IllegalArgumentException("Illegal Load: "+loadFactor);

        if (initialCapacity==0)
            initialCapacity = DEFAULT_CAPACITY;

        if (loadFactor == 0) this.loadFactor = DEFAULT_LOAD_FACTOR;
        else this.loadFactor = loadFactor;
        this.table = new Entry[initialCapacity];
        this.threshold = (int) (initialCapacity * loadFactor);
    }

    private static class Entry<K,V> implements Map.Entry<K,V>,Comparable<Entry<K,V>>{
        final int hash;
        final K key;
        V value;
        Entry<K,V> next;

        public Entry(int hash, K key,V value) {
            this.hash = hash;
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return "Key：" + key + ",Value：" + value;
        }

        @Override
        public int compareTo(Entry<K, V> o) {
            return ((Comparable)key).compareTo(o.key);
        }
    }

    /**
     * 刷新重构哈希表
     */
    private void refreshTable(){

        int oldCapacity = table.length;

        /**
         * 容量为原哈希表的2倍+1
         */
        int newCapacity = (oldCapacity << 1) + 1;

        /**
         * 如果新容量已经大于超过虚拟机限制，返回即可
         */
        if (newCapacity - MAX_ARRAY_SIZE > 0){
            if (newCapacity == MAX_ARRAY_SIZE) return;
            newCapacity = MAX_ARRAY_SIZE;
        }

        /**
         * 新的哈希表，大小为原哈希表的2倍+1
         */
        Entry<K,V>[] newTable = new Entry[newCapacity];

        /**
         * 重新计算哈希极限吞吐量
         */
        threshold = (int) (newCapacity * loadFactor);

        /**
         * 遍历旧的哈希表，将所有元素重新散列
         * 时间复杂度：O(n)
         */
        for (int i = oldCapacity; i-- >0;){
            /**
             * 在for循环语句中声明 old 遍历，并且检查old不为空的情况下，才向下执行
             */
            for (Entry<K,V> old = table[i];old != null;){
                //临时记录即将插入到新表的节点
                Entry<K,V> tempEntry = old;
                //让当前节点指向下一个节点，方便下次遍历
                old = old.next;
                //由于表的大小改变，索引需要重新进行计算
                int index = (tempEntry.hash & 0x7FFFFFFF) % newCapacity;
                //获取新表的头结点
                Entry<K, V> e = newTable[index];
                /**
                 * 让新元素指向头结点，并且代替头结点的位置
                 * 此处的时间复杂度仅仅只是O(1)
                 */
                tempEntry.next = e;
                newTable[index] = tempEntry;
            }
        }

        table = newTable;

    }

    /**
     * 插入方法
     * 在不计算重构表的时间复杂度外，插入的时间复杂度为O(1)
     */
    @Override
    public void put(K key, V value) {
        if (ObjectUtils.isEmpty(value)) throw new RuntimeException("Value == null");
        int hash = hash(key);
        int index = index(hash);
        Entry<K, V> node = getNode(hash, index, key);
        if (node != null){
            node.setValue(value);
            return;
        }
        addEntry(index,hash,key,value);
    }

    /**
     * 为链表添加节点
     */
    private void addEntry(int index,int hash,K key,V value){

        /**
         * 如果添加的节点个数已经超过了哈希表的容量
         * 则对表进行刷新重构
         */
        if (count >= threshold){
            refreshTable();
            //因为表的刷新，所以需要重新刷新新插入节点的索引
            index = index(hash);
        }

        /**
         * 这里有一步很重要的优化
         * 如果之前链表添加的思维，哪我们会认为需要在链表的尾部添加节点
         * 这样子就会造成不必要的循环便利，极大降低了哈希表的插入。
         * 并且在刷新哈希表时会极其影响性能
         * 反过来想，如果将节点直接插入在头部，让新头部去指向旧头部，是不是也完成了一次插入操作
         * 并且插入的时间复杂度为O(1)
         */
        Entry<K, V> entry = table[index];
        Entry<K,V> newEntry = new Entry<>(hash,key,value);
        newEntry.next = entry;
        table[index] = newEntry;
        count++;
    }

    @Override
    public V get(K key) {
        int hash = hash(key);
        int index = index(hash);
        Entry<K, V> node = getNode(hash,index,key);
        return ObjectUtils.isEmpty(node) ? null : node.value;
    }

    private Entry<K,V> getNode(int hash,int index,K key){
        Entry<K, V> entry = table[index];
        /**
         * 遍历当前列表，是否存在指定元素
         * 如果存在，则覆盖value即可
         */
        for (;entry != null;entry = entry.next){
            if (equals(entry,hash,key)){
                return entry;
            }
        }
        return null;
    }

    /**
     * 删除指定节点
     * 时间复杂度为链表的长度
     */
    @Override
    public V remove(K key) {
        int hash = hash(key);
        int index = index(hash);
        Entry<K,V> preNode = null;
        Entry<K, V> entry = table[index];
        for (;entry != null;entry = entry.next){
            if (equals(entry,hash,key)){
                if (preNode == null){
                    //删除的是头结点的情况
                    table[index] = entry.next;
                }else {
                    preNode.next = entry.next;
                }
                --count;
                return entry.value;
            }
            preNode = entry;
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        int hash = hash(key);
        int index = index(hash);
        Entry<K, V> node = getNode(hash,index,key);
        return node != null;
    }

    @Override
    public int size() {
        return count;
    }

    @Override
    public boolean isEmpty() {
        return count == 0;
    }

    @Override
    public void clear() {
        /**
         * 清空所有哈希表的元素，时间复杂度是数组长度
         */
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }
        count = 0;
    }

    @Override
    public boolean putAll(Map<? extends K, ? extends V> map) {
        Iterator<? extends Map.Entry<? extends K, ? extends V>> iterator = map.iterator();
        if (iterator != null){
            while (iterator.hasNext()){
                Map.Entry<? extends K, ? extends V> next = iterator.next();
                put(next.getKey(),next.getValue());
            }
            return true;
        }
        return false;
    }

    @Override
    public Iterator<Map.Entry<K, V>> iterator() {
        return new HashTableIterator();
    }

    final class HashTableIterator implements Iterator<Map.Entry<K,V>>{

        /**
         * 遍历整个数组+列表即可
         */
        int index;

        Entry<K,V> currentNode;

        public HashTableIterator() {
            this.index = 0;
            //默认获取第0个位置上的节点
            currentNode = table[index];
            if (currentNode == null){
                //如果节点为空，则向下查找，直到找到节点
                for (int i = 1; i < table.length; ++i){
                    currentNode = table[i];
                    if (currentNode != null){
                        //如果找到新节点，则记录下标
                        index = i;
                        return;
                    }
                }
            }
        }

        @Override
        public boolean hasNext() {
            return currentNode != null;
        }

        @Override
        public Entry<K, V> next() {
            Entry<K,V> returnNode = currentNode;

            if (currentNode.next != null){
                //当前链表还存在下一个节点，则更新当前链表指针
                currentNode = currentNode.next;
            }else {
                //向下一个格子查找新节点
                index++;
                for (;index < table.length; ++index){
                    currentNode = table[index];
                    if (currentNode != null) {
                        //如果找到，则返回之间记录的节点
                        return returnNode;
                    }
                }
                //如果已经没有元素，将当前节点设置为空，防止 hasNext 异常返回
                currentNode = null;
            }

            return returnNode;
        }
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        Set<Map.Entry<K,V>> objectSet = CollectionConstructor.buildSet();
        Iterator<Map.Entry<K, V>> iterator = iterator();

        while (iterator.hasNext()){
            objectSet.add(iterator.next());
        }
        return objectSet;
    }

    @Override
    public Set<K> keySet() {
        Iterator<Map.Entry<K, V>> iterator = iterator();
        Set<K> objectSet = CollectionConstructor.buildSet();
        while (iterator.hasNext()){
            objectSet.add(iterator.next().getKey());
        }
        return objectSet;
    }

    @Override
    public Collection<V> values() {
        Iterator<Map.Entry<K, V>> iterator = iterator();
        Set<V> objectSet = CollectionConstructor.buildSet();
        while (iterator.hasNext()){
            objectSet.add(iterator.next().getValue());
        }
        return objectSet;
    }


    private boolean equals(Entry<K,V> entry,int hash,K key){
        if (entry.key == null){
            return key == null;
        }
        return entry.key.equals(key) && entry.hash == hash;
    }

    private int hash(K key){
        return key == null ? 0 : key.hashCode();
    }

    private int index(int hash) {
        return (hash & 0x7FFFFFFF) % table.length;
    }
}
