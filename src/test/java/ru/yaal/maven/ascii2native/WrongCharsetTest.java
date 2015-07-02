package ru.yaal.maven.ascii2native;

import com.google.common.io.Files;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.testing.MojoRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.File;
import java.nio.charset.Charset;

import static ru.yaal.maven.ascii2native.Helper.fillPomTemplate;

/**
 * @author Aleksey Yablokov.
 */
public class WrongCharsetTest {

    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void test() throws Exception {
        File folderDir = Files.createTempDir();

        File fileWithAscii = new File(folderDir, "ascii.html");
        Files.write("П\\u0410", fileWithAscii, Charset.forName("windows-1251"));

        File pom = fillPomTemplate(folderDir, new String[] {"UTF-8"});

        Ascii2NativeMojo mojo = (Ascii2NativeMojo) rule.lookupMojo(Ascii2NativeMojo.MOJO_NAME, pom);
        exception.expect(MojoExecutionException.class);
        exception.expectMessage("Ascii2Native: Can't read file in any charset: " + folderDir.getAbsolutePath());
        mojo.execute();
    }
}