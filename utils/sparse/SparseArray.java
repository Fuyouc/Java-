package utils.sparse;

/**
 * 稀疏数组
 */
public interface SparseArray<E> {
    /**
     * 转换成原二维数组
     * @return
     */
    E[][] array();
}
