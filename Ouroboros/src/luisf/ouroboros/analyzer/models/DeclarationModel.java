package luisf.ouroboros.analyzer.models;

import luisf.ouroboros.analyzer.ModifierEnum;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class DeclarationModel implements Serializable {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public List<ModifierEnum> modifiers = new ArrayList<>();
    public String name = "";
    public String type = "";

    // ================================================================

    public DeclarationModel(String name, String type, List<ModifierEnum> modifiers) {
        this.modifiers = modifiers;
        this.name = name;
        this.type = type;
    }

}
