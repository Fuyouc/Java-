package utils.data.random.child;


import utils.data.random.BaseAbstractGenerator;

public class IntegerGenerator extends BaseAbstractGenerator<Integer> {

    private static final char[] NUMBER = {
            '0','1','2','3','4','5','6','7','8','9'
    };


    @Override
    public Integer generator(Integer[] interval, int length) {
        if (interval==null) return random(length);
        if (interval.length == 1){
            return interval[0] + random.nextInt(Integer.MAX_VALUE - interval[0]);
        }else {
            int from = interval[0];
            int to = interval[1];
            int data = random.nextInt(to);
            return data >= from ? data : data + from;
        }
    }

    @Override
    public Integer[] generator(int arrLength, int dataLength) {
        Integer[] arr = new Integer[arrLength];
        for (int i = 0; i < arrLength; i++) {
            arr[i] = random(dataLength);
        }
        return arr;
    }

    @Override
    public Integer random(int length) {
        if (length <= 1){
            return random.nextInt();
        }
        StringBuilder sb = new StringBuilder();
        int rId = random.nextInt(NUMBER.length);
        rId = rId == 0 ? rId + 1 : rId;
        sb.append(NUMBER[rId]);
        for (int i = 1; i < length; i++) {
            sb.append(NUMBER[random.nextInt(NUMBER.length)]);
        }
        return Integer.valueOf(sb.toString());
    }
}
