package utils.data.random;

/**
 * 随机数据生成器
 */
public interface DataGenerator<T> {
    /**
     * 随机生成一连串数据
     *
     */
    T[] generator(int arrLength,int dataLength);

    /**
     * 在指定区间随机生成数据
     * @param interval   范围
     * @param length     数据长度
     * @return
     */
    T generator(T[] interval,int length);

    /**
     * 随机生成length长度的数据
     */
    T random(int length);
}
