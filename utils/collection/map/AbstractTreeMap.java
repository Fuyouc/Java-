package utils.collection.map;

import utils.collection.Collection;
import utils.collection.CollectionConstructor;
import utils.collection.list.List;
import utils.collection.set.Set;
import utils.objects.ObjectUtils;
import utils.collection.map.tree.printer.BinaryTreeInfo;

import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 抽象的树形Map
 * @param <K>
 * @param <V>
 */
public abstract class AbstractTreeMap<K,V> implements Map<K,V>, BinaryTreeInfo {

    protected Comparator<K> comparator;

    protected int size;

    protected Node<K,V> root;

    public AbstractTreeMap() {
        this(null);
    }

    public AbstractTreeMap(Comparator<K> comparator) {
        this.comparator = comparator;
    }


    @Override
    public Object root() {
        return root;
    }

    @Override
    public Object left(Object node) {
        return ((Node)node).left;
    }

    @Override
    public Object right(Object node) {
        return ((Node)node).right;
    }

    @Override
    public Object string(Object node) {
        return ((Node)node).value;
    }

    protected int compare(K k1,K k2){
        if (comparator != null) return comparator.compare(k1,k2);
        return ((Comparable)k1).compareTo(k2);
    }

    protected Node<K,V> createNode(K key, V value, Node<K,V> parent){
        return new Node<>(key,value,parent);
    }

    protected Node<K,V> getNode(K key){
        Node<K,V> node = root;
        while (node != null){
            int cmp = compare(key,node.key);
            if (cmp > 0){
                node = node.right;
            }else if (cmp < 0){
                node = node.left;
            }else return node;
        }
        return null;
    };

    /**
     * 获取当前节点的前驱节点
     * @param node
     * @return
     */
    protected Node<K,V> getPrecursorNode(Node<K,V> node){
        if (node == null) return null;

        if (node.left != null){
            node = node.left;
        }else return node;

        while (node.right != null) node = node.right;
        return node;
    }

    /**
     * 获取当前节点的后继节点
     * @param node
     * @return
     */
    protected Node<K,V> getSuccessor(Node<K,V> node){
        if (node == null) return null;
        if (node.right != null){
            node = node.right;
        }else return node;
        while (node.left != null) node = node.left;
        return node;
    }

    @Override
    public boolean putAll(Map<? extends K, ? extends V> map) {
        Iterator<? extends Entry<? extends K, ? extends V>> iterator = map.iterator();
        if (ObjectUtils.isEmpty(iterator)) return false;
        while (iterator.hasNext()){
            Entry<? extends K, ? extends V> entry = iterator.next();
            put(entry.getKey(),entry.getValue());
        }
        return true;
    }

    protected static class Node<K,V>{
        public Node<K,V> parent;
        public Node<K,V> left,right;
        public K key;
        public V value;

        public Node(K key, V value,Node<K,V> parent) {
            this.key = key;
            this.value = value;
            this.parent = parent;
        }

        /**
         * 当前节点是否是左子节点
         * @return
         */
        public boolean isLeft(){
            if (parent != null){
                return parent.left == this;
            }
            return false;
        }

        /**
         * 当前节点是否是右子节点
         * @return
         */
        public boolean isRight(){
            if (parent != null){
                return parent.right == this;
            }
            return false;
        }

        /**
         * 当前节点是否是根节点
         * @return
         */
        public boolean isRoot(){
            return parent == null;
        }

        /**
         * 当前节点是否是叶子节点
         * @return
         */
        public boolean isLeaf(){
            return left == null && right == null;
        }
    }

    protected interface NodeListener<K,V>{
        void getNode(Node<K,V> node);
    }

    protected enum ForeachType{
        PREORDER,
        MIDDLE,
        AFTERORDER,
    }

    /**
     * 根据类型遍历整个Map
     */
    protected void foreach(ForeachType type,NodeListener<K,V> nodeListener){
        switch (type){
            case PREORDER:
                preorder(nodeListener,root);
                break;
            case MIDDLE:
                middle(nodeListener,root);
                break;
            case AFTERORDER:
                afterOrder(nodeListener,root);
                break;
            default:
                middle(nodeListener,root);
                break;
        }
    }

    /**
     * 先序遍历
     */
    private void preorder(NodeListener<K,V> nodeListener,Node<K,V> node){
        if (ObjectUtils.isEmpty(node)) return;
        nodeListener.getNode(node);
        preorder(nodeListener,node.left);
        preorder(nodeListener,node.right);
    }

    /**
     * 中序遍历
     */
    private void middle(NodeListener<K,V> nodeListener,Node<K,V> node){
        if (node == null) return;
        middle(nodeListener,node.left);
        nodeListener.getNode(node);
        middle(nodeListener,node.right);
    }

    /**
     * 后序遍历
     */
    private void afterOrder(NodeListener<K,V> nodeListener,Node<K,V> node){
        if (node == null) return;
        afterOrder(nodeListener,node.left);
        afterOrder(nodeListener,node.right);
        nodeListener.getNode(node);
    }

    @Override
    public Iterator<Entry<K,V>> iterator() {
        return new TreeIterator();
    }

    final class TreeIterator implements Iterator<Entry<K,V>>{

        Queue<Node<K,V>> queue;

        public TreeIterator() {
            queue = new LinkedList<>();
            foreach(ForeachType.MIDDLE, new NodeListener<K, V>() {
                @Override
                public void getNode(Node<K, V> node) {
                    queue.add(node);
                }
            });
        }

        @Override
        public boolean hasNext() {
            return !queue.isEmpty();
        }

        @Override
        public Entry<K, V> next() {
            Node<K, V> node = queue.poll();
            return new TreeEntry<>(node.key,node.value);
        }
    }

    private static class TreeEntry<K,V> implements Map.Entry<K,V>,Comparable<TreeEntry<K,V>>{
        K key;
        V value;

        public TreeEntry(K key, V value) {
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

        @Override
        public String toString() {
            return "Key：" + getKey() + ",Value：" + getValue();
        }

        @Override
        public int compareTo(TreeEntry<K, V> o) {
            return ((Comparable)key).compareTo(o.key);
        }
    }


    @Override
    public Set<K> keySet() {
        Iterator<Entry<K, V>> iterator = iterator();
        Set<K> objectSet = CollectionConstructor.buildSet();
        while (iterator.hasNext()){
            objectSet.add(iterator.next().getKey());
        }
        return objectSet;
    }

    @Override
    public Collection<V> values() {
        Iterator<Entry<K, V>> iterator = iterator();
        List<V> objectSet = CollectionConstructor.buildList();
        while (iterator.hasNext()){
            objectSet.add(iterator.next().getValue());
        }
        return objectSet;
    }
}
