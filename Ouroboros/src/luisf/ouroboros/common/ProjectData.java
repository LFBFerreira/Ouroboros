package luisf.ouroboros.common;

import luisf.ouroboros.models.ClassModel;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class ProjectData {
    public String name = "";
    public LocalDateTime date;
    public String shortMessage;
    public String fullMessage;
    public List<ClassModel> classModels;

    /**
     * Constructor
     * @param classModels
     * @param id
     * @param date
     * @param shortMessage
     * @param fullMessage
     */
    public ProjectData(List<ClassModel> classModels, String id, LocalDateTime date, String shortMessage, String fullMessage) {
        this.name = id;
        this.classModels = new LinkedList<>(classModels);
        this.date = date;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
    }

    public ProjectData(String id, LocalDateTime date, String shortMessage, String fullMessage) {
        this( new LinkedList<>(), "", LocalDateTime.MIN, "", "");
    }

    public ProjectData(List<ClassModel> classModels) {
        this(classModels, "", LocalDateTime.MIN, "", "");
    }
}
