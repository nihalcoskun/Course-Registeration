package tr.edu.marmara;

import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

class RandomGenerator {
    private final static Random random = new Random();

    /**
     * Generates the set of distinct random numbers
     *
     * @param size  is size of the random number list
     * @param digit is digit count of each random number
     * @return the number set
     */
    public static Set<String> generate(int size, int digit) {
        Set<String> numbers = new LinkedHashSet<>();

        for (int i = 0; i < size; ++i) {
            StringBuilder number = new StringBuilder(String.valueOf(random.nextInt(9) + 1));

            for (int j = 1; j < digit; ++j) {
                number.append(random.nextInt(10));
            }

            numbers.add(number.toString());
        }

        return numbers;
    }
}