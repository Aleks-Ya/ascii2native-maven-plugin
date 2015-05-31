package ru.yaal.maven.ascii2native;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * todo add recursive parameter
 * todo accept folders array
 * todo add file encoding parameter
 *
 * @author Aleksey Yablokov.
 */
@Mojo(name = Ascii2NativeMojo.MOJO_NAME)
public class Ascii2NativeMojo extends AbstractMojo {
    public static final String MOJO_NAME = "ascii2native";
    private static final String LOG_PREFIX = "Ascii2Native: ";

    @Parameter(required = true)
    private File folder;

    @Parameter(required = true)
    private String[] includes;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        long startDate = System.currentTimeMillis();
        if (folder == null || !folder.exists()) {
            throw new MojoExecutionException(LOG_PREFIX + "Folder isn't exist: " + folder);
        }
        getLog().info(LOG_PREFIX + "Process folder: " + folder.getAbsolutePath());

        if (includes == null || includes.length == 0) {
            throw new MojoExecutionException(LOG_PREFIX + "Includes aren't specified.");
        }
        getLog().info(LOG_PREFIX + "Include masks: " + Arrays.deepToString(includes));

        WildcardFileFilter fileFilter = new WildcardFileFilter(includes);
        IOFileFilter dirFileFilter = DirectoryFileFilter.INSTANCE;
        Collection<File> files = FileUtils.listFiles(folder, fileFilter, dirFileFilter);
        getLog().info(LOG_PREFIX + "Found files: " + files.size());
        try {
            int filesWrote = 0;
            int filesSkipped = 0;
            for (File file : files) {
                getLog().debug(LOG_PREFIX + "Start file processing: " + file.getAbsolutePath());
                Path path = file.toPath();
                List<String> lines = Files.readAllLines(path);
                boolean containsAscii = false;
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    if (line.contains("\\u")) {
                        lines.remove(i);
                        lines.add(i, Ascii2Native.nativeToAscii(line));
                        containsAscii = true;
                    }
                }
                if (containsAscii) {
                    Files.write(path, lines, Charset.defaultCharset());
                    filesWrote++;
                    getLog().debug(LOG_PREFIX + "Write file: " + file.getAbsolutePath());
                } else {
                    filesSkipped++;
                    getLog().debug(LOG_PREFIX + "Skip file without ASCII symbols: " + file.getAbsolutePath());
                }
            }
            getLog().info(LOG_PREFIX + "Wrote files: " + filesWrote);
            getLog().info(LOG_PREFIX + "Skipped files: " + filesSkipped);
            assert (files.size() == filesWrote + filesSkipped);
            long finishDate = System.currentTimeMillis();
            getLog().info(LOG_PREFIX + "Process time (milliseconds): " + (finishDate - startDate));
        } catch (IOException e) {
            throw new MojoExecutionException(LOG_PREFIX + "Can't process file", e);
        }
    }
}

