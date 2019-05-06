package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.model.CodeModel;
import luisf.ouroboros.model.CodeModelInterface;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


import static luisf.ouroboros.parser.RegexConstants.oneOrMoreSpaces;
import static luisf.ouroboros.parser.RegexConstants.anythingGroup;
import static luisf.ouroboros.parser.RegexConstants.anything;


public class CodeParser {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final String fileExtensionFilter = "java";

    private File projectFilesFolder;
    private File outputFolder;

    private List<CodeModelInterface> codeModels;

    // ================================================================

    public CodeParser(File projectFilesFolder, File outputFolder, List<CodeModelInterface> codeModels) {

        this.projectFilesFolder = projectFilesFolder;
        this.outputFolder = outputFolder;
        this.codeModels = codeModels;
    }

    // ================================================================

    // Public

    public Boolean parseFiles() {
        List<File> fileList = new ArrayList<File>();

        getFileListRecursive(projectFilesFolder.getPath(), fileList);

        // print file paths
        log.info(Handy.f("Found %d files", fileList.size()));
        //fileList.stream().forEach(f -> log.info(f.toString()));

        try {
            log.info(Handy.f("Starting with %s", fileList.get(0).getCanonicalPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // parse file
        int[] idx = {0};

        fileList.stream().forEach(f ->
        {
            CodeModelInterface model = new CodeModel();
            parseFile(f, model);
            codeModels.add(model);
        });

        //parseFile(fileList.get(0), codeModels);

        codeModels.stream().forEach(c ->
        {
            log.info("-----------");
            log.info("package " + c.getPackageName());
            log.info("class " + c.getClassName());
        });
        return !fileList.isEmpty();
    }


    // ================================================================

    // Helpers

    private void parseFile(File file, CodeModelInterface codeModel) {
        String fileContent = fileToString(file);

        codeModel.setPackageName(getPackageName(fileContent));
        codeModel.setClassName(getClassName(fileContent));

//        log.info("-----------");
//        log.info("package " + getPackageName(fileContent));
//        log.info("class " + getClassName(fileContent));


//        Pattern p = Pattern.compile("(</?[a-z]*>)");
//
//        Matcher matcher = p.matcher(fileContent);
//
//        while (matcher.find()) {
//
//            System.out.println(matcher.group(1));
//        }
    }

    private String getPackageName(String content) {
        Pattern pattern = Pattern.compile(anything + "package" + oneOrMoreSpaces + anythingGroup + oneOrMoreSpaces + "\\;");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    private String getClassName(String content) {
        Pattern pattern = Pattern.compile(anything + "class" + oneOrMoreSpaces + anythingGroup + oneOrMoreSpaces + "\\{");
        Matcher matcher = pattern.matcher(content);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
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

    private String fileToString(File file) {
        String content = "";
        try {
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            log.severe(Handy.f("Exception occurred while reading the file '%s'", file.toPath()));
            e.printStackTrace();
        }
        return content;
    }
}
