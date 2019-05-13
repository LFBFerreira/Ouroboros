package luisf.ouroboros.model;

import java.util.List;
import java.util.Map;

public interface ClassModelInterface {
    void addMethod(MethodModel method);

    void setPackageName(String name);
    String getPackageName();

    void setClassName(String name);
    String getClassName();

    void setInterfaceName(String name);

    String getFullClassName();

    void setClassMethods(Map<String, String> methods);

    String[] getMethodNames();

    Boolean isInterface();
}
