package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.model.ClassModel;
import luisf.ouroboros.model.ClassModelInterface;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;


public class CodeParser {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final String fileExtensionFilter = "java";

    private File projectFilesFolder;
    private File outputFolder;

    private List<ClassModelInterface> classModels;


    // ================================================================

    public CodeParser(File projectFilesFolder, File outputFolder, List<ClassModelInterface> classModels) {

        this.projectFilesFolder = projectFilesFolder;
        this.outputFolder = outputFolder;
        this.classModels = classModels;
    }

    // ================================================================

    // Public

    public Boolean parseFiles() {
        List<File> fileList = new ArrayList<File>();

        Parse.getFileListRecursive(projectFilesFolder.getPath(), fileList, fileExtensionFilter);

        // print file paths
        log.info(Handy.f("Found %d files", fileList.size()));

        try {
            log.info(Handy.f("Starting with %s", fileList.get(0).getCanonicalPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // parse files
        fileList.stream().forEach(f ->
        {
            ClassModelInterface classModel = new ClassModel();
            parseFile(f, classModel);
            classModels.add(classModel);
        });

        // parse file individually
        //parseFile(fileList.get(0), model);
        //classModels.add(model);

        // debug print

        classModels.stream().forEach(c ->
        {
            log.info("\n-----------");
            log.info("package " + c.getPackageName());
            log.info("class " + c.getClassName());

            String[] methodNames = c.getMethodNames();
            if (methodNames != null) {
                Arrays.asList(c.getMethodNames()).forEach(name -> log.info(Handy.f("\t\t%s", name)));
            } else {
                log.warning("\t\tNo methods found in this class!");
            }

        });

        return !fileList.isEmpty();
    }


    // ================================================================

    // Helpers

    private void parseFile(File file, ClassModelInterface classModel) {
        String fileContent = Parse.fileToString(file);

        String packageName = Parse.getPackageName(fileContent);
        if (!Handy.isNullOrEmpty(packageName)) {
            classModel.setPackageName(packageName);
        }
        else
        {
            log.warning("Could not parse the package name!");
        }

        String className = Parse.getClassName(fileContent);
        if (!Handy.isNullOrEmpty(className)) {
            classModel.setClassName(className);
        }
        else
        {
            log.warning("Could not parse the class name!");
        }

        String classCode = Parse.getClassCode(fileContent);
        if (!Handy.isNullOrEmpty(classCode)) {
            List<String> methodCodes = Parse.getOuterScopes(classCode);
            classModel.setClassMethods(methodCodes);
        }
        else
        {
            log.warning("Could not parse the class code!");
        }

        //methodCodes.stream().forEach(m -> log.info("\n" + m + "\n"));
    }
}
