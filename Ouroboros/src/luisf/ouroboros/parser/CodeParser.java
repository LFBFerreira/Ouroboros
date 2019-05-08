package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.model.CodeModel;
import luisf.ouroboros.model.CodeModelInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;


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

        Parse.getFileListRecursive(projectFilesFolder.getPath(), fileList, fileExtensionFilter);

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

        CodeModelInterface model = new CodeModel();

        // parse file individually
        fileList.stream().forEach(f ->
        {
            parseFile(f, model);
            codeModels.add(model);
        });

        //parseFile(fileList.get(0), model);
        //codeModels.add(model);

        // debug print
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
        String fileContent = Parse.fileToString(file);

        codeModel.setPackageName(Parse.getPackageName(fileContent));
        codeModel.setClassName(Parse.getClassName(fileContent));

        String classCode = Parse.getClassCode(fileContent);
        //log.info("\n" + Parse.getClassCode(fileContent) + "\n");

        List<String> methodCodes = Parse.getOuterScopes(classCode);
        codeModel.setClassMethods(methodCodes);

        //methodCodes.stream().forEach(m -> log.info("\n" + m + "\n"));
    }
}
