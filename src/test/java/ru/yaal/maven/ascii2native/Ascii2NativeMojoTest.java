package ru.yaal.maven.ascii2native;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Aleksey Yablokov.
 */
public class Ascii2NativeMojoTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    @Test
    public void test() throws Exception {
        File folderDir = Files.createTempDir();

        File fileWithAscii = new File(folderDir, "ascii.html");
        Charset expCharset = Charset.forName("windows-1251");
        Files.write("Привет, \\u0410\\u0411\\u0412\\u0413\\u0414!", fileWithAscii, expCharset);

        File fileWithoutAscii = new File(folderDir, "no_ascii.html");
        Files.write("До встречи!", fileWithoutAscii, Charset.forName("UTF-8"));

        File pom = Helper.fillPomTemplate(folderDir, new String[] {"UTF-8", "windows-1251"});

        Ascii2NativeMojo mojo = (Ascii2NativeMojo) rule.lookupMojo(Ascii2NativeMojo.MOJO_NAME, pom);
        assertNotNull(mojo);
        mojo.execute();

        assertEquals("Привет, АБВГД!", FileUtils.readFileToString(fileWithAscii, expCharset).trim());
        assertEquals("До встречи!", FileUtils.readFileToString(fileWithoutAscii).trim());
    }

}