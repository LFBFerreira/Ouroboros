package luisf.ouroboros.analyzer.models;

import luisf.ouroboros.analyzer.VisibilityEnum;

import java.util.logging.Logger;

public class DeclarationModel {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    public VisibilityEnum visibility = VisibilityEnum.NONE;

    public String name = "";
    public String type = "";

    // ================================================================

    public DeclarationModel(String name, String type, VisibilityEnum visibility) {
        this.visibility = visibility;
        this.name = name;
        this.type = type;
    }

}
