package ru.yaal.maven.ascii2native;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;

/**
 * @author Aleksey Yablokov.
 */
class Helper {
    public static File fillPomTemplate(File folderDir, String[] charsets) throws IOException {
        File pomTemplate = new File(SkipWrongCharsetTest.class.getResource("pom_template.xml").getFile());
        File pom = File.createTempFile("tmp-", "-pom.xml");
        String content = FileUtils.readFileToString(pomTemplate);
        content = insertFolder(folderDir, content);
        content = insertCharsets(charsets, content);
        FileUtils.writeStringToFile(pom, content);
        return pom;
    }

    private static String insertCharsets(String[] charsets, String content) {
        StringBuilder charsetInsert = new StringBuilder();
        if (charsets != null && charsets.length > 0) {
            charsetInsert.append("<charsets>");
            for (String charset : charsets) {
                charsetInsert.append("<charset>");
                charsetInsert.append(charset);
                charsetInsert.append("</charset>");
            }
            charsetInsert.append("</charsets>");
        }
        content = content.replaceAll("\\[charsets]", charsetInsert.toString());
        return content;
    }

    private static String insertFolder(File folderDir, String content) {
        content = content.replaceAll("\\[folder]", folderDir.getAbsolutePath());
        return content;
    }

    public static void assertStatistics(Ascii2NativeMojo mojo, int filesWrote, int filesSkipped, int readErrors)
            throws NoSuchFieldException, IllegalAccessException {

        Field filesWroteField = mojo.getClass().getDeclaredField("filesWrote");
        Field filesSkippedField = mojo.getClass().getDeclaredField("filesSkipped");
        Field readErrorsField = mojo.getClass().getDeclaredField("readErrors");

        if (!filesWroteField.isAccessible()) {
            filesWroteField.setAccessible(true);
        }
        if (!filesSkippedField.isAccessible()) {
            filesSkippedField.setAccessible(true);
        }
        if (!readErrorsField.isAccessible()) {
            readErrorsField.setAccessible(true);
        }

        assertEquals("Files wrote mismatch:", filesWrote, filesWroteField.getInt(mojo));
        assertEquals("Files skipped mismatch:", filesSkipped, filesSkippedField.getInt(mojo));
        assertEquals("Read errors mismatch:", readErrors, readErrorsField.getInt(mojo));
    }
}
