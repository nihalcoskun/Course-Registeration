package tr.edu.marmara;

import com.google.gson.JsonObject;

import java.util.Set;

public class Prerequisite implements Comparable<Prerequisite> {
    Course course;
    LetterGrade minGrade;

    public Prerequisite(Course course, LetterGrade minGrade) {
        this.course = course;
        this.minGrade = minGrade;
    }

    public Prerequisite(JsonObject json, Set<Course> courses) throws Exception {
        if (!json.has("grade"))
            throw new Exception("Prerequisite object must has grade property");
        if (!json.has("course"))
            throw new Exception("Prerequisite object must has course property");

        String courseCode = json.get("course").getAsString();
        this.minGrade = new Helper().generateLG(json.get("grade").getAsString());

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

    public LetterGrade getMinGrade() {
        return minGrade;
    }

    @Override
    public int compareTo(Prerequisite p) {
        return course.compareTo(p.course);
    }
}
