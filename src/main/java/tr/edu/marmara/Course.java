package tr.edu.marmara;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Set;
import java.util.TreeSet;

public class Course implements Comparable<Course> {
    private int semester;
    private int credit;
    private String code;
    private String name;
    private CourseType type;
    private TreeSet<Prerequisite> prerequisites = new TreeSet<>();

    private ActionReporter ar = new ActionReporter();

    public Course(int semester, int credit, String code, String name, CourseType type) {
        this.semester = semester;
        this.credit = credit;
        this.code = code;
        this.name = name;
        this.type = type;
    }

    public Course(JsonElement element) throws Exception {
        JsonObject json = element.getAsJsonObject();

        if (!json.has("semester"))
            throw new Exception("The course object must have semester property");
        if (!json.has("credit"))
            throw new Exception("The course object must have credit property");
        if (!json.has("code"))
            throw new Exception("The course object must have code property");
        if (!json.has("name"))
            throw new Exception("The course object must have name property");
        if (!json.has("type"))
            throw new Exception("The course object must have type property");

        semester = json.get("semester").getAsInt();
        credit = json.get("credit").getAsInt();
        code = json.get("code").getAsString();
        name = json.get("name").getAsString();
        type = new Helper().generateCT(json.get("type").getAsString());
    }

    public int getCredit() {
        return credit;
    }

    public int getSemester() {
        return semester;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public CourseType getType() {
        return type;
    }

    public TreeSet<Prerequisite> getPrerequisites() {
        return prerequisites;
    }

    @Override
    public int compareTo(Course c) {
        if (semester == c.semester) {
            return code.compareTo(c.code);
        }
        return semester - c.semester;
    }

    public void addPrerequisites(JsonArray array, Set<Course> courses) {
        array.asList().forEach(item -> {
            try {
                this.getPrerequisites().add(new Prerequisite(item.getAsJsonObject(), courses));
            } catch (Exception e) {
                ar.err(e);
            }
        });
    }
}
