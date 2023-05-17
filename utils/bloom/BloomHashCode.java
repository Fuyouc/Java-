package utils.bloom;

/**
 * 布隆过滤器的哈希函数
 */
public interface BloomHashCode {
    int hashCode(Object element);
}
