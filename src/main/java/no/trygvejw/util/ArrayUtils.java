package no.trygvejw.util;

import java.math.BigDecimal;

/**
 * Utils for arrays No shit
 */
public class ArrayUtils {

    /**
     * big decimal array ops
     */
    public static class BigDecimalArrays {

        /**
         * Elementvise subtraction of two big decimal arrays returns the result in a new array
         *
         * @param alpha subtracted from
         * @param beta  suptracter?
         *
         * @return alpha - beta  in a new array
         */
        public static BigDecimal[] elementSubtract(BigDecimal[] alpha, BigDecimal[] beta) {
            // shold probably test for equal size
            BigDecimal[] outArray = new BigDecimal[alpha.length];
            for (int i = 0; i < alpha.length; i++) {
                outArray[i] = alpha[i].subtract(beta[i]);
            }
            return outArray;
        }

        /**
         * Elementvise squering of an big decimal array returns the result in a new array
         *
         * @param alpha the array to squere the elements from
         *
         * @return alpha^2 elementvise in a new array
         */
        public static BigDecimal[] elementSquare(BigDecimal[] alpha) {
            BigDecimal[] outArray = new BigDecimal[alpha.length];
            for (int i = 0; i < alpha.length; i++) {
                outArray[i] = alpha[i].multiply(alpha[i]);
            }
            return outArray;
        }

        /**
         * Sums the elements in a big decimal array
         *
         * @param alpha the array to sum
         *
         * @return the sum
         */
        public static BigDecimal sumElements(BigDecimal[] alpha) {
            BigDecimal returnValue = BigDecimal.valueOf(0L);
            for (int i = 0; i < alpha.length; i++) {
                returnValue = returnValue.add(alpha[i]);
            }
            return returnValue;
        }


    }


}
