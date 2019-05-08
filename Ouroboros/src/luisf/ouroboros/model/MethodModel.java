package luisf.ouroboros.model;

public class MethodModel {

    public String parentClassName = "";
    public String name = "";
    public String code = "";


    public MethodModel(String parentClassName, String name, String code) {
        this.parentClassName = parentClassName;
        this.name = name;
        this.code = code;
    }
}
