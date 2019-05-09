package luisf.ouroboros.model;

import luisf.ouroboros.common.Handy;
import luisf.ouroboros.parser.Parse;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.IntStream;


public class ClassModel implements ClassModelInterface {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private List<MethodModel> methods = new ArrayList<MethodModel>();
    private String packageName = "";
    private String className = "";

    // ================================================================

    public ClassModel() {

    }

    // ================================================================

    // ClassModelInterface

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
            //log.info("Method name: " + name);

            if (Handy.isNullOrEmpty(name)) {
                log.severe("Method name could not be parsed");
                log.info("\n\n" + code + "\n\n");
            } else {
                methods.add(new MethodModel(className, name, code));
            }
        }
    }

    @Override
    public String[] getMethodNames() {
        if (methods.isEmpty()) {
            return null;
        }

        String[] names = new String[methods.size()];

        IntStream.range(0, methods.size()).forEach(i ->
        {
            names[i] = methods.get(i).name;
        });

        return names;
    }

    // ================================================================

    // Helpers


}
