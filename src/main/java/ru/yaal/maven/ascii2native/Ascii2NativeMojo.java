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
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.MalformedInputException;
import java.nio.charset.UnsupportedCharsetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * todo javadoc documentation
 * todo add "make backup" parameter
 * todo add "skip on error" parameter
 * todo add "recursive" parameter
 * todo accept folders array
 *
 * @author Aleksey Yablokov.
 */
@Mojo(name = Ascii2NativeMojo.MOJO_NAME)
public class Ascii2NativeMojo extends AbstractMojo {
    public static final String MOJO_NAME = "ascii2native";
    private static final String LOG_PREFIX = "Ascii2Native: ";

    @Parameter(required = true)
    @SuppressWarnings("unused")
    private File folder;

    @Parameter(required = true)
    @SuppressWarnings("unused")
    private String[] includes;

    @Parameter
    @SuppressWarnings({"unused", "MismatchedReadAndWriteOfArray"})
    private String[] charsets;
    private final List<Charset> charsetList = new ArrayList<>();

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        long startDate = System.currentTimeMillis();

        checkFolderParameter();
        checkIncludesParameter();
        checkCharsetsParameter();

        WildcardFileFilter fileFilter = new WildcardFileFilter(includes);
        IOFileFilter dirFileFilter = DirectoryFileFilter.INSTANCE;
        Collection<File> files = FileUtils.listFiles(folder, fileFilter, dirFileFilter);
        getLog().info(LOG_PREFIX + "Found files: " + files.size());
        try {
            int filesWrote = 0;
            int filesSkipped = 0;
            int readError = 0;
            for (File file : files) {
                getLog().debug(LOG_PREFIX + "Start file processing: " + file.getAbsolutePath());
                Path path = file.toPath();
                FileInfo info = readFileInAnyEncoding(path);
                if (!info.skip) {
                    boolean containsAscii = false;
                    for (int i = 0; i < info.lines.size(); i++) {
                        String line = info.lines.get(i);
                        if (line.contains("\\u")) {
                            info.lines.remove(i);
                            info.lines.add(i, Ascii2Native.nativeToAscii(line));
                            containsAscii = true;
                        }
                    }
                    if (containsAscii) {
                        Files.write(path, info.lines, info.charset);
                        filesWrote++;
                        getLog().debug(LOG_PREFIX + "Write file (" + info.charset.name() + "): " + file.getAbsolutePath());
                    } else {
                        filesSkipped++;
                        getLog().debug(LOG_PREFIX + "Skip file without ASCII symbols: " + file.getAbsolutePath());
                    }
                } else {
                    readError++;
                }
            }
            getLog().info(LOG_PREFIX + "Wrote files: " + filesWrote);
            getLog().info(LOG_PREFIX + "Skipped files (no ascii symbols): " + filesSkipped);
            getLog().info(LOG_PREFIX + "Skipped files (read error): " + readError);
            assert (files.size() == filesWrote + filesSkipped + readError);
            long finishDate = System.currentTimeMillis();
            getLog().info(LOG_PREFIX + "Process time (milliseconds): " + (finishDate - startDate));
        } catch (IOException e) {
            throw new MojoExecutionException(LOG_PREFIX + "Can't process file", e);
        }
    }

    private FileInfo readFileInAnyEncoding(Path path) throws IOException, MojoExecutionException {
        FileInfo fileInfo = new FileInfo(path);
        for (Charset charset : charsetList) {
            try {
                fileInfo.lines = Files.readAllLines(path, charset);
                fileInfo.skip = false;
                fileInfo.charset = charset;
                return fileInfo;
            } catch (MalformedInputException e) {
                getLog().warn(LOG_PREFIX + "Failed read file in charset " + charset.name() + ": " + path);
            }
        }
        getLog().warn(LOG_PREFIX + "Can't read file in any charset (skip it): " + path);
        fileInfo.skip = true;
        return fileInfo;
    }

    private void checkCharsetsParameter() throws MojoExecutionException {
        if (charsets != null && charsets.length > 0) {
            for (String charset : charsets) {
                try {
                    charsetList.add(Charset.forName(charset));
                } catch (IllegalCharsetNameException | UnsupportedCharsetException e) {
                    throw new MojoExecutionException(LOG_PREFIX + "Incorrect charset: " + charset);
                }
            }
        } else {
            charsetList.add(Charset.defaultCharset());
            getLog().warn(LOG_PREFIX + "Use default charset: " + Charset.defaultCharset().name());
        }
        getLog().info(LOG_PREFIX + "Charsets: " + charsetList);
    }

    private void checkIncludesParameter() throws MojoExecutionException {
        if (includes == null || includes.length == 0) {
            throw new MojoExecutionException(LOG_PREFIX + "Includes aren't specified.");
        }
        getLog().info(LOG_PREFIX + "Include masks: " + Arrays.deepToString(includes));
    }

    private void checkFolderParameter() throws MojoExecutionException {
        if (folder == null || !folder.exists()) {
            throw new MojoExecutionException(LOG_PREFIX + "Folder isn't exist: " + folder);
        }
        getLog().info(LOG_PREFIX + "Process folder: " + folder.getAbsolutePath());
    }

    static class FileInfo {
        public FileInfo(Path path) {
            this.path = path;
        }

        Path path;
        Charset charset;
        List<String> lines;
        boolean skip;
    }
}

