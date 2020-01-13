package luisf.ouroboros.common;

import luisf.ouroboros.models.ClassModel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class ProjectData {
    public String id = "";
    public LocalDateTime commitDate;
    public String shortMessage;
    public String fullMessage;
    public List<ClassModel> classModels;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");

    /**
     * Constructor
     *
     * @param classModels
     * @param id
     * @param commitDate
     * @param shortMessage
     * @param fullMessage
     */
    public ProjectData(List<ClassModel> classModels, String id, LocalDateTime commitDate, String shortMessage, String fullMessage) {
        this.id = id;
        this.classModels = new LinkedList<>(classModels);
        this.commitDate = commitDate;
        this.shortMessage = shortMessage;
        this.fullMessage = fullMessage;
    }

    public ProjectData(String id, LocalDateTime commitDate, String shortMessage, String fullMessage) {
        this(new LinkedList<>(), id, commitDate, shortMessage, fullMessage);
    }

    public ProjectData(List<ClassModel> classModels) {
        this(classModels, "", LocalDateTime.MIN, "", "");
    }

    public void addModels(List<ClassModel> newModels) {
        classModels.addAll(newModels);
    }

    public String getCommitDateFormated() {
        return commitDate.format(formatter);
    }
}
