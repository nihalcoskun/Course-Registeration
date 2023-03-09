package tr.edu.marmara;

import com.google.gson.JsonObject;

import java.util.Set;

public class Grade implements Serializable, Comparable<Grade> {
    private Course course;
    private LetterGrade letterGrade;

    public Grade(Course course, LetterGrade letterGrade) {
        this.course = course;
        this.letterGrade = letterGrade;
    }

    /**
     * Creates the grade by using JSON object
     *
     * @param json    is JsonObject, includes the Grade's
     *                credentials.
     * @param courses required for dependency injection.
     *                To avoid instance conflict, the System
     *                must provide the instances instead of
     *                making us re-creating them.
     *                (Provider Pattern)
     */
    public Grade(JsonObject json, Set<Course> courses) throws Exception {
        if (!json.has("grade"))
            throw new Exception("Transcript object must has grade property");
        if (!json.has("course"))
            throw new Exception("Transcript object must has course property");

        String courseCode = json.get("course").getAsString();
        this.letterGrade = new Helper().generateLG(json.get("grade").getAsString());

        for (Course c : courses) {
            if (c.getCode().compareTo(courseCode) == 0) {
                this.course = c;
                break;
            }
        }
    }

    public Course getCourse() {
        return course;
    }

    public LetterGrade getLetterGrade() {
        return letterGrade;
    }

    @Override
    public JsonObject toJson() {
        JsonObject object = new JsonObject();

        object.addProperty("course", course.getCode());
        object.addProperty("grade", letterGrade.toString());

        return object;
    }

    @Override
    public int compareTo(Grade g) {
        return course.compareTo(g.course);
    }
}
