package utils.data.random.child;


import utils.data.random.BaseAbstractGenerator;

public class StringGenerator extends BaseAbstractGenerator<String> {

    private static final char[] LOWER_CASE = {
            'q','w','e','r','t','y','u','i','o','p','a','s','d','f','g','h','j','k','l','z','x','c','v','b','n','m'
    };
    private static final char[] UPPER_CASE = {
            'Q','W','E','R','T','Y','U','I','O','P','A','S','D','F','G','H','J','K','L','Z','X','C','V','B','N','M'
    };

    private static final char[] NUMBER = {
            '0','1','2','3','4','5','6','7','8','9'
    };

    @Override
    public String generator(String[] interval, int length) {
        if (interval == null) return random(length);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(interval[random.nextInt(interval.length)]);
        }
        return sb.toString();
    }

    @Override
    public String[] generator(int arrLength, int dataLength) {
        String[] arr = new String[arrLength];
        for (int i = 0; i < arrLength; i++) {
            arr[i] = random(dataLength);
        }
        return arr;
    }

    @Override
    public String random(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomId = random.nextInt(3);
            switch (randomId){
                case 0:
                    sb.append(LOWER_CASE[random.nextInt(LOWER_CASE.length)]);
                    break;
                case 1:
                    sb.append(UPPER_CASE[random.nextInt(UPPER_CASE.length)]);
                    break;
                case 2:
                    sb.append(NUMBER[random.nextInt(NUMBER.length)]);
                    break;
            }
        }
        return sb.toString();
    }

}
