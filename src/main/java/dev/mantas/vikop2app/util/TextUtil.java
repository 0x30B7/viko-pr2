package dev.mantas.vikop2app.util;

public class TextUtil {

    public static String capitalize(String input) {
        return Character.toUpperCase(input.charAt(0)) + input.substring(1);
    }

    public static Integer parseInt(String text) {
        try {
            return Integer.parseInt(text);
        } catch (Exception ex) {
            return null;
        }
    }

    public static Long parseLong(String text) {
        try {
            return Long.parseLong(text);
        } catch (Exception ex) {
            return null;
        }
    }

}
