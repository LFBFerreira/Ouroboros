package luisf.ouroboros.analyzer.models;

import luisf.ouroboros.analyzer.ModifierEnum;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class MethodModel implements Serializable {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public String name = "";
    public String code = "";
    public String returnType = "";
    public List<ModifierEnum> modifiers = new LinkedList<>();
    public List<CodeModel> metrics;

    // ================================================================


    public MethodModel(String name, String code, List<ModifierEnum> modifiers, String returnType, List<CodeModel> metrics) {
        this.name = name;
        this.code = code;
        this.modifiers = modifiers;
        this.returnType = returnType;
        this.metrics = metrics;
    }
}
