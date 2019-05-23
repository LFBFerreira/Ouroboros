package luisf.ouroboros.visualizer.skins;

import luisf.ouroboros.analyzer.models.MethodModel;

import java.util.logging.Logger;

public class MethodLine {

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    float size = 0;

    // ================================================================

    public MethodLine(MethodModel method, float size) {
        this.size = size;
    }

    // ================================================================

    // Public

    // ================================================================

    // Helpers
}
