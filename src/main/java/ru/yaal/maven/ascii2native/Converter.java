package ru.yaal.maven.ascii2native;

/**
 * @author Aleksey Yablokov.
 */
final class Converter {
    private static final int ASCII_LENGTH = 6;

    private Converter() {
    }

    /**
     * Converts given ASCII string into native string.
     *
     * @param str String contains ASCII symbols.
     * @return Native string.
     */
    public static String ascii2Native(String str) {
        if (str == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int asciiIndex;
        do {
            asciiIndex = str.indexOf("\\u", i);
            String noAscii = asciiIndex != -1 ? str.substring(i, asciiIndex) : str.substring(i, str.length());
            sb.append(noAscii);
            i = i + noAscii.length();
            String six = asciiIndex != -1 ? str.substring(asciiIndex, asciiIndex + ASCII_LENGTH) : "";
            sb.append(ascii2native(six));
            i = i + ASCII_LENGTH;
        } while (asciiIndex != -1);
        return sb.toString();
    }

    private static String ascii2native(String ascii) {
        if (ascii.length() == ASCII_LENGTH && ascii.startsWith("\\u")) {
            String num = ascii.substring(2);
            return Character.toString((char) Integer.decode("#" + num).intValue());
        } else {
            return ascii;
        }
    }
}
