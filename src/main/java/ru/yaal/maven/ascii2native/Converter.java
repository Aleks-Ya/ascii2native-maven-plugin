package ru.yaal.maven.ascii2native;

/**
 * @author Aleksey Yablokov.
 */
final class Converter {
    private static final int ASCII_LENGTH = 6;

    private Converter() {
    }

    /**
     * Converts given String into ASCII String.
     */
    public static String nativeToAscii(String cs) {
        if (cs == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        int i = 0;
        int asciiIndex;
        do {
            asciiIndex = cs.indexOf("\\u", i);
            String noAscii = asciiIndex != -1 ? cs.substring(i, asciiIndex) : cs.substring(i, cs.length());
            sb.append(noAscii);
            i = i + noAscii.length();
            String six = asciiIndex != -1 ? cs.substring(asciiIndex, asciiIndex + ASCII_LENGTH) : "";
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
