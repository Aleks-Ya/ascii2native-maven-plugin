/*
 * The MIT License
 * 
 * Copyright (c) 2007 The Codehaus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package ru.yaal.maven.ascii2native;

/**
 * @author Evgeny Mandrikov
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
        char c = (char) Integer.decode("#" + num).intValue();
        return c;
    }
}
