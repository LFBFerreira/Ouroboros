package luisf.ouroboros.analyzer.models;

import java.util.List;
import java.util.Map;

public interface ClassModelInterface {
    void setPackageName(String name);
    String getPackageName();

    void setClassName(String name);
    void setInterfaceName(String name);
    void setEnumName(String name);

    String getClassName();
    String getFullClassName();

    void setClassMethods(Map<String, String> methods);
    void setClassMethods(List<MethodModel> methods);
    List<String> getMethodNames();

    void setDeclarations(List<DeclarationModel> declarations);
    List<DeclarationModel> getDeclarations();

    Boolean isInterface();
}
