package tr.edu.marmara;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Staff extends Person {
    private List<Student> advising = new ArrayList<>();
    private ActionReporter ar = new ActionReporter();
    private Settings s;


    public Staff(Settings s) {
        super();
        this.s = s;
    }

    public Staff(JsonElement element, Settings s) throws Exception {
        this.s = s;
        JsonObject json = element.getAsJsonObject();

        if (!json.has("name"))
            throw new Exception("Staff object must has name property");
        if (!json.has("surname"))
            throw new Exception("Staff object must has surname property");

        this.setName(json.get("name").getAsString());
        this.setSurname(json.get("surname").getAsString());
    }

    @Override
    public String getEmail() {
        return String.format("%s.%s@%s", getName().toLowerCase(), getSurname().toLowerCase(), s.mailPostfix);
    }

    public List<Student> getAdvising() {
        return advising;
    }

    public void startRegistrationProcess() {
        this.getAdvising().forEach(student -> student.getSubmissions().forEach(lecture -> {
            //check if took that course and success
            if (student
                    .getGrades()
                    .stream()
                    .anyMatch(
                            new Predicate<Grade>() {
                                @Override
                                public boolean test(Grade g) {
                                    return g.getCourse().equals(lecture.getCourse()) && s.success.greaterOrEqual(g.getLetterGrade());
                                }
                            }
                    )
            ) {
                ar.print(student.getNumber() + " already success " + lecture.getCourse().getCode());
                return;
            }

            //check if satisfies prerequisites
            if (!lecture.getCourse().getPrerequisites().stream().allMatch(new Predicate<Prerequisite>() {
                @Override
                public boolean test(Prerequisite prerequisite) {
                    return student.getGrades().stream().anyMatch(new Predicate<Grade>() {
                        @Override
                        public boolean test(Grade grade) {
                            return grade.getCourse().equals(prerequisite.getCourse()) && prerequisite.getMinGrade().greaterOrEqual(grade.getLetterGrade());
                        }
                    });
                }
            })) {
                ar.print(student.getNumber() + " doesn't satisfy all the prerequisites of " + lecture.getCourse().getCode());
                return;
            }

            //check if there are enough credit to take that course
            if (student.getRemainCredits() < lecture.getCourse().getCredit()) {
                ar.print(student.getNumber() + " doesn't have enough credits for " + lecture.getCourse().getCode());
                return;
            }

            //check if is there quota
            if (lecture.getQuota() < 1) {
                ar.print(student.getNumber() + " can't take " + lecture.getCourse().getCode() + " due to quota");
                return;
            }

            lecture.register();
            student.getRegistrations().add(lecture);
            ar.print(student.getNumber() + " registered to " + lecture.getCourse().getCode());
        }));
    }
}
