package ru.yaal.maven.ascii2native;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Aleksey Yablokov.
 */
class Helper {
    public static File fillPomTemplate(File folderDir, String[] charsets) throws IOException {
        File pomTemplate = new File(WrongCharsetTest.class.getResource("correct_pom.xml").getFile());
        File pom = File.createTempFile("tmp-", "-pom.xml");
        String content = FileUtils.readFileToString(pomTemplate);
        content = insertFolder(folderDir, content);
        content = insertCharsets(charsets, content);
        FileUtils.writeStringToFile(pom, content);
        return pom;
    }

    public static String insertCharsets(String[] charsets, String content) {
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

    public static String insertFolder(File folderDir, String content) {
        content = content.replaceAll("\\[folder]", folderDir.getAbsolutePath());
        return content;
    }
}
