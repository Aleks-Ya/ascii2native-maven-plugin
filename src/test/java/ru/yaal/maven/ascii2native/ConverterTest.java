package ru.yaal.maven.ascii2native;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Aleksey Yablokov.
 */
public class ConverterTest {
    private static final String natives = "АБВГДЕЁЖЗИЙКЛМНОПРСТУФХЦЧШЩЪЫЬЭЮЯабвгдеёжзийклмнопрстуфхцчшщъыьэюя";
    private static final String ascii = "\\u0410\\u0411\\u0412\\u0413\\u0414\\u0415\\u0401\\u0416\\u0417\\u0418\\u0419\\u041a\\u041b\\u041c\\u041d\\u041e\\u041f\\u0420\\u0421\\u0422\\u0423\\u0424\\u0425\\u0426\\u0427\\u0428\\u0429\\u042a\\u042b\\u042c\\u042d\\u042e\\u042f\\u0430\\u0431\\u0432\\u0433\\u0434\\u0435\\u0451\\u0436\\u0437\\u0438\\u0439\\u043a\\u043b\\u043c\\u043d\\u043e\\u043f\\u0440\\u0441\\u0442\\u0443\\u0444\\u0445\\u0446\\u0447\\u0448\\u0449\\u044a\\u044b\\u044c\\u044d\\u044e\\u044f";

    @Test
    public void testNull() {
        assertEquals(null, Converter.ascii2Native(null));
    }

    @Test
    public void russianAlphabet() {
        assertEquals(natives, Converter.ascii2Native(ascii));
    }

    @Test
    public void punctuationMarks() {
        assertEquals("Привет, АБВГД!", Converter.ascii2Native("Привет, \\u0410\\u0411\\u0412\\u0413\\u0414!"));
    }

    @Test
    public void tilde() {
        assertEquals("~", Converter.ascii2Native("\u007e"));
    }
}
