package tr.edu.marmara;

import java.util.TreeSet;
import java.util.function.Predicate;

public class Lecture {
    private int quota;
    private Course course;
    private Lecturer lecturer;

    public Lecture(int quota, Course course, Lecturer lecturer) {
        this.quota = quota;
        this.course = course;
        this.lecturer = lecturer;
    }

    public int getQuota() {
        return quota;
    }

    public Course getCourse() {
        return course;
    }

    public Lecturer getLecturer() {
        return lecturer;
    }

    public void register() {
        quota--;
    }

    public boolean doesPrerequisitesSatisfy(TreeSet<Grade> grades) {
        return this
                .getCourse()
                .getPrerequisites()
                .stream()
                .allMatch(
                        new Predicate<Prerequisite>() {
                            @Override
                            public boolean test(Prerequisite prerequisite) {
                                return grades
                                        .stream()
                                        .anyMatch(
                                                new Predicate<Grade>() {
                                                    @Override
                                                    public boolean test(Grade grade) {
                                                        return grade.getCourse().equals(prerequisite.getCourse()) && prerequisite.minGrade.greaterOrEqual(grade.getLetterGrade());
                                                    }
                                                }
                                        );
                            }
                        }
                );
    }

    public Grade grade(LetterGrade letterGrade) {
        return new Grade(course, letterGrade);
    }
}
