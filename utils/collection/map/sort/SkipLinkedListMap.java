package utils.collection.map.sort;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;
import utils.collection.map.AbstractMap;
import utils.collection.map.Map;
import utils.collection.set.Set;
import utils.objects.ObjectUtils;

import java.security.Key;
import java.util.*;

/**
 * 基于跳表实现的有序集合
 */
public class SkipLinkedListMap<K,V> extends AbstractMap<K,V> {

    /**
     * 节点，存放元素，下一个节点的地址
     */
    final static class Node<K,V>{
        K key;
        V value;
        Node<K,V> next;

        public Node(K key, V value, Node<K, V> next) {
            this.key = key;
            this.value = value;
            this.next = next;
        }

        @Override
        public String toString() {
            return "Key：" + key + "，Value：" + value;
        }
    }

    /**
     * 索引
     * 索引包含右侧索引，下层索引，当前索引对应的Node
     */
    final static class Index<K,V>{
        Node<K,V> node;
        Index<K,V> down;
        Index<K,V> right;

        public Index(Node<K, V> node, Index<K, V> down, Index<K, V> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }
    }

    /**
     * 头索引
     */
    private Index<K,V> head;

    /**
     * 头索引的第0层索引
     */
    private Index<K,V> headLast;

    /**
     * 当前跳表的层数
     */
    private int level;

    //比较器对象
    private Comparator<K> comparator;

    private static final double PROBABILITY = 0.5; // 概率因子

    //设置层数最高层
    private static final int MAX_LEVEL = 8;

    public SkipLinkedListMap() {
        this(null);
    }

    public SkipLinkedListMap(Comparator<K> comparator) {
        this.comparator = comparator;
        //优先创建第0层索引（改层存放索引链表节点）
        head = new Index<>(null,null,null);
        headLast = head;
        level = 0;
    }

    @Override
    public void put(K key, V value) {
        if (isEmpty()){
            /**
             * 如果当前没有节点，则让新节点插入到头索引的第0层上
             */
            head.node = new Node<>(key,value,null);
            ++size;
        }else {
            doPut(key,value);
        }
    }

    private void doPut(K key,V value){
        //随机获取当前节点生成的索引数
        int newLevel = getLevel();

        /**
         * 如果 newLevel > level，则优先更新head的层次（向上递增）
         */
        for (;level < newLevel; ++level){
            //每个新的头索引不应该包含node，只保留自己的下一层索引
            head = new Index<>(null,head,null);
        }

        Index<K,V> currentIndex = head;

        /**
         * 记录在插入该节点后，会被影响的索引
         * 这些索引需要更新 right 指针，指向新索引
         * 新索引则需要继承这些索引的原 right
         * 注意：在这里面的索引索引，只是可能会受影响，主要取决于新节点的索引高度如何
         */
        Stack<Index<K,V>> parentIndexStack = new Stack<>();

        /**
         * 从头索引开始，查找需要新节点插入的位置
         */
        for (;;){
            /**
             * 如果发现右侧索引为空，则向下一层索引进行查找
             */
            if (currentIndex.right == null){
                /**
                 * 如果当前索引不是头索引的第0层索引
                 * 则将当前索引标记为受影响的索引
                 * 并向下一层出发继续查找
                 */
                if (currentIndex == headLast) break;
                parentIndexStack.push(currentIndex);
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            //如果右侧存在索引
            int cmp = compare(key,currentIndex.right.node.key);
            if (cmp > 0){
                //如果 key > 下一个索引的key值，则更新当前索引成为右侧索引
                currentIndex = currentIndex.right;
            } else if (cmp == 0){
                //如果找到相同的节点，只更新value不做任何处理
                currentIndex.right.node.value = value;
                return;
            } else {
                /**
                 * 如果 key < 右侧索引，说明新添加的节点在这段范围内
                 * 将当前索引标记为受影响索引，向下查找
                 * 注意：如果存在右侧索引，那么它一定不是头索引的第0层索引
                 */
                parentIndexStack.push(currentIndex);
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }
        }

        /**
         * currentNode：当前索引对应的节点
         * parentNode：上一个节点
         * newNode：新节点
         */
        Node<K,V> currentNode = currentIndex.node,
                parentNode = null,
                newNode = new Node<>(key,value,null);

        /**
         * 插入新节点
         */
        while (currentNode != null){
            int cmp = compare(key,currentNode.key);
            if (cmp < 0){
                /**
                 * key < 当前节点的key，则找到插入地方
                 */
                if (parentNode == null){
                    //如果不存在上一个节点，说明插入的节点是在索引的下一个节点上，更新索引的node，并且让新node去指向旧的索引node
                    newNode.next = currentIndex.node;
                    currentIndex.node = newNode;
                }else {
                    //采用链表的方式添加新节点即可
                    newNode.next = parentNode.next;
                    parentNode.next = newNode;
                }
                break;
            }else if (cmp == 0){
                currentNode.value = value;
                return;
            }
            //如果 key > 当前节点key，向下一个节点出发
            parentNode = currentNode;
            currentNode = currentNode.next;
        }

        //如果没有找到一个合适的插入点，则插入在末尾元素中
        if (currentNode == null) parentNode.next = newNode;

        /**
         * 如果当前节点有层次
         * 则应该去修复索引之间的right指针
         */
        if (newLevel > 0){
            Index<K,V> newIndex = null;
            for (int i = 0; i < newLevel; ++i){
                newIndex = new Index<>(newNode,newIndex,null);
                /**
                 * 受影响的索引能不能全部被处理，取决于新索引的层次
                 * 所以这里采用栈的方式，就是为了利用栈的先进后出特性，完成每个受影响索引的改动
                 */
                Index<K,V> parentIndex = parentIndexStack.pop();
                newIndex.right = parentIndex.right;
                parentIndex.right = newIndex;
            }
        }
        ++size;
    }


    @Override
    public V get(K key) {
        Node<K, V> node = findNode(key);
        return node != null ? node.value : null;
    }

    /**
     * 根据 key 查找节点Node
     *  - 从头索引开始查找
     *    - 如果头索引不存在右侧索引，则更新头索引为下一层索引，继续查找
     *    - 如果头索引存在右侧索引
     *      - key > 右侧索引节点key。头索引 = 右侧索引
     *      - key < 右侧索引节点key。则更新头索引到下一层索引上，继续查找
     *      - key = 右侧索引节点key，返回右侧节点
     *  - 如果在索引层没有找到指定节点，则根据单向链表的方式来查找
     *    要注意：查找的范围是有限的（当前索引节点 - 右侧索引节点）这段距离上
     *    如果没有这个限制，那么就回到单向链表的查找时间复杂度
     *
     */
    private Node<K,V> findNode(K key){
        if (isEmpty()) return null;
        Index<K,V> currentIndex = head;
        int cmp;
        for (;;){
            if (currentIndex.right == null){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            cmp = compare(key,currentIndex.right.node.key);
            if (cmp > 0){
                currentIndex = currentIndex.right;
            }else if (cmp < 0){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }else {
                return currentIndex.right.node;
            }
        }

        Node<K,V> node = currentIndex.node;

        /**
         * 边界节点（要注意：链表的查找范围应该是 当前索引节点-右边索引之间的范围，而不是整个链表）
         */
        Node<K,V> boundaryNode = null;

        if (currentIndex.right != null){
            if (compare(key,currentIndex.right.node.key) == 0){
                return currentIndex.right.node;
            }
            boundaryNode = currentIndex.right.node;
        }

        while (node != boundaryNode){
            cmp = compare(key,node.key);
            if (cmp > 0) node = node.next;
            else if (cmp < 0) return null;
            else return node;
        }

        return null;

    }

    /**
     * 删除节点
     */
    @Override
    public V remove(K key) {

        if (isEmpty()) return null;

        int cmp = 0;
        //如果删除的节点就是头索引第0层所对应节点
        cmp = compare(key, headLast.node.key);
        if (cmp == 0){
            Node<K,V> removeNode = headLast.node;
            headLast.node = removeNode.next;
            head.node = headLast.node;
            size--;
            if (isEmpty()){
                /**
                 * 则应该清楚整个跳表
                 */
                clear();
            }
            return removeNode.value;
        }

        Index<K,V> currentIndex = head;

        /**
         * 查找要删除的节点
         */
        for (;;){
            if (currentIndex.right == null){
                //如果没有右边索引
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            //比较两个Key的大小
            cmp = compare(key,currentIndex.right.node.key);
            if (cmp > 0){
                //如果key > 当前节点 key,向右边移动
                currentIndex = currentIndex.right;
            }else if (cmp < 0){
                //如果key < 当前节点 key,向下层移动
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }else {

                /**
                 * 如果相等，说明在索引处找到需要删除的节点
                 * 这个队列是用于保存在删除节点后，会受影响的索引
                 * 注意：这里所有被记录的索引都会受到删除节点的影响
                 */
                Queue<Index<K,V>> queue = new LinkedList<>();
                /**
                 * 因为right就是需要删除的索引
                 * 保存当前的索引，用于修复 right 指针
                 */
                queue.add(currentIndex);

                /**
                 * 记录删除索引的最上层（之后在删除当前索引后，需要修复删除索引的 right）
                 */
                Index<K,V> removeHeadIndex = currentIndex.right;

                /**
                 * 向下查找，找到那些因为删除索引受影响的索引
                 */
                while (currentIndex.down != null && currentIndex.down != headLast){
                    currentIndex = currentIndex.down;
                    down:while (currentIndex.right != null){
                        /**
                         * 如果能来到这里，说明下层一定有与删除索引关联的索引
                         * 所以这里只会出现两种情况
                         *  - 要么 key  > 右侧索引（向右移动）
                         *  - key = 右侧索引（记录当前索引，并修改 currentIndex）
                         */
                        cmp = compare(key,currentIndex.right.node.key);
                        if (cmp > 0){
                            currentIndex = currentIndex.right;
                        }else {
                            queue.add(currentIndex);
                            break down;
                        }
                    }
                }

                //删除节点的上一个节点
                Node<K,V> preNode = currentIndex.node;

                //被删除的节点
                Node<K,V> removeNode = currentIndex.right.node;

                /**
                 * 修复正确的删除节点
                 */
                while (removeNode.next != null) {
                    if (compare(key,removeNode.key) == 0){
                        break;
                    }
                    removeNode = removeNode.next;
                }

                //由于是在索引层找到，所以还需要精确确定到删除节点的上一个节点
                while (preNode.next != removeNode) preNode = preNode.next;

                preNode.next = removeNode.next;

                //修勾受影响的索引
                while (!queue.isEmpty()){
                    Index<K, V> poll = queue.poll();
                    poll.right = removeHeadIndex.right;
                    removeHeadIndex = removeHeadIndex.down;
                }
                size--;
                return removeNode.value;
            }
        }

        Node<K, V> preNode = null;
        if (currentIndex.node != null) {
            /**
             * 能来到这里，说明删除的节点并不是一个索引层的节点
             * 获取当前索引
             */
            preNode = currentIndex.node;
        }else {
            preNode = headLast.node;
        }

        //这里就是采用列表的方式查找删除了
        while (preNode.next != null){
            cmp = compare(key,preNode.next.key);
            //如果key比下一个节点的值大
            if (cmp > 0){
                preNode = preNode.next;
            }else if (cmp < 0){
                //说明key比下一个节点的值小，找不到元素，直接返回
                return null;
            }else {
                //如果相等,说明下一个节点就是需要删除的节点，使用单向链表的删除方式即可
                Node<K,V> removeNode = preNode.next;
                preNode.next = removeNode.next;
                --size;
                return removeNode.value;
            }
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        return !(get(key) == null);
    }

    @Override
    public void clear() {
        head = new Index<>(null,null,null);
        headLast = head;
        level = 0;
        size = 0;
    }

    @Override
    public Iterator<Entry<K, V>> iterator() {
        return new SkipIterator<>(headLast.node);
    }

    final static class SkipIterator<K,V> implements Iterator<Entry<K,V>>{

        private Node<K,V> node;

        public SkipIterator(Node<K, V> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public Entry<K, V> next() {
            Node<K,V> temp = node;
            node = node.next;
            return new MapEnter<>(temp.key,temp.value);
        }
    }


    /**
     * 范围查找（查找start-end之间的范围数据）
     */
    public List<Entry<K,V>> rangeSearch(K start, K end){
        if (compare(start,end) > 0){
            //如果end > start,交换两个顺序
            K temp = start;
            start = end;
            end = temp;
        }
        Node<K, V> startNode = findNode(start);
        Node<K, V> endNode = findNode(end);

        if (ObjectUtils.isEmpty(startNode) || ObjectUtils.isEmpty(endNode)) return null;

        List<Entry<K,V>> list = new ArrayList<>();

        while (startNode != null && startNode.next != endNode){
            list.add(new MapEnter<>(startNode.key, startNode.value));
            startNode = startNode.next;
        }

        list.add(new MapEnter<>(endNode.key, endNode.value));

        return list;
    }

    private int compare(K k1,K k2){
        if (comparator != null) return comparator.compare(k1,k2);
        else return  ((Comparable)k1).compareTo(k2);
    }

    /**
     * 随机生成每个节点新的高度
     * 使用 Math.random() 方法生成随机数来决定新元素是否应该插入到每一层中。
     * 通过不断生成概率小于 PROBABILITY（默认为 0.5）的随机数，
     * 可以得到一个类似二项分布的概率分布，从而保证了跳表高度的稳定性。
     */
    private int getLevel(){
        int level = 1;
        // 当 level < MAX_LEVEL，且随机数小于设定的晋升概率时，level + 1
        while (Math.random() < PROBABILITY && level < MAX_LEVEL)
            level += 1;
        return level;
    }
}
