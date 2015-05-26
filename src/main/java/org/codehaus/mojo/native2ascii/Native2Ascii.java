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
package org.codehaus.mojo.native2ascii;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.CharBuffer;

/**
 * @author Evgeny Mandrikov
 */
public final class Native2Ascii {

    private Native2Ascii() {
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
            Character c = Encoder.ascii2native(six);
            if (c != null) {
                sb.append(c);
                i = i + 6;
            } else {
                sb.append(cs.charAt(i));
                i++;
            }
            six = getSixChars(i, cs);
        }
        return sb.toString();
    }

    private static String getSixChars(int index, String s) {
        if (s.length() < index + 6) {
            return null;
        }
        return s.substring(index, index + 6);
    }

    public static void nativeToAscii(File src, File dst, String encoding)
            throws IOException {
        BufferedReader input = null;
        BufferedWriter output = null;
        try {
            input = new BufferedReader(new InputStreamReader(new FileInputStream(src), encoding));
            output = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(dst), "US-ASCII"));

            char[] buffer = new char[4096];
            int len;
            while ((len = input.read(buffer)) != -1) {
                output.write(nativeToAscii(CharBuffer.wrap(buffer, 0, len)));
            }
        } finally {
            closeQuietly(input);
            closeQuietly(output);
        }
    }

    private static void closeQuietly(Closeable closeable) {
        if (closeable != null) {
            try {
                closeable.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
