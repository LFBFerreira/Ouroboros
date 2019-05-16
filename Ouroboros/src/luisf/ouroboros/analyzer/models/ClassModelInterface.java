package luisf.ouroboros.analyzer.models;

import java.util.Map;

public interface ClassModelInterface {
    void addMethod(MethodModel method);

    void setPackageName(String name);
    String getPackageName();

    void setClassName(String name);
    void setInterfaceName(String name);
    void setEnumName(String name);

    String getClassName();

    String getFullClassName();

    void setClassMethods(Map<String, String> methods);

    String[] getMethodNames();

    Boolean isInterface();
}
