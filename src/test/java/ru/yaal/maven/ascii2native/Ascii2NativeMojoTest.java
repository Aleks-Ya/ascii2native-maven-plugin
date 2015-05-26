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
        File tmpDir = Files.createTempDir();
        File procesFile = new File(tmpDir, "a.html");
        Files.write("Привет, \\u0410\\u0411\\u0412\\u0413\\u0414!", procesFile, Charset.forName("UTF-8"));

        File pom = new File(Ascii2NativeMojoTest.class.getResource("correct_pom.xml").getFile());
        File tmpPom = File.createTempFile("tmp-", "-pom.xml");
        String content = FileUtils.readFileToString(pom);
        String replacedContent = content.replaceAll("\\[folder]", tmpDir.getAbsolutePath());
        FileUtils.writeStringToFile(tmpPom, replacedContent);

        Ascii2NativeMojo mojo = (Ascii2NativeMojo) rule.lookupMojo(Ascii2NativeMojo.MOJO_NAME, tmpPom);
        assertNotNull(mojo);
        mojo.execute();

        assertEquals("Привет, АБВГД!", FileUtils.readFileToString(procesFile).trim());
    }
}