package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.model.ClassModel;
import luisf.ouroboros.model.ClassModelInterface;
import luisf.ouroboros.model.MethodModel;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;


public class CodeParser {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final String fileExtensionFilter = "java";

    private File projectFilesFolder;
    private File outputFolder;

    private List<ClassModel> classModels;


    // ================================================================

    public CodeParser(File projectFilesFolder, File outputFolder, List<ClassModel> classModels) {

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

        // parse files
        fileList.stream().forEach(f ->
        {
            log.info(Handy.f("Parsing '%s'", f.getAbsolutePath()));
            ClassModel classModel = new ClassModel();
            parseFile(f, classModel);
            classModels.add(classModel);
        });

        // debug print
//        classModels.stream().forEach(c ->
//        {
//            log.info("\n-----------");
//            log.info("package " + c.getPackageName());
//            log.info("class " + c.getClassName());
//
//            String[] methodNames = c.getMethodNames();
//            if (methodNames != null) {
//                Arrays.asList(c.getMethodNames()).forEach(name -> log.info(Handy.f("\t\t%s", name)));
//            } else {
//                log.warning("\t\tNo methods found in this class!");
//            }
//        });

        return !fileList.isEmpty();
    }

    public List<ClassModel> getModels() {
        return classModels;
    }


    // ================================================================

    // Helpers

    private void parseFile(File file, ClassModelInterface classModel) {
        String fileContent = Handy.fileToString(file);

        // parse package name
        String packageName = Parse.getPackageName(fileContent);
        if (!Handy.isNullOrEmpty(packageName)) {
            classModel.setPackageName(packageName);
        } else {
            log.warning("Could not parse the package name!");
            log.warning(fileContent);
        }

        // parse class name
        String className = Parse.getClassName(fileContent);
        if (!Handy.isNullOrEmpty(className)) {
            classModel.setClassName(className);
        } else {
            className = Parse.getInterfaceName(fileContent);

            if (!Handy.isNullOrEmpty(className)) {
                classModel.setInterfaceName(className);
            } else {
                log.warning("Could not parse the class or interface name!");
                log.warning(fileContent);
            }
        }

        if (!classModel.isInterface()) {

            // parse class methods
            String classCode = Parse.getClassMethodsText(fileContent);
            if (!Handy.isNullOrEmpty(classCode)) {
                List<String> methodCodes = Parse.getOuterScopes(classCode);
                Map<String, String> methodsInfo = new HashMap <String, String>();

                // get chunks of code for each method, and the name
                for (String code : methodCodes) {
                    String methodName = Parse.getMethodName(code);

                    if (Handy.isNullOrEmpty(methodName)) {
                        log.severe(Handy.f("Method name could not be parsed for the class %s.%s", packageName, className));
                    } else {
                        methodsInfo.put(methodName, code);
                    }
                }

                classModel.setClassMethods(methodsInfo);
            } else {
                log.warning("Could not parse the methods!");
                log.warning(fileContent);
            }

        } else {
            // parse interface methods
            log.info("TODO: parse interface methods");
        }

        //methodCodes.stream().forEach(m -> log.info("\n" + m + "\n"));
    }
}
