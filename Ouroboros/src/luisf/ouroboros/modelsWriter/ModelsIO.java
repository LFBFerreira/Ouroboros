package luisf.ouroboros.modelsWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import luisf.ouroboros.common.Handy;
import luisf.ouroboros.models.ClassModel;
import luisf.ouroboros.models.ClassModelInterface;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

public class ModelsIO {
    private static Logger log = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

    private File modelsFolder;
    private String outputFolderString = "";
    private String jsonExtension = ".json";
    private String ouroborosExtension = ".oro";

    // ================================================================

    public ModelsIO(File modelsFolder) {
        this.modelsFolder = modelsFolder;

        try {
            outputFolderString = modelsFolder.getCanonicalPath();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ================================================================

    // Public

    public Boolean saveModelsBinary(List<ClassModel> models) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {

            for (ClassModel model : models) {
                fos = new FileOutputStream(getModelFilePath(model, outputFolderString, ouroborosExtension));
                oos = new ObjectOutputStream(fos);
                oos.writeObject(model);
                oos.close();
                fos.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public Boolean saveModels(List<ClassModel> models) {
        if (models.isEmpty()) {
            log.warning("No models!");
            return false;
        }

        Boolean result = true;

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enableDefaultTyping();

        for (ClassModel model : models) {
            if (Handy.isNullOrEmpty(model.getPackageName()) ||
                    Handy.isNullOrEmpty(model.getClassName())) {
                log.warning("Found a class without a package or name. Skipping it");
                continue;
            }

            File filePath = getModelFilePath(model, outputFolderString, jsonExtension);

            try {
                objectMapper.writeValue(filePath, model);
            } catch (IOException e) {
                log.severe(Handy.f("Could not write the model to '%s'", filePath.getPath()));
                e.printStackTrace();
                result = false;
            }
        }

        return result;
    }

    public List<ClassModel> loadModels() {
        List<ClassModel> models = new LinkedList<ClassModel>();

        File[] modelFiles = modelsFolder.listFiles();
        ObjectMapper objectMapper = new ObjectMapper();

        for (File modelFile : modelFiles) {
            String modelString = Handy.fileToString(modelFile);

            ClassModel model = null;
            try {
                model = objectMapper.readValue(modelString, ClassModel.class);
            } catch (IOException e) {
                log.severe(Handy.f("An exception occurred while parsing the model file '%s'", modelFile.getPath()));
                e.printStackTrace();
            }

            models.add(model);
        }

        return models;
    }

    public List<ClassModel> loadModelsBinary() {
        List<ClassModel> models = new LinkedList<ClassModel>();

        File[] modelFiles = modelsFolder.listFiles();

        for (File modelFile : modelFiles) {
            if (!Handy.getFileExtension(modelFile).equals(ouroborosExtension.replace(".", "")))
            {
                log.info("Ignoring a file with incompatible extension: %s" + modelFile.getAbsolutePath());
                continue;
            }

            ClassModel model = null;
            FileInputStream fis = null;
            ObjectInputStream ois = null;

            try {
                fis = new FileInputStream(modelFile);
                ois = new ObjectInputStream(fis);
                model = (ClassModel)ois.readObject();
                ois.close();
                fis.close();
            } catch (IOException e) {
                log.severe(Handy.f("An exception occurred while parsing the model file '%s'", modelFile.getPath()));
                e.printStackTrace();
            }catch (ClassNotFoundException e) {
                log.severe(Handy.f("An exception occurred while parsing the model file '%s'", modelFile.getPath()));
                e.printStackTrace();
            }

            models.add(model);
        }

        return models;
    }

    // ================================================================

    // Helpers


    private File getModelFilePath(ClassModelInterface model, String outputFolder, String extension) {
        String fileName = model.getFullClassName();

        fileName = fileName.replace('.', '_');
        fileName = fileName.concat(extension);

        return Paths.get(outputFolder, fileName).toFile();
    }
}
