package utils.collection.map.tree;

import utils.collection.CollectionConstructor;
import utils.collection.map.AbstractTreeMap;
import utils.collection.map.Map;
import utils.collection.set.Set;

import java.util.*;

/**
 * 由二叉搜索树实现的Map
 * @param <K>
 * @param <V>
 */
public class BinarySearchTreeMap<K,V> extends AbstractTreeMap<K,V> {

    public BinarySearchTreeMap() {
        this(null);
    }

    public BinarySearchTreeMap(Comparator<K> comparator) {
        this.comparator = comparator;
    }

    @Override
    public void put(K key, V value) {
        if (isEmpty()){
            root = createNode(key,value,null);
        }else {
            Node<K,V> parent = null,node = root;

            int cmp = 0;
            while (node != null){
                parent = node;
                cmp = compare(key,node.key);
                if (cmp > 0){
                    //如果添加的元素 > 当前节点元素，说明往右边走
                    node = node.right;
                }else if (cmp < 0){
                    //如果添加的元素 < 当前节点元素，说明往左边走
                    node = node.left;
                }else {
                    node.value = value;
                    return;
                }
            }

            Node<K,V> newNode = createNode(key,value,parent);
            if (cmp > 0){
                parent.right = newNode;
            }else {
                parent.left = newNode;
            }
        }
        ++size;
    }

    @Override
    public V get(K key) {
        Node<K, V> node = getNode(key);
        if (node == null) return null;
        return node.value;
    }

    @Override
    public V remove(K key) {
        Node<K, V> node = getNode(key);
        if (node == null) return null;

        if (node.isLeaf()){
            //如果删除的是叶子节点
            if (node.isRoot()){
                root = null;
            }else {
                if (node.isLeft()){
                    node.parent.left = null;
                }else {
                    node.parent.right = null;
                }
            }
        }else {
            //删除普通节点
            remove(node);
        }
        --size;
        return node.value;
    }

    private void remove(Node<K,V> removeNode){
        //获取前驱节点
        Node<K, V> precursorNode = getPrecursorNode(removeNode);
        if (precursorNode != null){
            //前驱节点存在
            if (precursorNode.isLeft()){
                precursorNode.parent.left = precursorNode.right;
            }else if (precursorNode.isRight()){
                precursorNode.parent.right = precursorNode.left;
            }

            //更新父节点
            precursorNode.parent = removeNode.parent;

            if (removeNode.isLeft()){
                removeNode.parent.left = precursorNode;
            }else if (removeNode.isRight()){
                removeNode.parent.right = precursorNode;
            }

            //继承删除节点的左右子树
            precursorNode.left = removeNode.left;
            precursorNode.right = removeNode.right;

            //更新左右子树的父节点
            if (precursorNode.left != null) precursorNode.left.parent = precursorNode;
            if (precursorNode.right != null) precursorNode.right.parent = precursorNode;

            //如果接替的是root节点，则更新root节点的索引
            if (precursorNode.isRoot()) root = precursorNode;
        }else {
            if (removeNode.isLeft()){
                removeNode.parent.left = removeNode.right;
            }else if (removeNode.isRight()){
                removeNode.parent.right = removeNode.right;
            }
            removeNode.right.parent = removeNode.parent;
        }
    }

    @Override
    public boolean contains(K key) {
        if (getNode(key) == null){
            return false;
        }
        return true;
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
        root = null;
        size = 0;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        Set<Map.Entry<K,V>> objectSet = CollectionConstructor.buildSet();
        Iterator<Entry<K, V>> iterator = iterator();
        while (iterator.hasNext()){
            objectSet.add(iterator.next());
        }
        return objectSet;
    }

}
