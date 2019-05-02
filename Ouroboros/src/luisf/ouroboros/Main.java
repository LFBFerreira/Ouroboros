package luisf.ouroboros;

import com.sun.org.apache.bcel.internal.classfile.Code;
import luisf.ouroboros.codeParser.CodeParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Main {

    public static void main(String[] args) throws IOException, IllegalArgumentException {
        System.out.println("Hello World!");
        CodeParser parser = new CodeParser();
        System.out.println(parser);

        ClassLoader classLoader = new Main().getClass().getClassLoader();
        File file = parser.getFileFromResources("configuration.xml");

        if (file != null)
        {
            System.out.println("Got it!");
        }
        else
        {
            System.out.println("Failed file");
        }

        ClassLoader classloader = Thread.currentThread().getContextClassLoader();
        InputStream is = classloader.getResourceAsStream("configuration.xml");

        if (is != null)
        {
            System.out.println("Got the stream!");
        }
        else
        {
            System.out.println("Failed stream");
        }
    }
}
