package utils.collection.list.order.child;

import utils.collection.list.List;
import utils.collection.list.child.ArrayList;
import utils.collection.list.order.OrderedList;
import utils.collection.map.sort.SkipLinkedListMap;
import utils.objects.ObjectUtils;

import java.util.*;

/**
 * 由跳跃列表实现的有序列表
 * Element元素要实现 Comparable 接口并且重写 equals 方法，才能正常进行使用
 */
public class SkipLinkedList<E extends Comparable<E>> implements OrderedList<E> {

    /**
     * 单向节点（双向节点会占用不必要的内存）
     */
    static final class Node<E>{
        E element;    //元素
        Node<E> next; //下一个节点

        public Node(E element, Node<E> next) {
            this.element = element;
            this.next = next;
        }

        @Override
        public String toString() {
            return element.toString();
        }

    }

    /**
     * 索引（单向索引）
     */
    static final class Index<E>{
        Node<E> node;   //索引的节点
        Index<E> down;  //下一层索引
        Index<E> right; //右侧索引

        public Index(Node<E> node, Index<E> down, Index<E> right) {
            this.node = node;
            this.down = down;
            this.right = right;
        }
    }

    private Index<E> head;

    private Index<E> bottomHead;

    /**
     * 当前跳表最高层数
     */
    private int level;

    private static final double PROBABILITY = 0.5; // 概率因子

    /**
     * 跳表节点最高层（设置成2的n次方），建议是16与32
     */
    private static final int MAX_LEVEL = 16;

    private int size;

    private Comparator<E> comparator;


    public SkipLinkedList(){
        this(null);
    }

    public SkipLinkedList(Comparator<E> comparator){
        this.comparator = comparator;
        size = 0;
        head = new Index<>(null,null,null);
        bottomHead = head;
    }

    @Override
    public boolean add(E element) {
        if (ObjectUtils.isEmpty(element)) return false;
        if (isEmpty()){
            head.node = new Node<>(element,null);
            ++size;
        }else {
            addVal(element);
        }
        return true;
    }

    private void addVal(E element){

        int newLevel = getLevel();
        /**
         * 如果 newLevel > level，则优先更新head的层次（向上递增）
         */
        for (;level < newLevel; ++level){
            //每个新的头索引不应该包含node，只保留自己的下一层索引
            head = new Index<>(null,head,null);
        }

        Index<E> currentIndex = head;

        /**
         * 记录在插入该节点后，会被影响的索引
         * 这些索引需要更新 right 指针，指向新索引
         * 新索引则需要继承这些索引的原 right
         * 注意：在这里面的索引索引，只是可能会受影响，主要取决于新节点的索引高度如何
         */
        Stack<Index<E>> parentIndexStack = new Stack<>();

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
                if (currentIndex == bottomHead) break;
                parentIndexStack.push(currentIndex);
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            //如果右侧存在索引
            int cmp = compare(element,currentIndex.right.node.element);
            if (cmp > 0){
                //如果 key > 下一个索引的key值，则更新当前索引成为右侧索引
                currentIndex = currentIndex.right;
            } else if (cmp == 0 && currentIndex.right.node.element.equals(element)){
                //如果存在相同元素，直接返回
                return;
            }else {
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
        Node<E> currentNode = currentIndex.node,
                parentNode = null,
                newNode = new Node<>(element,null);

        /**
         * 插入新节点
         */
        while (currentNode != null){
            int cmp = compare(element,currentNode.element);
            if (cmp <= 0){
                if (cmp == 0 && currentNode.element.equals(element)) return;
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
            Index<E> newIndex = null;
            for (int i = 0; i < newLevel; ++i){
                newIndex = new Index<>(newNode,newIndex,null);
                /**
                 * 受影响的索引能不能全部被处理，取决于新索引的层次
                 * 所以这里采用栈的方式，就是为了利用栈的先进后出特性，完成每个受影响索引的改动
                 */
                Index<E> parentIndex = parentIndexStack.pop();
                newIndex.right = parentIndex.right;
                parentIndex.right = newIndex;
            }
        }
        ++size;
    }

    private List<Node<E>> findNodes(E element){
        if (isEmpty() || ObjectUtils.isEmpty(element)) return null;
        Index<E> currentIndex = head;
        List<Node<E>> nodeList = new ArrayList<>();
        Node<E> currentNode = null;
        int cmp;
        for (;;){
            if (currentIndex.right == null){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            cmp = compare(element,currentIndex.right.node.element);
            if (cmp > 0){
                currentIndex = currentIndex.right;
            }else if (cmp < 0){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }else {
                break;
            }
        }

        if (currentIndex.node != null) {
            currentNode = currentIndex.node;
        }else {
            //如果等于空，可能是由于头索引的right索引就是我们查找的元素
            currentNode = bottomHead.node;
        }

        while (currentNode != null){
            cmp = compare(element,currentNode.element);
            if (cmp == 0){
                //说明找到了
                nodeList.add(currentNode);
                currentNode = currentNode.next;
                while (currentNode != null){
                    if (compare(element,currentNode.element) == 0){
                        nodeList.add(currentNode);
                        currentNode = currentNode.next;
                    }else break;
                }
                return nodeList;
            }else if (cmp > 0) {
                currentNode = currentNode.next;
            }else break;
        }
        return null;
    }

    private Node<E> findNode(E element){
        if (isEmpty() || ObjectUtils.isEmpty(element)) return null;
        Index<E> currentIndex = head;
        Node<E> currentNode = null;
        int cmp;
        for (;;){
            if (currentIndex.right == null){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            cmp = compare(element,currentIndex.right.node.element);
            if (cmp > 0){
                currentIndex = currentIndex.right;
            }else if (cmp < 0){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }else {
                if (currentIndex.right.node.element.equals(element)){
                    return currentIndex.right.node;
                }
                currentIndex = currentIndex.right;
            }
        }

        if (currentIndex.node != null) {
            currentNode = currentIndex.node;
        }else {
            //如果等于空，可能是由于头索引的right索引就是我们查找的元素
            currentNode = bottomHead.node;
        }

        while (currentNode != null){
            cmp = compare(element,currentNode.element);
            if (cmp == 0){
                if (currentNode.element.equals(element)){
                    return currentNode;
                }
                currentNode = currentNode.next;
            }else if (cmp > 0) {
                currentNode = currentNode.next;
            }else break;
        }
        return null;
    }


    @Override
    public E get(int index) {
        if (index >= size || isEmpty()) return null;
        Node<E> node = bottomHead.node;
        for (int i = 0; i < index; ++i){
            node = node.next;
        }
        return node.element;
    }

    @Override
    public E set(E oldElement, E newElement) {
        if (isEmpty()
                || (ObjectUtils.isEmpty(oldElement)
                || ObjectUtils.isEmpty(newElement))
                || compare(oldElement,newElement) != 0) return null;

        int cmp = 0;
        Index<E> index = head;
        for (;;){
            if (index.right == null){
                if (index.down == null) break;
                index = index.down;
                continue;
            }
            cmp = compare(oldElement,index.right.node.element);
            if (cmp > 0){
                index = index.right;
            }else if (cmp < 0){
                if (index.down == null) break;
                index = index.down;
            }else break;
        }

        Node<E> node = null;

        if (index.node != null){
            node = index.node;
        }else {
            node = bottomHead.node;
        }

        for (;;){
            if (node == null) return null;
            cmp = compare(oldElement,node.element);
            if (cmp > 0){
                node = node.next;
            }else if (cmp < 0) {
                return null;
            } else {
                if (node.element.equals(oldElement)){
                    //如果找到指定元素
                    E old = node.element;
                    node.element = newElement;
                    return old;
                }else {
                    //如果当前元素不是指定元素，则继续向下查找
                    node = node.next;
                }
            }
        }
    }

    @Override
    public List<E> get(E element) {
        List<Node<E>> nodes = findNodes(element);
        if (nodes != null){
            List<E> list = new ArrayList<>(nodes.size());
            for (int i = 0; i < nodes.size(); i++) {
                list.add(nodes.get(i).element);
            }
            return list;
        }
        return null;
    }

    @Override
    public E remove(int index) {
        if (index >= size || isEmpty()) return null;
        Node<E> node = bottomHead.node;
        for (int i = 0; i < index; ++i){
            node = node.next;
        }
        removeNode(node.element);
        return node.element;
    }

    @Override
    public boolean remove(E element) {
        return removeNode(element) != null;
    }

    /**
     *  在进行删除多个元素时，需要考虑以下因素：
     *  1、删除的元素在索引层中被发现
     *     - 1-1：创建两个队列（preIndexQueue,removeIndexQueue）
     *              - preIndexQueue：记录删除索引的上一个索引
     *              - removeIndexQueue：记录删除索引的最后一个索引（由于List支持重复，不保证第一个发现的索引就是需要删除的索引，所以我们还需要向右查找，直到找到最后一个索引）
     *     - 1-2：记录删除索引的上一个索引（保存在preIndexQueue），并且让删除索引向右查找，在保证右侧索引 != null && element = 右侧索引后，删除索引向右移动，找到最后一个索引（保存在removeIndexQueue中）
     *              - 向下查找
     *                  - 如果找到还存在下一层索引，让当前索引进行下一层索引，进行执行1-2的流程
     *                  - 如果不存在下一层，进行节点层删除节点
     *     - 1-3：在节点层中，获取当前索引的节点，并且让该节点向后查找。找到真正需要删除的第一个节点，并且记录下上一个节点
     *     - 1-4：找到第一个需要删除的节点后，继续向后查找，找到最后一个需要删除的节点，让上一个节点直接指向末尾删除节点的next
     *     - 1-5：将队列中的内容取出，preIndex.right = removeIndex.right（这里两个队列的个数应该相同！）
     *  2、删除的元素在节点层（未在索引层发现）
     *     - 2-1：在节点层中，获取当前索引的节点，并且让该节点向后查找。找到真正需要删除的第一个节点，并且记录下上一个节点
     *     - 2-2：找到第一个需要删除的节点后，继续向后查找，找到最后一个需要删除的节点，让上一个节点直接指向末尾删除节点的next
     */
    @Override
    public int removeAll(E element) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return 0;
        List<E> list = removeSame(element);
        if (list != null){
            size = size - list.size();
            return list.size();
        }else return 0;
    }

    /**
     * 删除单个元素
     * @param element
     * @return
     */
    private E removeNode(E element){
        if (isEmpty() || ObjectUtils.isEmpty(element)) return null;
        int cmp = 0;
        //如果删除的节点就是头索引第0层所对应节点
//        cmp = compare(element, bottomHead.node.element);
//        if (cmp == 0){
//            Node<E> removeNode = bottomHead.node;
//            if (removeNode.element.equals(element)){
//                bottomHead.node = removeNode.next;
//                head.node = removeNode.next;
//                size--;
//                if (isEmpty()){
//                    /**
//                     * 则应该清楚整个跳表
//                     */
//                    clear();
//                }
//                return removeNode.element;
//            }else {
//                Node<E> preNode = removeNode;
//                removeNode = removeNode.next;
//                while (removeNode != null){
//                    cmp = compare(element,removeNode.element);
//                    if (cmp != 0){
//                        break;
//                    }
//                    if (removeNode.element.equals(element)){
//                        preNode.next = removeNode.next;
//                        return removeNode.element;
//                    }
//                    preNode = removeNode;
//                    removeNode = removeNode.next;
//                }
//            }
//        }

        Index<E> currentIndex = head;

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
            cmp = compare(element,currentIndex.right.node.element);
            if (cmp > 0){
                //如果key > 当前节点 key,向右边移动
                currentIndex = currentIndex.right;
            }else if (cmp < 0){
                //如果key < 当前节点 key,向下层移动
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }else if (currentIndex.right.node.element.equals(element)){
                /**
                 * 如果相等，说明在索引处找到需要删除的节点
                 * 这个队列是用于保存在删除节点后，会受影响的索引
                 * 注意：这里所有被记录的索引都会受到删除节点的影响
                 */
                Queue<Index<E>> queue = new LinkedList<>();
                /**
                 * 因为right就是需要删除的索引
                 * 保存当前的索引，用于修复 right 指针
                 */
                queue.add(currentIndex);

                /**
                 * 记录删除索引的最上层（之后在删除当前索引后，需要修复删除索引的 right）
                 */
                Index<E> removeHeadIndex = currentIndex.right;

                /**
                 * 向下查找，找到那些因为删除索引受影响的索引
                 */
                while (currentIndex.down != null && currentIndex.down != bottomHead){
                    currentIndex = currentIndex.down;
                    down:while (currentIndex.right != null){
                        /**
                         * 如果能来到这里，说明下层一定有与删除索引关联的索引
                         * 所以这里只会出现两种情况
                         *  - 要么 key  > 右侧索引（向右移动）
                         *  - key = 右侧索引（记录当前索引，并修改 currentIndex）
                         */
                        cmp = compare(element,currentIndex.right.node.element);
                        if (cmp > 0){
                            currentIndex = currentIndex.right;
                        }else if (currentIndex.right.node.element.equals(element)){
                            queue.add(currentIndex);
                            break down;
                        }else currentIndex = currentIndex.right;
                    }
                }

                //删除节点的上一个节点
                Node<E> preNode = currentIndex.node == null ? bottomHead.node : currentIndex.node;

                //被删除的节点
                Node<E> removeNode = currentIndex.right.node;

                while (removeNode.next != null) {
                    if (compare(element,removeNode.element) == 0){
                        break;
                    }
                    removeNode = removeNode.next;
                }

                if (preNode == removeNode) {
                    if (currentIndex.node == null){
                        bottomHead.node = removeNode.next;
                    }else {
                        currentIndex.node = removeNode.next;
                    }
                }else {
                    //由于是在索引层找到，所以还需要精确确定到删除节点的上一个节点
                    while (preNode.next != removeNode) {
                        preNode = preNode.next;
                    }
                    preNode.next = removeNode.next;
                }

                //修勾受影响的索引
                while (!queue.isEmpty()){
                    Index<E> poll = queue.poll();
                    poll.right = removeHeadIndex.right;
                    removeHeadIndex = removeHeadIndex.down;
                }
                size--;
                return removeNode.element;
            }else {
                currentIndex = currentIndex.right;
            }
        }

        Node<E> preNode = null;

        if (currentIndex.node != null) {
            /**
             * 能来到这里，说明删除的节点并不是一个索引层的节点
             * 获取当前索引
             */
            preNode = currentIndex.node;
        }else {
            preNode = bottomHead.node;
        }

        //这里就是采用列表的方式查找删除了
        while (preNode.next != null){
            cmp = compare(element,preNode.next.element);
            //如果key比下一个节点的值大
            if (cmp > 0){
                preNode = preNode.next;
            }else if (cmp < 0){
                //说明key比下一个节点的值小，找不到元素，直接返回
                return null;
            }else if (preNode.next.element.equals(element)){
                //如果相等,说明下一个节点就是需要删除的节点，使用单向链表的删除方式即可
                Node<E> removeNode = preNode.next;
                preNode.next = removeNode.next;
                --size;
                return removeNode.element;
            }else {
                preNode = preNode.next;
            }
        }
        return null;
    }

    /**
     * 删除索引所有相同的元素
     * @param element
     * @return
     */
    private List<E> removeSame(E element){
        Index<E> currentIndex = head;
        int cmp = 0;
        List<E> list = new ArrayList<>();
        for (;;){
            if (currentIndex.right == null){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
                continue;
            }
            cmp = compare(element,currentIndex.right.node.element);
            if (cmp > 0){
                currentIndex = currentIndex.right;
            }else if (cmp < 0){
                if (currentIndex.down == null) break;
                currentIndex = currentIndex.down;
            }else {
                /**
                 * 记录删除索引后受影响的索引
                 */
                Queue<Index<E>> preIndexQueue = new LinkedList<>();
                /**
                 * 记录删除的索引
                 */
                Queue<Index<E>> removeIndexQueue = new LinkedList<>();

                for (;;){
                    if (currentIndex.right == null){
                        if (currentIndex.down == null) break;
                        currentIndex = currentIndex.down;
                        continue;
                    }

                    cmp = compare(element,currentIndex.right.node.element);

                    if (cmp == 0){
                        preIndexQueue.add(currentIndex);
                        Index<E> removeIndex = currentIndex.right;
                        if (removeIndex.right != null && compare(element,removeIndex.right.node.element) == 0){
                            //说明右边还存在相同的索引
                            while (removeIndex.right != null){
                                if (compare(element,removeIndex.right.node.element) == 0) removeIndex = removeIndex.right;
                                else break;
                            }
                            removeIndexQueue.add(removeIndex);
                        }else {
                            removeIndexQueue.add(removeIndex);
                        }
                        if (currentIndex.down == null) break;
                        currentIndex = currentIndex.down;
                    }else if (cmp > 0){
                        currentIndex = currentIndex.right;
                    }
                }

                Node<E> preNode = null;
                Node<E> removeNode = null;
                if (currentIndex.node != null){
                    removeNode = currentIndex.node;
                }else {
                    removeNode = bottomHead.node;
                }

                //选出第一个被删除的节点
                for (;;){
                    if (compare(element,removeNode.element) > 0) {
                        preNode = removeNode;
                        removeNode = removeNode.next;
                    }
                    else break;
                }

                list.add(removeNode.element);

                //找到最后一个需要被删除的节点
                while (removeNode.next != null){
                    if (compare(element,removeNode.next.element) == 0){
                        removeNode = removeNode.next;
                        list.add(removeNode.element);
                    }else break;
                }

                /**
                 * 删除节点
                 */
                if (preNode == null){
                    if (currentIndex.node == null){
                        bottomHead.node = removeNode.next;
                    }else {
                        currentIndex.node = removeNode.next;
                    }
                }else {
                    preNode.next = removeNode.next;
                }

                while (!preIndexQueue.isEmpty()){
                    Index<E> pre = preIndexQueue.poll();
                    Index<E> removeIndex = removeIndexQueue.poll();
                    pre.right = removeIndex.right;
                }

                return list;
            }
        }
        Node<E> preNode = null;
        Node<E> removeNode = null;
        if (currentIndex.node != null){
            removeNode = currentIndex.node;
        }else {
            removeNode = bottomHead.node;
        }

        for (;;){
            if (removeNode == null) return null;
            cmp = compare(element,removeNode.element);
            if (cmp > 0){
                preNode = removeNode;
                removeNode = removeNode.next;
            }else if (cmp < 0) return null;
            else break;
        }

        list.add(removeNode.element);

        while (removeNode.next != null){
            cmp = compare(element,removeNode.next.element);
            if (cmp == 0){
                removeNode = removeNode.next;
                list.add(removeNode.element);
            }else break;
        }

        if (preNode == null){
            if (currentIndex.node == null){
                bottomHead.node = removeNode.next;
            }else {
                currentIndex.node = removeNode.next;
            }
        }else {
            preNode.next = removeNode.next;
        }
        return list;
    }

    /**
     * 范围删除
     */
    @Override
    public List<E> removeRange(E start, E end) {
       if (isEmpty() || (ObjectUtils.isEmpty(start) || ObjectUtils.isEmpty(end))) return null;
       if (compare(start,end) == 0){
           //说明删除的是同一个元素
           List<E> list = removeSame(start);
           size = list == null ? size : size - list.size();
           return list;
       }else {

           if (compare(start,end) > 0){
               E temp = start;
               start = end;
               end = temp;
           }

           int sCmp,eCmp;
           /**
            * 记录受影响的索引
            */
           Queue<Index<E>> preIndexQueue = new LinkedList<>();
           /**
            * 记录被删除的索引
            */
           Queue<Index<E>> removeIndexQueue = new LinkedList<>();

           Index<E> currentIndex = head;

           for (;;){
               if (currentIndex.right == null){
                   if (currentIndex.down == null) break;
                   currentIndex = currentIndex.down;
                   continue;
               }
               sCmp = compare(start,currentIndex.right.node.element);
               eCmp = compare(end,currentIndex.right.node.element);

               if (sCmp > 0) {
                   //如果 start > 当前元素，那么 end 一定也大于当前元素
                   currentIndex = currentIndex.right;
               }else if (eCmp < 0){
                   //如果 end < 当前元素，那么 start 一定也小于当前元素
                   if (currentIndex.down == null) break;
                   currentIndex = currentIndex.down;
               } else if (sCmp == 0 || (sCmp < 0 && eCmp > 0)){
                   //如果找到start索引 || 这个索引在 start 与 end 之间
                   preIndexQueue.add(currentIndex);
                   Index<E> removeIndex = currentIndex.right;

                   if (removeIndex.right != null){
                       for (;;){
                           if (removeIndex.right == null) break;
                           sCmp = compare(removeIndex.right.node.element,end);
                           //如果下一个节点已经 > end，直接退出
                           if (sCmp > 0) break;
                           else {
                               removeIndex = removeIndex.right;
                           }
                       }
                       removeIndexQueue.add(removeIndex);
                   }else {
                       removeIndexQueue.add(removeIndex);
                   }

                   if (currentIndex.down == null) break;
                   currentIndex = currentIndex.down;

               }else if (eCmp == 0){
                   //如果找到 end 索引
                   preIndexQueue.add(currentIndex);
                   Index<E> removeIndex = currentIndex.right;
                   while (removeIndex.right != null){
                       if (compare(end,removeIndex.right.node.element) == 0) removeIndex = removeIndex.right;
                       break;
                   }
                   removeIndexQueue.add(removeIndex);
                   if (currentIndex.down == null) break;
                   currentIndex = currentIndex.down;
               }
           }

           Node<E> preNode = null;
           Node<E> removeNode = null;

           if (currentIndex.node != null){
               removeNode = currentIndex.node;
           }else {
               removeNode = bottomHead.node;
           }

           List<E> list = new ArrayList<>();

           /**
            * 确定 preNode 节点的位置
            */
           for (;;){
               if (removeNode == null) return null;
               sCmp = compare(start,removeNode.element);
               if (sCmp > 0){
                   preNode = removeNode;
                   removeNode = removeNode.next;
               }else {
                   if (compare(removeNode.element,end) > 0) {
                       //如果当前元素已经比end还大，直接返回
                       return null;
                   }else break;
               }
           }

           list.add(removeNode.element);

           //能到这里，说明是存在 start 节点，那么从 start - end 这段距离上的节点，都是删除的节点
           while (removeNode.next != null){
               eCmp = compare(end,removeNode.next.element);
               if (eCmp >= 0){
                   //只要比 end <= 的 都列为删除节点
                   removeNode = removeNode.next;
                   list.add(removeNode.element);
               }else break; //超出范围，找到
           }

           if (preNode == null){
               if (currentIndex.node == null){
                   bottomHead.node = removeNode.next;
               }else {
                   currentIndex.node = removeNode.next;
               }
           }else {
               preNode.next = removeNode.next;
           }

           size = size - list.size();

           while (!preIndexQueue.isEmpty()){
               Index<E> preIndex = preIndexQueue.poll();
               Index<E> removeIndex = removeIndexQueue.poll();
               preIndex.right = removeIndex.right;
           }

           return list;
       }
    }

    @Override
    public List<E> searchRange(E start, E end) {
        if (isEmpty() || (ObjectUtils.isEmpty(start) || ObjectUtils.isEmpty(end))) return null;

        int cmp = compare(start, end);

        if (cmp == 0){
            //如果查询的范围相等，则根据查询当个即可
            return get(start);
        }else if (cmp > 0){
            //如果 end > start,交换两个顺序
            E temp = start;
            start = end;
            end = temp;
        }

        Index<E> index = head;
        for (;;){
            if (index.right == null){
                if (index.down == null) break;
                index = index.down;
                continue;
            }
            cmp = compare(start, index.right.node.element);
            if (cmp > 0){
                index = index.right;
            }else if (cmp < 0){
                if (index.down == null) break;
                index = index.down;
            }else break;
        }
        Node<E> node = null;

        if (index.node == null){
            node = bottomHead.node;
        }else {
            node = index.node;
        }

        //找出起始点
        for (;;){
            if (node == null) return null;
            cmp = compare(start,node.element);
            if (cmp > 0){
                node = node.next;
            }else {
                if (compare(node.element,end) > 0) {
                    //如果当前元素已经比end还大，直接返回
                    return null;
                }else break;
            }
        }

        List<E> list = new ArrayList<>();

        list.add(node.element);

        while (node.next != null){
            cmp = compare(end,node.next.element);
            if (cmp >= 0){
                node = node.next;
                list.add(node.element);
            }else break;
        }

        return list;
    }

    @Override
    public List<E> ceiling(E element, boolean contain) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return null;
        Index<E> index = head;
        int cmp;
        for (;;){
            if (index.right == null){
                if (index.down == null) break;
                index = index.down;
                continue;
            }
            cmp = compare(element,index.right.node.element);
            if (cmp > 0){
                index = index.right;
            }else if (cmp < 0){
                if (index.down == null) break;
                index = index.down;
            }else break;
        }

        Node<E> node = null;
        if (index.node != null){
            node = index.node;
        }else {
            node = bottomHead.node;
        }

        for (;;){
            if (node == null) return null;
            cmp = compare(element,node.element);
            if (cmp <= 0){
                //说明已经找到 >= element 的元素
                if (!contain){
                    //如果不包含相同的，则过滤掉 = 的元素
                    node = node.next;
                    for (;;){
                        if (node == null) return null;
                        cmp = compare(element,node.element);
                        if (cmp == 0){
                            node = node.next;
                        }else break;
                    }
                }
                List<E> list = new ArrayList<>();
                while (node != null){
                    list.add(node.element);
                    node = node.next;
                }
                return list;
            }else node = node.next;
        }

    }

    @Override
    public List<E> floor(E element, boolean contain) {
        if (isEmpty() || ObjectUtils.isEmpty(element)) return null;
        Index<E> index = head;
        int cmp;
        for (;;){
            if (index.right == null){
                if (index.down == null) break;
                index = index.down;
                continue;
            }
            cmp = compare(element,index.right.node.element);
            if (cmp > 0){
                index = index.right;
            }else if (cmp < 0){
                if (index.down == null) break;
                index = index.down;
            }else break;
        }

        Node<E> node = null;
        if (index.node != null){
            node = index.node;
        }else {
            node = bottomHead.node;
        }

        //找出相同的节点
        for (;;){
            if (node == null) break;
            cmp = compare(element,node.element);
            if (cmp > 0){
                node = node.next;
            }else break;
        }

        List<E> list = new ArrayList<>();
        Node<E> headNode = bottomHead.node;
        for (;;){
            if (headNode == null) break;
            //从头结点开始，获取到 < element 的元素
            cmp = compare(element,headNode.element);
            if (cmp > 0){
                list.add(headNode.element);
                headNode = headNode.next;
            }else break;
        }

        if (contain){
            for (;;){
                if (node == null) break;
                cmp= compare(element,node.element);
                if (cmp == 0){
                    list.add(node.element);
                    node = node.next;
                }else break;
            }
        }

        return list;
    }

    @Override
    public E getMaxElement() {
        if (isEmpty()) return null;
        Index<E> index = head;
        for (;;){
            if (index.right == null){
                if (index.down == null) break;
                index = index.down;
                continue;
            }
            index = index.right;
        }
        Node<E> node = index.node == null ? bottomHead.node : index.node;
        while (node.next != null) node = node.next;
        return node.element;
    }

    @Override
    public E getMinElement() {
        if (isEmpty()) return null;
        return bottomHead.node.element;
    }


    @Override
    public boolean contains(E element) {
        return findNode(element) != null;
    }

    @Override
    public void reversal() {
        throw new RuntimeException("SkipLinkedList Not Supported reversal");
        //......
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        head = new Index<>(null,null,null);
        bottomHead = head;
        level = 0;
        size = 0;
    }

    @Override
    public Iterator<E> iterator() {
        return new SkipIterator<>(bottomHead.node);
    }

    final static class SkipIterator<E> implements Iterator<E>{

        private Node<E> node;


        public SkipIterator(Node<E> node) {
            this.node = node;
        }

        @Override
        public boolean hasNext() {
            return node != null;
        }

        @Override
        public E next() {
            E temp = node.element;
            node = node.next;
            return temp;
        }
    }


    private int compare(E k1,E k2){
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
