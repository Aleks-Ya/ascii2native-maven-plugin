package ru.yaal.maven.ascii2native;

/**
 * @author Aleksey Yablokov.
 */
public final class Ascii2Native {

    private Ascii2Native() {
    }

    /**
     * Converts given CharSequence into ASCII String.
     */
    public static String nativeToAscii(CharSequence csq) {
        if (csq == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        String cs = csq.toString();
        int i = 0;
        String six = getSixChars(i, cs);
        while (six != null) {
            if (six.startsWith("\\u")) {
                Character c = ascii2native(six);
                sb.append(c);
                i = i + 6;
            } else {
                sb.append(cs.charAt(i));
                i++;
            }
            six = getSixChars(i, cs);
        }
        sb.append(getLastChars(i, cs));
        return sb.toString();
    }

    private static String getSixChars(int index, String s) {
        if (s.length() < index + 6) {
            return null;
        }
        return s.substring(index, index + 6);
    }

    private static String getLastChars(int index, String s) {
        return s.length() > index ? s.substring(index) : "";
    }

    private static char ascii2native(String ascii) {
        assert (ascii.length() == 6);
        assert (ascii.startsWith("\\u"));

        String num = ascii.substring(2);
        return (char) Integer.decode("#" + num).intValue();
    }
}
