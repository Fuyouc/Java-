package utils.collection.tree.prefix;

import utils.collection.list.List;

/**
 * 前缀树
 * Trie：单词查找树（只支持存储单词）
 * StringPrefixSearchTree：字符串前缀查找树（支持任意类型的字符串）
 * Trie与StringPrefixSearchTree的区别：
 *  1、Trie与StringPrefixSearchTree所要做的事是一致的
 *  2、Trie在对比StringPrefixSearchTree占用的空间要小一些，单词只能存储单词
 *  3、StringPrefixSearchTree虽然占用的空间要大，但是支持存储任意类型字符串
 */
public interface PrefixTree {

    /**
     * 添加
     */
    void add(String value);

    /**
     * 删除
     * @param value
     */
    boolean remove(String value);

    /**
     * 删除前缀为 prefix 的字符串内容
     * @return 返回删除的个数
     */
    int removePrefix(String prefix);

    /**
     * 是否存在该字符串
     * @param value
     */
    boolean contains(String value);

    /**
     * 字符串总个数
     */
    int size();

    /**
     * 是否为空
     * @return
     */
    boolean isEmpty();

    /**
     * 获取以 prefix 为前缀的单词
     * @param prefix
     * @return
     */
    List<String> getPrefix(String prefix);

    /**
     * 获取以 prefix 字符串个数
     */
    int getPrefixSize(String prefix);

    /**
     *
     * 清空集合内容
     */
    void clear();
}
