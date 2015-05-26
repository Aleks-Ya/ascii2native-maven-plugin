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
import java.util.Collection;
import java.util.List;

/**
 * @author Aleksey Yablokov.
 */
@Mojo(name = "ascii2native")
public class Ascii2NativeMojo extends AbstractMojo {

    @Parameter(property = "ascii2native.folder", required = true)
    private String folder;

    //todo add recursive parameter

    @Parameter(property = "ascii2native.include", defaultValue = "*.properties")
    private String[] include;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        File dir = new File(folder);
        if (!dir.exists()) {
            throw new MojoExecutionException("Folder isn't exist: " + dir);
        }

        WildcardFileFilter fileFilter = new WildcardFileFilter(include);
        IOFileFilter dirFileFilter = DirectoryFileFilter.INSTANCE;
        Collection<File> files = FileUtils.listFiles(dir, fileFilter, dirFileFilter);
        try {
            for (File file : files) {
                Path path = file.toPath();
                List<String> lines = Files.readAllLines(path);
                for (int i = 0; i < lines.size(); i++) {
                    String line = lines.get(i);
                    lines.remove(i);
                    lines.add(i, Ascii2Native.nativeToAscii(line));
                }
                Files.write(path, lines, Charset.defaultCharset());
            }
        } catch (IOException e) {
            throw new MojoExecutionException("Can't process file", e);
        }
    }
}

