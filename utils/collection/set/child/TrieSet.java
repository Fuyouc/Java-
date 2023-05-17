package utils.collection.set.child;


import utils.collection.set.AbstractSet;

import java.util.HashSet;
import java.util.Iterator;

/**
 * 单词查找树
 * 只允许添加不允许添加数字
 */
public class TrieSet extends AbstractSet<String> {

    private int size;

    private HashSet<String> set;

    private static final String regex = "[a-zA-Z]{0,}";

    /**
     * 树中每个节点
     */
    private static class Node{
        int prefix;       //该树中有多少个以这个字符为前缀的
        int end;          //该树中有多少个以这个字符为结尾的
        Node[] nexts;  //每个字符下的字符列表

        public Node() {
            this.prefix = 0;
            this.end = 0;
            /**
             * 因为有26个字母，所以这里我们只需要创建26个下一个节点数组即可
             * 根据 ASCII编码表，我们可以使用 字符 - 'a' 来表示每个数组格子是那个字符
             */
            this.nexts = new Node[26];
        }
    }

    /**
     * root节点表示的是一个空String | null
     */
    private Node root; //根节点

    public TrieSet() {
        root = new Node(); //初始化根节点
        set = new HashSet<>();
    }

    @Override
    public boolean add(String value) {
        char[] chars = toCharArray(value);
        Node node = root;
        /**
         * 每调用一次add方法，root节点的前缀就要++
         */
        node.prefix++;
        for (int i = 0; i < chars.length; i++) {
            int index = getCharIndex(chars[i]);
            if (node.nexts[index] == null){
                node.nexts[index] = new Node();
            }
            node = node.nexts[index];
            node.prefix++;
        }
        node.end++;
        ++size; //++元素个数
        if (!set.contains(value)){
            set.add(value);
        }
        return true;
    }

    @Override
    public boolean remove(String value) {
        if (!contains(value)) return false;
        char[] chars = toCharArray(value);
        Node node = root;
        node.prefix--;
        for (int i = 0; i < chars.length; i++) {
            int index = getCharIndex(chars[i]);
            //减少当前节点的prefix值
            --node.nexts[index].prefix;
            //如果当前节点的prefix的值已经是0，说明底下的所有元素也是被删除的元素，直接将当前格子置空并返回即可
            if (node.nexts[index].prefix == 0){
                node.nexts[index] = null;
                --size;
                set.remove(value);

                return true;
            }
            node = node.nexts[index];
        }
        //如果没有一个节点被删除，则减少最后的end值即可
        --node.end;
        --size;
        return true;
    }

    @Override
    public boolean contains(String value) {
        char[] chars = toCharArray(value);
        Node node = root;
        for (int i = 0; i < chars.length; i++) {
            int index = getCharIndex(chars[i]);
            if (node.nexts[index] == null) return false;
            node = node.nexts[index];
        }
        return node.end > 0;
    }

    /**
     * 获取有多少个指定的相同字
     * @return
     */
    public int getStringSize(String value){
        char[] chars = toCharArray(value);
        Node node = root;
        for (int i = 0; i < chars.length; i++) {
            int index = getCharIndex(chars[i]);
            if (node.nexts[index] == null){
                return 0;
            }
            node = node.nexts[index];
        }
        return node.end;
    }

    /**
     * 获取有多少个以指定字符串开头的字符串个数
     * @return
     */
    public int getPrefixStringSize(String prefix){
        char[] chars = toCharArray(prefix);
        Node node = root;
        for (int i = 0; i < chars.length; i++) {
            int index = getCharIndex(chars[i]);
            if (node.nexts[index] == null) return 0;
            node = node.nexts[index];
        }
        return node.prefix;
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
        this.size = 0;
        for (int i = 0; i < root.nexts.length; i++) {
            root.nexts[i] = null;
        }
        root.prefix = 0;
        root.end = 0;
    }

    @Override
    public Iterator<String> iterator() {
        return set.iterator();
    }

    private char[] toCharArray(String value){
        if (value == null) return new char[0];
        else if (!value.matches(regex)) throw new RuntimeException("Value does not allow non-letters");
        return value.toCharArray();
    }

    private int getCharIndex(char c){
        return c - 'a';
    }
}
