package luisf.ouroboros.analyzer;

import luisf.ouroboros.analyzer.models.DeclarationModel;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.analyzer.models.ClassModel;
import luisf.ouroboros.analyzer.models.ClassModelInterface;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;


public class CodeAnalyzer {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private final String fileExtensionFilter = "java";

    private File projectFilesFolder;

    private List<ClassModel> classModels;


    // ================================================================

    public CodeAnalyzer(File projectFilesFolder, List<ClassModel> classModels) {

        this.projectFilesFolder = projectFilesFolder;
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
//            log.info("------------------------");
//            log.info("package " + c.getPackageName());
//            log.info("class " + c.getClassName());
//
//            String[] methodNames = c.getMethodNames();
//            if (methodNames != null) {
//                Arrays.asList(c.getMethodNames()).forEach(name -> log.info(Handy.f("\t\t%s", name)));
//            } else {
//                log.info("\t\tClass has no methods");
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

        // parse class / interface / enum name
        String className = Parse.getClassName(fileContent);
        if (!Handy.isNullOrEmpty(className)) {
            classModel.setClassName(className);
        } else {
            className = Parse.getInterfaceName(fileContent);
            if (!Handy.isNullOrEmpty(className)) {
                classModel.setInterfaceName(className);
            } else {
                className = Parse.getEnumName(fileContent);
                if (!Handy.isNullOrEmpty(className)) {
                    classModel.setEnumName(className);
                } else {
                    log.warning("Could not parse the class or interface name!");
                    log.warning(fileContent);
                }
            }
        }

        // parse class methods'
        if (!classModel.isInterface()) {
            // filter text outside of the classes braces
            fileContent = Parse.extractClassContent(fileContent);

            // remove static blocks
            fileContent = Parse.removeStaticBlocks(fileContent);

            //List<DeclarationModel> declarations = Parse.extractClassDeclarations(fileContent);

            Map<String, String> methodsInfo = Parse.parseMethods(fileContent);

            if (methodsInfo.isEmpty()) {
                log.warning(Handy.f("Could not find any methods in the class %s.%s", packageName, className));
            }

            classModel.setClassMethods(methodsInfo);

        } else {
            // parse interface methods
            log.info("TODO: parse interface methods");
        }
    }
}
