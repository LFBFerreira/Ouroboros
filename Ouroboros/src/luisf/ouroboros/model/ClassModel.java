package luisf.ouroboros.model;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.parser.Parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.stream.IntStream;


public class ClassModel implements ClassModelInterface, Serializable {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private List<MethodModel> methodModels = new ArrayList<MethodModel>();
    private String packageName = "";
    private String className = "";

    public Boolean isInterface = false;

    public String test = "This is a test";

    // ================================================================

    public ClassModel() {

    }

    // ================================================================

    // ClassModelInterface

    @Override
    public void addMethod(MethodModel method) {
        if (method != null) {
            methodModels.add(method);
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
        isInterface = false;
        this.className = name;
    }

    @Override
    public void setInterfaceName(String name) {
        isInterface = true;
        this.className = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getFullClassName() {
        return packageName + "." + className;
    }

//    @Override
//    public void setClassMethods(List<String> methodsCode) {
//
//        for (String code : methodsCode) {
//            String name = Parse.getMethodName(code);
//            //log.info("Method className: " + className);
//
//            if (Handy.isNullOrEmpty(name)) {
//                log.severe(Handy.f("Method className could not be parsed for %s.%s class", getPackageName(), getClassName()));
//                //log.info("\n\n" + code + "\n\n");
//            } else {
//                methodModels.add(new MethodModel(this.className, name, code));
//            }
//        }
//    }

    @Override
    public void setClassMethods(Map<String, String> methodsCode) {
        methodsCode.forEach((name, code) ->
        {
            if (Handy.isNullOrEmpty(name) || Handy.isNullOrEmpty(code))
            {
                log.severe(Handy.f("The method name or code are empty for class %s", className));
            }
            methodModels.add(new MethodModel(this.className, name, code));
        }
        );
    }

    @Override
    public String[] getMethodNames() {
        if (methodModels.isEmpty()) {
            return null;
        }

        String[] names = new String[methodModels.size()];

        IntStream.range(0, methodModels.size()).forEach(i ->
        {
            names[i] = methodModels.get(i).name;
        });

        return names;
    }

    @Override
    public Boolean isInterface() {
        return isInterface;
    }

    // ================================================================

    // Helpers


}
