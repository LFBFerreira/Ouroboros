package luisf.ouroboros.model;

import java.util.List;

public interface CodeModelInterface {
    void addMethod(MethodModel method);

    void setPackageName(String name);
    String getPackageName();

    void setClassName(String name);
    String getClassName();

    void setClassMethods(List<String> methods);
}
