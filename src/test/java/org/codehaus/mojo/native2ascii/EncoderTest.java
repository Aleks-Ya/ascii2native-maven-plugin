package org.codehaus.mojo.native2ascii;

import junit.framework.TestCase;

/**
 * @author yablokov a.
 */
public class EncoderTest extends TestCase {

    public void testAscii2native() throws Exception {
        assertEquals('А', Encoder.ascii2native("\\u0410").charValue());
        assertEquals('я', Encoder.ascii2native("\\u044f").charValue());
    }
}