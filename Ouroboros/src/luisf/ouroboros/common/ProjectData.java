package luisf.ouroboros.common;

import luisf.ouroboros.models.ClassModel;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class ProjectData {
    public String name = "";
    public LocalDateTime commitDate;
    public String shortMessage;
    public String fullMessage;
    public List<ClassModel> classModels;

    /**
     * Constructor
     * @param classModels
     * @param id
     * @param commitDate
     * @param shortMessage
     * @param fullMessage
     */
    public ProjectData(List<ClassModel> classModels, String id, LocalDateTime commitDate, String shortMessage, String fullMessage) {
        this.name = id;
        this.classModels = new LinkedList<>(classModels);
        this.commitDate = commitDate;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
    }

    public ProjectData(String id, LocalDateTime commitDate, String shortMessage, String fullMessage) {
        this( new LinkedList<>(), "", commitDate, "", "");
    }

    public ProjectData(List<ClassModel> classModels) {
        this(classModels, "", LocalDateTime.MIN, "", "");
    }
}
