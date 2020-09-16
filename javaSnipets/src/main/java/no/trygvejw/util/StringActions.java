package no.trygvejw.util;


/**
 * A utility where EXAM_NMR has put different kind of utility and quality of life methods for strings.
 * For the most part simple string manipulation and slicing.
 */
public class StringActions {

    /**
     * Returns a string consisting of the given pattern repeted the given amount of times
     *
     * @param pattern     the pattern to repeat
     * @param repetitions the number of times to repeat the given pattern
     * @return a string consisting of the given pattern repetitions amount of times
     */
    public static String repeat(String pattern, int repetitions) {
        return new String(new char[repetitions]).replace("\0", pattern);
    }
}
