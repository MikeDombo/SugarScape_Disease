public class HammingDistance {
    public static int[][] getDistances(String[] sequences) {
        String s1 = sequences[0];
        String s2 = sequences[1];
        if (s1 == null || s2 == null)
            System.exit(0);
        int m = s1.length();
        int n = s2.length();
        // normalize case
        s1 = s1.toUpperCase();
        s2 = s2.toUpperCase();
        int distance[][] = new int[40][40];

        // Instead of a 2d array of space O(m*n) such as int d[][] = new int[m +
        // 1][n + 1], only the previous row and current row need to be stored at
        // any one time in prevD[] and currD[].
        int prevD[] = new int[n + 1];
        int currD[] = new int[n + 1];
        int temp[]; // temporary pointer for swapping

        // the distance of any second string to an empty first string
        for (int j = 0; j < n + 1; j++) {
            prevD[j] = j;
        }

        // for each row in the distance matrix
        for (int i = 0; i < m; i++) {

            // the distance of any first string to an empty second string
            currD[0] = i + 1;
            char ch1 = s1.charAt(i);

            // for each column in the distance matrix
            for (int j = 1; j <= n; j++) {

                char ch2 = s2.charAt(j - 1);
                if (ch1 == ch2) {
                    currD[j] = prevD[j - 1];
                } else {
                    currD[j] = Math.min(prevD[j] + 1, Math.min(currD[j - 1] + 1, prevD[j - 1] + 1));
                }

            }

            temp = prevD;
            prevD = currD;
            currD = temp;

        }

        return distance;

    }

    /**
     * Get the hamming distance between two string sequences
     *
     * @param sequence1 - the first sequence
     * @param sequence2 - the second sequence
     * @return the hamming distance
     */
    public static int getHammingDistance(String sequence1, String sequence2) {
        // TODO fill in this method
        int distance = 0;
        if (sequence1 == null || sequence2 == null)
            System.exit(0);

        sequence1 = sequence1.toUpperCase();
        sequence2 = sequence2.toUpperCase();

        if (sequence1.length() != sequence2.length()) {
            System.out.println("The string are not equal in length ,Please enter the strings wit equal lengths ");
        }

        for (int i = 0; i < sequence1.length(); i++) {
            if (sequence1.charAt(i) != sequence2.charAt(i))
                distance++;
        }


        return distance;

    }
}
