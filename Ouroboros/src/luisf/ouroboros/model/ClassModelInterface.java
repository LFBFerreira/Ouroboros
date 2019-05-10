package luisf.ouroboros.model;

import java.util.List;

public interface ClassModelInterface {
    void addMethod(MethodModel method);

    void setPackageName(String name);
    String getPackageName();

    void setClassName(String name);
    String getClassName();

    String getFullClassName();

    void setClassMethods(List<String> methods);
    String[] getMethodNames();
}
