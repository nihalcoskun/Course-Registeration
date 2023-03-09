package tr.edu.marmara;

import java.util.Map;
import java.util.TreeMap;

public class Settings {
    public int maxQuota;
    public int maxCredits;
    public int numberLength;
    public int successWeight;
    public String mailPostfix;
    public LetterGrade success;
    public int maxLectureCountPerCourse;
    public Map<Integer, Map<CourseType, Integer>> electivePolicy = Map.of(1, new TreeMap<>(), 2, new TreeMap<>(), 3, new TreeMap<>(), 4, new TreeMap<>(), 5, new TreeMap<>(), 6, new TreeMap<>(), 7, new TreeMap<>(), 8, new TreeMap<>());
}
