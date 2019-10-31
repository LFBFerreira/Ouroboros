package luisf.ouroboros.common;

import luisf.ouroboros.models.ClassModel;

import java.util.List;

public class ProjectData {
    public String name = "";
    public List<ClassModel> classModels;

    public ProjectData(String name, List<ClassModel> classModels) {
        this.name = name;
        this.classModels = classModels;
    }
}
