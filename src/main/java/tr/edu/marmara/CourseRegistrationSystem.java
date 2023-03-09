package tr.edu.marmara;

import com.google.gson.*;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class CourseRegistrationSystem {
    private static JsonObject whole;
    private List<Staff> staffs = new ArrayList<>();
    private List<Student> students = new ArrayList<>();
    private List<Lecturer> lecturers = new ArrayList<>();
    private Map<Course, JsonArray> courses = new TreeMap<>();
    private ActionReporter ar = new ActionReporter();
    private Settings s = new Settings();

    public void run(Reader input) throws Exception {
        gather(input);
        process();
    }

    public void write(Writer output) throws IOException {
        JsonArray array = new JsonArray();
        students.stream().map(Student::toJson).forEach(array::add);

        whole.remove("students");
        whole.add("students", array);

        output.write(new GsonBuilder().setPrettyPrinting().create().toJson(whole));
        output.close();
    }

    public void generate(int length) {
        students.clear();
        List<String> numbers = new ArrayList<>(RandomGenerator.generate(8 * length, s.numberLength));

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < length; ++j) {
                add(new Student(i + 1, "", "", numbers.get(i * length + j), s));
            }
        }
    }

    public void gather(Reader input) throws Exception {
        whole = new Gson().fromJson(input, JsonObject.class);
        input.close();

        if (!whole.has("settings")) throw new Exception("JSON must have settings property");
        if (!whole.has("courses")) throw new Exception("JSON must have courses property");
        if (!whole.has("staffs")) throw new Exception("JSON must have staffs property");
        if (!whole.has("students")) throw new Exception("JSON must have students property");

        JsonObject settings = whole.get("settings").getAsJsonObject();

        if (!settings.has("success")) throw new Exception("Setting object must have success property");
        if (!settings.has("maxQuota")) throw new Exception("Setting object must have maxQuota property");
        if (!settings.has("maxCredits")) throw new Exception("Setting object must have maxCredits property");
        if (!settings.has("mailPostfix")) throw new Exception("Setting object must have mailPostfix property");
        if (!settings.has("numberLength")) throw new Exception("Setting object must have numberLength property");
        if (!settings.has("successWeight")) throw new Exception("Setting object must have successWeight property");
        if (!settings.has("maxLectureCountPerCourse"))
            throw new Exception("Setting object must have maxLectureCountPerCourse property");

        s.success = new Helper().generateLG(settings.get("success").getAsString());
        s.maxQuota = settings.get("maxQuota").getAsInt();
        s.maxCredits = settings.get("maxCredits").getAsInt();
        s.mailPostfix = settings.get("mailPostfix").getAsString();
        s.numberLength = settings.get("numberLength").getAsInt();
        s.successWeight = settings.get("successWeight").getAsInt();
        s.maxLectureCountPerCourse = settings.get("maxLectureCountPerCourse").getAsInt();

        if (settings.has("electivePolicy") && settings.get("electivePolicy").isJsonObject()) {
            JsonObject electivePolicy = settings.get("electivePolicy").getAsJsonObject();

            for (Integer t : List.of(1, 2, 3, 4, 5, 6, 7, 8)) {
                if (electivePolicy.has(t.toString()) && electivePolicy.get(t.toString()).isJsonObject()) {
                    JsonObject term = electivePolicy.getAsJsonObject(t.toString());

                    for (String type : term.keySet()) {
                        if (!new Helper().isLegalElective(type)) return;
                        if (!term.get(type).isJsonPrimitive()) return;
                        if (!term.getAsJsonPrimitive(type).isNumber()) return;
                        try {
                            s.electivePolicy.get(t).put(new Helper().generateCT(type), term.get(type).getAsInt());
                        } catch (Exception e) {
                            ar.err(e);
                        }
                    }
                }

                if (t > 1) {
                    for (CourseType type : s.electivePolicy.get(t - 1).keySet()) {
                        if (s.electivePolicy.get(t).containsKey(type))
                            s.electivePolicy.get(t).replace(type, s.electivePolicy.get(t - 1).get(type));
                        else s.electivePolicy.get(t).put(type, s.electivePolicy.get(t - 1).get(type));
                    }
                }
            }
        }

        for (JsonElement element : whole.getAsJsonArray("courses").asList()) {
            try {
                if (element.getAsJsonObject().has("prerequisites"))
                    this.courses.put(new Course(element), element.getAsJsonObject().get("prerequisites").getAsJsonArray());
                else this.courses.put(new Course(element), new JsonArray());

            } catch (Exception e) {
                ar.err(e);
            }
        }

        for (Course course : this.courses.keySet()) {
            course.addPrerequisites(this.courses.get(course), this.courses.keySet());
        }

        for (JsonElement element : whole.getAsJsonArray("staffs").asList()) {
            try {
                this.staffs.add(new Staff(element, s));

                if (element.getAsJsonObject().has("isLecturer")) {
                    if (element.getAsJsonObject().get("isLecturer").getAsBoolean()) {
                        this.lecturers.add(new Lecturer(element, s));
                    }
                }
            } catch (Exception e) {
                ar.err(e);
            }
        }

        for (JsonElement element : whole.getAsJsonArray("students").asList()) {
            try {
                this.students.add(new Student(element.getAsJsonObject(), this.courses.keySet(), s));
            } catch (Exception e) {
                ar.err(e);
            }
        }
    }

    private void add(Student student) {
        if (student != null && students.contains(student)) return;
        students.add(student);
    }

    private void process() throws Exception {
        if (staffs.size() == 0) throw new Exception("There must be at least one stuff in the list");
        if (lecturers.size() == 0) throw new Exception("There must be at least one lecturer in the list");

        Map<Course, List<Lecture>> courseList = new TreeMap<>();

        for (Student student : this.students) {
            Collections.shuffle(staffs);
            staffs.get(0).getAdvising().add(student);

            courseList.clear();

            for (Course course : this.courses.keySet()) {
                courseList.put(course, new ArrayList<>());

                int lectureCount = new Random().nextInt(s.maxLectureCountPerCourse) + 1;
                for (int i = 0; i < lectureCount; ++i) {
                    Collections.shuffle(lecturers);
                    courseList.get(course).add(new Lecture(new Random().nextInt(s.maxQuota) + 1, course, lecturers.get(0)));
                }
            }

            student.submit(courseList);
        }

        this.staffs.forEach(Staff::startRegistrationProcess);

        this.students.forEach(Student::accommodateFinals);
    }

    public void printGPAs() {
        for (Student student : this.students) {
            ar.print(student.getNumber() + ": " + student.getGPA());
        }
    }
}
