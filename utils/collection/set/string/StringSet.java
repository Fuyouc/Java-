package utils.collection.set.string;

import utils.collection.Collection;
import utils.collection.list.List;
import utils.collection.set.Set;

/**
 * 字符串集合
 * 可用于高效的 添加+查找+删除 字符串
 */
public interface StringSet extends Set<String> {

    /**
     * 添加字符串
     */
    boolean add(String value);

    /**
     * 删除指定字符串
     * @return 返回是否删除成功
     */
    boolean remove(String value);

    /**
     * 删除前缀为 prefix 的字符串集合
     * @return 返回删除的个数
     */
    int removePrefix(String prefix);

    /**
     * 匹配前缀为 prefix 的字符串集合
     * @param prefix
     * @return 前缀为 prefix 的字符串集合
     */
    List<String> matchingPrefix(String prefix);

    /**
     * 获取前缀为 prefix 的字符串个数
     */
    int getPrefixSize(String prefix);

    /**
     * 字符串是否存在
     */
    boolean contains(String value);


    /**
     * 获取字符串个数
     */
    int size();

    /**
     * 是否没有字符串
     */
    boolean isEmpty();

    /**
     * 清空所有字符串
     */
    void clear();

}
