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
    public List<ModifierEnum> modifiers = new LinkedList<>();


    // ================================================================


    public MethodModel(String name, String code, List<ModifierEnum> modifiers) {
        this.name = name;
        this.code = code;
        this.modifiers = modifiers;
    }
}
