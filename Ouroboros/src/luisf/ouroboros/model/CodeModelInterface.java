package luisf.ouroboros.model;

public interface CodeModelInterface {
    void addMethod(MethodModel method);

    void setPackageName(String name);
    String getPackageName();

    void setClassName(String name);
    String getClassName();

}
