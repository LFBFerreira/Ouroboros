package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.model.CodeModelInterface;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class CodeParser {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final String fileExtensionFilter = "java";

    private File projectFilesFolder;
    private File outputFolder;

    private CodeModelInterface codeModel;

    // ================================================================

    public CodeParser(File projectFilesFolder, File outputFolder, CodeModelInterface codeModel) {

        this.projectFilesFolder = projectFilesFolder;
        this.outputFolder = outputFolder;
        this.codeModel = codeModel;

        log.info(Handy.f("project files: %s", projectFilesFolder.getAbsolutePath()));
        log.info(Handy.f("output: %s", outputFolder.getAbsolutePath()));
    }

    // ================================================================

    // Public

    public Boolean parseFiles() {
        List<File> fileList = new ArrayList<File>();

        getFileListRecursive(projectFilesFolder.getPath(), fileList);

        // print file paths
        log.info(Handy.f("Found %d files", fileList.size()));
        fileList.stream().forEach(f -> log.info(f.toString()));

        // parse file
        fileList.stream().forEach(f -> parseFile(f, codeModel));

        return !fileList.isEmpty();
    }


    // ================================================================

    // Helpers

    private void parseFile(File filePath, CodeModelInterface codeModel)
    {

    }

    private void getFileListRecursive(String folderPath, List<File> files) {
        File[] topLevelFiles = new File(folderPath).listFiles();

        //log.info(Handy.f("Folder: %s (%d)", folderPath, topLevelFiles.length));

        if (topLevelFiles != null)
            for (File file : topLevelFiles) {
                if (file.isFile() && Handy.getFileExtension(file).equals(fileExtensionFilter)) {
                    files.add(file);
                } else if (file.isDirectory()) {
                    getFileListRecursive(file.getAbsolutePath(), files);
                }
            }
    }
}
