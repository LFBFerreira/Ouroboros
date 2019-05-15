package luisf.ouroboros.parser;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.model.ClassModel;
import luisf.ouroboros.model.ClassModelInterface;

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
        // read whole file to a string
        String fileContent = Handy.fileToString(file);

        // remove comments to make the parsing later easier
        fileContent = Parse.removeComments(fileContent);

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
            // keep only the content of the class, whats inside braces
            fileContent = Parse.extractClassContent(fileContent);

            // remove static blocks
            fileContent = Parse.removeStaticBlocks(fileContent);


            // parse class methods
            List<String> methodCodes = Parse.getOuterScopeContent(fileContent);
            Map<String, String> methodsInfo = new HashMap<String, String>();

            // get chunks of code for each method, and the name
            for (String code : methodCodes) {
                String methodName = Parse.getMethodName(code);


                if (Handy.isNullOrEmpty(methodName)) {
                    log.severe(Handy.f("Method name could not be parsed for the class %s.%s", packageName, className));
                    //log.info(code);
                } else {

                    String cleanCode = code; //Parse.removeStaticBlocks(code);
                    int openBraceIndex = Parse.getMethodStartBrace(code);

                    if (openBraceIndex > 0) {
                        int closingBraceIndex = Parse.getMatchingBraceIndex(cleanCode, openBraceIndex);

                        if (closingBraceIndex > -1) {
                            // increment once to ignore the open brace
                            openBraceIndex++;

                            cleanCode = cleanCode.substring(openBraceIndex, closingBraceIndex);
                        } else {
                            log.warning(Handy.f("Could not find the closing brace for this method (open @ %d): \n%s",
                                    openBraceIndex, cleanCode));
                        }
                    } else {
                        log.warning(Handy.f("Could not find the opening brace for this method: \n%s", cleanCode));
                    }

                    if (Handy.isNullOrEmpty(cleanCode)) {
                        log.warning(Handy.f("The method $s has no code", methodName));
                    } else {
                        methodsInfo.put(methodName, cleanCode);
                    }
                }
            }

            classModel.setClassMethods(methodsInfo);

        } else {
            // parse interface methods
            log.info("TODO: parse interface methods");
        }
    }
}
