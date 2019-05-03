package luisf.ouroboros.model;

import luisf.ouroboros.parser.CodeParser;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;



public class CodeModel implements CodeModelInterface{

    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private List<MethodModel> methods = new ArrayList<MethodModel>();

    // ================================================================

    public CodeModel()
    {

    }

    // ================================================================

    // CodeModelInterface

    @Override
    public void addMethod(MethodModel method) {
        if(method != null)
        {
            methods.add(method);
        }
    }

    // ================================================================

}
