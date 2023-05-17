package utils.bloom;

/**
 * 布隆过滤器公式
 */
public class BloomFormula {

    /**
     * 计算布隆器需要的内存大小
     * @param size          元素总数
     * @param errorRate     误差率
     * @return
     */
    public static long getMemoryBit(int size,double errorRate){
        return  ((long)(- size * Math.log(errorRate) / Math.pow(Math.log(2), 2))) + 1;
    }

    public static int getMemoryKB(long bits){
        return (((int) bits / 8) / 1024) + 1;
    }
}
