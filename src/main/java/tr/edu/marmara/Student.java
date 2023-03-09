package tr.edu.marmara;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Student extends Person implements Serializable, Comparable<Student> {
    private int semester;
    private String number;
    private TreeSet<Grade> grades = new TreeSet<>();
    private List<Lecture> submissions = new ArrayList<>();
    private List<Lecture> registrations = new ArrayList<>();
    private ActionReporter ar = new ActionReporter();
    private Settings s;

    public Student(int semester, String name, String surname, String number, Settings s) {
        super(name, surname);
        this.semester = semester;
        this.number = number;
        this.s = s;
    }

    /**
     * Creates the student by using JSON object
     *
     * @param json    is JsonObject, includes the Student's
     *                credentials.
     * @param courses required for dependency injection.
     *                To avoid instance conflict, the System
     *                must provide the instances instead of
     *                making us re-creating them.
     *                (Provider Pattern)
     */
    public Student(JsonObject json, Set<Course> courses, Settings s) throws Exception {
        this.s = s;
        if (!json.has("semester"))
            throw new Exception("Student object must has semester property.");
        if (!json.has("number"))
            throw new Exception("Student object must has number property.");
        if (!json.has("name"))
            throw new Exception("Student object must has name property.");
        if (!json.has("surname"))
            throw new Exception("Student object must has surname property.");
        if (!json.has("grades"))
            throw new Exception("Student object must has grades property.");

        this.semester = json.get("semester").getAsInt();
        this.number = json.get("number").getAsString();
        this.setName(json.get("name").getAsString());
        this.setSurname(json.get("surname").getAsString());

        for (JsonElement array : json.get("grades").getAsJsonArray().asList()) {
            try {
                this.getGrades().add(new Grade(array.getAsJsonObject(), courses));
            } catch (Exception e) {
                ar.err(e);
            }
        }
    }

    public int getSemester() {
        return semester;
    }

    public String getNumber() {
        return number;
    }

    public TreeSet<Grade> getGrades() {
        return grades;
    }

    public List<Lecture> getSubmissions() {
        return submissions;
    }

    public List<Lecture> getRegistrations() {
        return registrations;
    }

    @Override
    public String getEmail() {
        return String.format("%s@%s", number, s.mailPostfix);
    }

    @Override
    public JsonObject toJson() {
        JsonArray array = new JsonArray();
        JsonObject object = new JsonObject();

        for (Grade grade : grades) {
            array.add(grade.toJson());
        }

        object.addProperty("semester", semester);
        object.addProperty("number", number);
        object.addProperty("name", getName());
        object.addProperty("surname", getSurname());
        object.add("grades", array);
        return object;
    }

    public void submit(Map<Course, List<Lecture>> courseList) {
        //firstly try to take un-taken and failed compulsary courses
        int semester = this.semester;
        TreeSet<Grade> grades = this.grades;
        List<Lecture> submissions = this.submissions;

        courseList
                .keySet()
                .stream()
                .filter(new Predicate<Course>() {
                    @Override
                    public boolean test(Course course) {
                        return course.getSemester() <= semester;
                    }
                })
                .filter(new Predicate<Course>() {
                    @Override
                    public boolean test(Course course) {
                        return course.getType() == CourseType.CC;
                    }
                })
                .filter(new Predicate<Course>() {
                    @Override
                    public boolean test(Course course) {
                        Grade grade = grades
                                .stream()
                                .filter(new Predicate<Grade>() {
                                    @Override
                                    public boolean test(Grade grade) {
                                        return grade.getCourse().equals(course);
                                    }
                                })
                                .findAny()
                                .orElse(null);


                        return grade == null || s.success.smaller(grade.getLetterGrade());
                    }
                })
                .forEach(new Consumer<Course>() {
                    @Override
                    public void accept(Course course) {
                        List<Lecture> lectureList = courseList.get(course);
                        submissions.add(lectureList.get(new Random().nextInt(lectureList.size())));
                    }
                });

        //afterwards try to take electives regarding elective policy on settings
        s.electivePolicy
                .keySet()
                .stream()
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) {
                        return integer <= semester;
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) {
                        return integer % 2 == semester % 2;
                    }
                })
                .filter(new Predicate<Integer>() {
                    @Override
                    public boolean test(Integer integer) {
                        return s.electivePolicy.get(integer).size() > 0;
                    }
                })
                .forEach(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) {
                        s.electivePolicy.get(integer).keySet().forEach(new Consumer<CourseType>() {
                            @Override
                            public void accept(CourseType policy) {
                                int necessaryCount = s.electivePolicy.get(integer).get(policy);
                                int successCount = (int) grades
                                        .stream()
                                        .filter(new Predicate<Grade>() {
                                            @Override
                                            public boolean test(Grade grade) {
                                                return grade.getCourse().getType().equals(policy);
                                            }
                                        })
                                        .filter(new Predicate<Grade>() {
                                            @Override
                                            public boolean test(Grade grade) {
                                                return s.success.greaterOrEqual(grade.getLetterGrade());
                                            }
                                        })
                                        .count();

                                if (necessaryCount <= successCount) return;

                                List<Course> cl = courseList
                                        .keySet()
                                        .stream()
                                        .filter(new Predicate<Course>() {
                                            @Override
                                            public boolean test(Course course) {
                                                return course.getType().equals(policy);
                                            }
                                        })
                                        .collect(Collectors.toList());

                                Collections.shuffle(cl);

                                cl.stream().limit(necessaryCount - successCount)
                                        .forEach(new Consumer<Course>() {
                                            @Override
                                            public void accept(Course course) {
                                                {
                                                    List<Lecture> lectureList = courseList.get(course);
                                                    submissions.add(lectureList.get(new Random().nextInt(lectureList.size())));
                                                }
                                            }
                                        });
                            }
                        });
                    }
                });
    }

    public int getRemainCredits() {
        return s.maxCredits - this.getRegistrations()
                .stream()
                .reduce(
                        0,
                        new BiFunction<Integer, Lecture, Integer>() {
                            @Override
                            public Integer apply(Integer subtotal, Lecture current) {
                                return subtotal + current.getCourse().getCredit();
                            }
                        },
                        Integer::sum
                );
    }

    public void accommodateFinals() {
        this.getRegistrations().forEach(registrations -> {
            boolean success = new Random().nextInt(101) <= s.successWeight;
            Grade grade = registrations.grade(success ? new Helper().getRandomSuccessGrade(s) : new Helper().getRandomFailGrade(s));
            this.getGrades().removeIf(new Predicate<Grade>() {
                @Override
                public boolean test(Grade g) {
                    return g.getCourse().equals(grade.getCourse());
                }
            });
            this.getGrades().removeIf(new Predicate<Grade>() {
                @Override
                public boolean test(Grade g) {
                    return g.getCourse().getType() != CourseType.CC && g.getCourse().getType() == grade.getCourse().getType() && g.getCourse().getSemester() == grade.getCourse().getSemester();
                }
            });
            this.getGrades().add(grade);
            ar.print(this.getNumber() + " took " + grade.getLetterGrade() + " " + grade.getCourse().getCode());
        });
    }

    public double getGPA() {
        double result = 0;
        AtomicInteger creditSum = new AtomicInteger(0);
        AtomicReference<Double> successSum = new AtomicReference<>((double) 0);

        this.getGrades().forEach(new Consumer<Grade>() {
            @Override
            public void accept(Grade grade) {
                creditSum.addAndGet(grade.getCourse().getCredit());
                successSum.updateAndGet(v -> v + grade.getCourse().getCredit() * grade.getLetterGrade().successRate());
            }
        });

        result = successSum.get() / creditSum.get();

        return Double.isNaN(result) ? 0 : result;
    }

    @Override
    public int compareTo(Student o) {
        return number.compareTo(o.number);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return number.equals(student.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(number);
    }
}
