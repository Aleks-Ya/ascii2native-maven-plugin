package ru.yaal.maven.ascii2native;

import junit.framework.TestCase;
import ru.yaal.maven.ascii2native.Encoder;

/**
 * @author yablokov a.
 */
public class EncoderTest extends TestCase {

    public void testAscii2native() throws Exception {
        assertEquals('А', Encoder.ascii2native("\\u0410").charValue());
        assertEquals('я', Encoder.ascii2native("\\u044f").charValue());
    }
}