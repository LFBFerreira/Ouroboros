package luisf.ouroboros.model;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.parser.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static luisf.ouroboros.parser.RegexConstants.*;


public class CodeModel implements CodeModelInterface {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private List<MethodModel> methods = new ArrayList<MethodModel>();
    private String packageName = "";
    private String className = "";

    // ================================================================

    public CodeModel() {

    }

    // ================================================================

    // CodeModelInterface

    @Override
    public void addMethod(MethodModel method) {
        if (method != null) {
            methods.add(method);
        }
    }

    @Override
    public void setPackageName(String name) {
        packageName = name;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public void setClassName(String name) {
        className = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public void setClassMethods(List<String> methodsCode) {

        for (String code : methodsCode) {
            String name = Parse.getMethodName(code);
            log.info("Method name: " + name);
            if (Handy.isNullOrEmpty(name)) {
                log.severe("NOOOOOOOOO");
                log.info("\n\n" + code + "\n\n");
            }

            //log.info("\n" + code + "\n");

//            Pattern pattern = Pattern.compile(anythingCharGroup + anyWhitespaceChar + "\\(" + anyWhitespaceChar + anyChar + anyWhitespaceChar + "\\)" + anyWhitespaceChar + "\\{");
//            Matcher matcher = pattern.matcher(code);
//
//            if (matcher.find()) {
//                methods.add(new MethodModel(className, matcher.group(1), code));
//            } else {
//                log.warning("The name of this method could not be parsed!");
//                //log.warning("\n" + code + "\n");
//            }
        }
    }

    // ================================================================

    // Helpers


}
