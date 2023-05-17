package utils.bloom;


import java.util.ArrayList;
import java.util.List;

/**
 * 布隆过滤器
 * 用于高效的查询一个元素是否存在（O(k)级别）
 * 适用场景：需要快速检查一个一个用是否处于黑名单，
 */
public final class BloomFilter<E> {
    /**
     * bit数组
     * 1字节=8bit
     * int=4字节
     * int=4个byte组成
     * 1byte=1B
     */
    private long[] bits;

    /**
     * 哈希函数列表，自定义哈希函数需要实现 BloomHashCode 接口
     */
    private List<BloomHashCode> bloomHashCodes;


    public BloomFilter() {
        this(50000,0.01);
    }

    /**
     *
     * @param size      元素总数
     * @param errorRate 误差率（0.01=1%）
     */
    public BloomFilter(int size,double errorRate){
        int memoryKB = BloomFormula.getMemoryKB(BloomFormula.getMemoryBit(size, errorRate));
        bits = new long[(memoryKB * 1024) / 8];
        bloomHashCodes = new ArrayList<>();
    }
    

    /**
     * 添加元素
     */
    public void addElement(E element){
        for (int i = 0; i < bloomHashCodes.size(); i++) {
            //根据hash运算。获取bit位
            int bit = hashCode(bloomHashCodes.get(i), element);
            int numberIndex = getArrIndex(bit); //计算出这个bit在bits数组中的下标
            int bitIndex = getBitIndex(bit);    //计算出这个bit在bits[numberIndex]的那个二进制位
            //将指定 bit 的状态设置为1
            bits[numberIndex] = bits[numberIndex] | (1L << (bitIndex));
            //将指定 bit 的状态设置为0
//            bits[numberIndex] = bits[numberIndex] & (~ 1L << (bitIndex));
        }
    }

    /**
     * 判断这一个元素是否在集合中
     */
    public boolean contains(E element){
        int[] tempBits = new int[bloomHashCodes.size()];
        for (int i = 0; i < bloomHashCodes.size(); i++) {
            //根据hash运算。获取bit位
            int bit = hashCode(bloomHashCodes.get(i), element);
            int numberIndex = getArrIndex(bit); //计算出这个bit在bits数组中的下标
            int bitIndex = getBitIndex(bit);    //计算出这个bit在bits[numberIndex]的那个二进制位
//            //获取这一bit的状态，并记录这一状态
            tempBits[i] = (int) ((bits[numberIndex] >> (bitIndex)) & 1);
        }
        return contains(tempBits);
    }

    /**
     * 检查所有bit为是都为1，如果不是则表示元素不存在
     */
    private boolean contains(int[] bits){
        if (bits.length == 0) return false;
        for (int i = 0; i < bits.length; i++) {
            if (bits[i] == 0){
                return false;
            }
        }
        return true;
    }

    public void registerHashCode(utils.bloom.BloomHashCode bloomHashCode){
        bloomHashCodes.add(bloomHashCode);
    }

    /**
     * 根据哈希函数获取bit位信息
     */
    private int hashCode(utils.bloom.BloomHashCode bloomHashCode, E element){
        return bloomHashCode.hashCode(element) % bits.length;
    }

    private int getArrIndex(int bit){
        return bit / getByteSize();
    }

    private int getBitIndex(int bit){
        return bit % getByteSize();
    }

    private int getByteSize(){
        return 64;
    }
}
