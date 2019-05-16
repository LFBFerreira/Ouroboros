package luisf.ouroboros.analyzer.models;

import luisf.ouroboros.common.Handy;

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
    private List<DeclarationModel> localDeclarations = new ArrayList<DeclarationModel>();

    public Boolean isInterface = false;


    // ================================================================

    /**
     * Constructor
     */
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
    public void setEnumName(String name) {
        // TODO specify the type with enum instead of boolean
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

    @Override
    public void setClassMethods(Map<String, String> methodsCode) {
        log.info("Class " + className);
        methodsCode.forEach((name, code) ->
                {
                    if (Handy.isNullOrEmpty(name) || Handy.isNullOrEmpty(code)) {
                        log.severe(Handy.f("The method name or code are empty for class %s", className));
                    } else {
                        methodModels.add(new MethodModel(this.className, name, code));
                    }
                    //log.info("Method " + name);
                    //log.info("Code\n" + code + "\n");
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
