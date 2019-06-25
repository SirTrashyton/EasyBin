package server;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generates a PIN given the default length of the PIN.
 * A PIN can be digits and letters.
 *
 * @author Jacob Gordon
 * @version 1.0
 * @date 6/20/19
 **/
public class PINGenerator {

    /**
     * Length of all generated PINS.
     */
    public static final int LENGTH = 4;

    /**
     * Instance of our StringBuilder to avoid concetation.
     */
    private static StringBuilder sb;

    /**
     * Instance of our random number generater.
     */
    private static Random random;

    /**
     * List to track our PIN's to avoid repeat generations.
     */
    private static ArrayList listPins;

    /**
     * List of all possible characters to generate the PIN with.
     */
    static String[] allChars = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

    /**
     * Generates a random String of 'LENGTH' with
     * digits and letters. Assumes that PIN's can always be
     * generated and the list will not be full of combinations.
     * Prevents regenerating PINs.
     * @return a PIN of 'LENGTH'.
     */
    public static String gen() {
        sb = new StringBuilder();
        if (random == null) random = new Random();
        if (listPins == null) listPins = new ArrayList();
        while (true) {
            for (int i = 0; i < LENGTH; i++) {
                sb.append(allChars[random.nextInt(allChars.length)]);
            }
            if (!listPins.contains(sb.toString())) break;
        }
        listPins.add(sb.toString());
        return sb.toString();
    }

    /**
     * Generates a PIN based on a length.
     * Does not prevent repeat PINs.
     * @param length of the PIN.
     * @return a random string of 'length' length.
     */
    public static String gen(int length) {
        sb = new StringBuilder();
        if (random == null) random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(allChars[random.nextInt(allChars.length)]);
        }
        return sb.toString();
    }

    /**
     * Checks if the given PIN has been connected and is valid.
     * @param pin to check if is valid.
     * @return
     */
    public static boolean isValidPin(String pin) {
        return listPins.contains(pin);
    }

    /**
     * Removes the given PIN from our list of PINS.
     * @param pin to remove.
     * @return true if successful, false otherwise.
     */
    public static boolean removePIN(String pin) {
        return listPins.remove(pin);
    }
}
