package luisf.ouroboros.model;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.parser.Parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;


public class ClassModel implements ClassModelInterface, Serializable {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private List<MethodModel> methodModels = new ArrayList<MethodModel>();
    private String packageName = "";
    private String className = "";

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
        className = name;
    }

    @Override
    public String getClassName() {
        return className;
    }

    @Override
    public String getFullClassName() {
        return packageName + "." + className;
    }

    @Override
    public void setClassMethods(List<String> methodsCode) {

        for (String code : methodsCode) {
            String name = Parse.getMethodName(code);
            //log.info("Method name: " + name);

            if (Handy.isNullOrEmpty(name)) {
                log.severe(Handy.f("Method name could not be parsed for %s.%s class", getPackageName(), getClassName()));
                //log.info("\n\n" + code + "\n\n");
            } else {
                methodModels.add(new MethodModel(className, name, code));
            }
        }
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

    // ================================================================

    // Helpers


}
