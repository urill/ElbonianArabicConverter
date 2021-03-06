package converter;

import converter.exceptions.MalformedNumberException;
import converter.exceptions.ValueOutOfBoundsException;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class implements a converter that takes a string that represents a number in either the
 * Elbonian or Arabic numeral form. This class has methods that will return a value in the chosen form.
 * s
 *
 * @version 3/18/17
 */
public class ElbonianArabicConverter {

    // A string that holds the number (Elbonian or Arabic) you would like to convert
    private final String number;

    private HashMap<Character, Integer> char2int = new HashMap<>();
    private static final String theWholeThing = "MMMDeCCCLmXXXVwIII";
    private static final Pattern elbonianRegex = Pattern.compile("^M{0,3}D?e?C{0,3}L?m?X{0,3}V?w?I{0,3}$");

    private int arabic;
    private String elbonian;


    /**
     * Constructor for the ElbonianArabic class that takes a string. The string should contain a valid
     * Elbonian or Arabic numeral. The String can have leading or trailing spaces. But there should be no
     * spaces within the actual number (ie. "9 9" is not ok, but " 99 " is ok). If the String is an Arabic
     * number it should be checked to make sure it is within the Elbonian number systems bounds. If the
     * number is Elbonian, it must be a valid Elbonian representation of a number.
     * <p>
     * Unspecified behaviors:
     * 0. A string that is not valid Elbonian nor valid arabic will throw MalformedNumberException
     * 1. An empty string will be assumed Elbonian and converts to Arabic 0.
     * 2. Elbonian number does not have to exhaust the larger number before using the smaller number, as long as they are in order (rule 3).
     * e.g. MDeCLmXVwI and MMCX are both valid, mapping to arabic 2110.
     *
     * @param number A string that represents either a Elbonian or Arabic number.
     * @throws MalformedNumberException  Thrown if the value is an Elbonian number that does not conform
     *                                   to the rules of the Elbonian number system. Leading and trailing spaces should not throw an error.
     * @throws ValueOutOfBoundsException Thrown if the value is an Arabic number that cannot be represented
     *                                   in the Elbonian number system.
     */
    public ElbonianArabicConverter(String number) throws MalformedNumberException, ValueOutOfBoundsException {

        this.char2int.put('M', 1000);
        this.char2int.put('C', 100);
        this.char2int.put('X', 10);
        this.char2int.put('I', 1);
        this.char2int.put('D', 500);
        this.char2int.put('L', 50);
        this.char2int.put('V', 5);
        this.char2int.put('e', 400);
        this.char2int.put('m', 40);
        this.char2int.put('w', 4);

        this.number = number;
        String input = number.trim();
        Matcher elbonianMatcher = elbonianRegex.matcher(input);
        if (elbonianMatcher.matches()) {
            this.elbonian = input;
            this.arabic = this.toArabicErrorable();
        } else {
            try {
                arabic = Integer.parseInt(input);
                this.elbonian = this.toElbonianRecursive("", theWholeThing, 0);
            } catch (NumberFormatException e) {
                throw new MalformedNumberException(input);
            }
        }


    }

    /**
     * Converts the number to an Arabic numeral or returns the current value as an int if it is already
     * in the Arabic form.
     *
     * @return An arabic value
     */
    public int toArabic() throws MalformedNumberException {
        return this.arabic;
    }

    private int toArabicErrorable() throws MalformedNumberException {
        try {
            return this.number.chars()
                    .mapToObj(c -> (char) c)
                    .map(char2int::get)
                    .mapToInt(Integer::intValue).sum();
        } catch (NullPointerException e) {
            throw new MalformedNumberException(number);
        }
    }

    /**
     * Converts the number to an Elbonian numeral or returns the current value if it is already in the Elbonian form.
     *
     * @return An Elbonian value
     */
    public String toElbonian() {
        return this.elbonian;
    }

    private String toElbonianRecursive(String confirmedElbonian, String possibleElbonian, int confirmedArabic) throws ValueOutOfBoundsException {

        char currentElbonian = possibleElbonian.charAt(0);//todo check string size and make sure it is not zero
        int currentArabic = this.char2int.get(currentElbonian);
        int tentativeArabic = currentArabic + confirmedArabic;
        String tentativeElbonian = confirmedElbonian + String.valueOf(currentElbonian);
        try {
            if (tentativeArabic == this.arabic) {
                return tentativeElbonian;
            } else if (tentativeArabic < this.arabic) {
                return this.toElbonianRecursive(tentativeElbonian, possibleElbonian.substring(1), tentativeArabic);
            } else {
                return this.toElbonianRecursive(confirmedElbonian, possibleElbonian.substring(1), confirmedArabic);
            }
        } catch (StringIndexOutOfBoundsException e) {
            throw new ValueOutOfBoundsException(this.number);
        }
    }
}
