package ctfmodell.util;

/**
 * Geplant war hier eine Sammlung von kleinen Hilfsmethoden
 *
 * @author Nick Garbusa
 */
public class Helper {
    public static boolean isValidClassName(String className) {
        return Character.isUpperCase(className.charAt(0));
    }
}
