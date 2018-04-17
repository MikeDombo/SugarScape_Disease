public class HammingDistance {
    public static int[] getMinHammingDistance(String a, String b){
        char[] aArr = a.toCharArray();
        char[] bArr = b.toCharArray();

        assert (a.length() >= b.length());

        int minDistance = Integer.MAX_VALUE;
        int distance;
        int minDistanceIndex = 0;

        for(int i = 0; i <= aArr.length - bArr.length; i++){
            distance = 0;
            for(int j = 0; j < bArr.length; j++){
                if(aArr[i+j] != bArr[j]){
                    distance++;
                }
            }
            if(distance < minDistance){
                minDistance = distance;
                minDistanceIndex = i;
            }
        }

        int[] ret = {minDistance, minDistanceIndex};
        return ret;
    }

    public static String closerByOne(String a, String b){
        int[] distVal = getMinHammingDistance(a, b);
        if(distVal[0] > 0){
            for (int i = distVal[1]; i <= b.length(); i++){
                if(a.charAt(i) != b.charAt(i - distVal[1])){
                    String s = a.substring(0, i);
                    s += b.charAt(i - distVal[1]);
                    s += a.substring(i+1);
                    return s;
                }
            }
        }
        return a;
    }
}
