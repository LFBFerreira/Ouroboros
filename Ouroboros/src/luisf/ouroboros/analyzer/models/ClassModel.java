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
    private List<DeclarationModel> declarations = new ArrayList<>();

    private String packageName = "";
    private String className = "";

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
        // TODO refactor this to include methods modifiers
        methodsCode.forEach((name, code) ->
                {
                    if (Handy.isNullOrEmpty(name) || Handy.isNullOrEmpty(code)) {
                        log.severe(Handy.f("The method name or code are empty for class %s", className));
                    } else {
                        methodModels.add(new MethodModel(this.className, name, code));
                    }
                }
        );
    }

    @Override
    public void setDeclarations(List<DeclarationModel> declarations) {
        this.declarations = declarations;
    }

    @Override
    public List<DeclarationModel> getDeclarations() {
        return declarations;
    }

    @Override
    public List<String> getMethodNames() {
        List<String> names = new ArrayList<>();

        if (!methodModels.isEmpty()) {
            methodModels.forEach(m -> names.add(m.name));
        }

        return names;
    }

    @Override
    public Boolean isInterface() {
        return isInterface;
    }

    // ================================================================

    // Helpers


}
