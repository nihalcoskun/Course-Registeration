package tr.edu.marmara;

import java.util.*;

public class Helper {

    public boolean isLegalElective(String type) {
        CourseType t;
        try {
            t = this.generateCT(type);
        } catch (Exception e) {
            return false;
        }
        return t != CourseType.CC;
    }

    public CourseType generateCT(String type) throws Exception {
        switch (type.toUpperCase()) {
            case "CC":
                return CourseType.CC;
            case "TE":
                return CourseType.TE;
            case "NTE":
                return CourseType.NTE;
            case "UE":
                return CourseType.UE;
            case "FTE":
                return CourseType.FTE;
        }
        throw new Exception("The type must be one of following; CC, TE, NTE, UE, FTE");
    }

    public LetterGrade generateLG(String grade) throws Exception {
        switch (grade.toUpperCase()) {
            case "AA":
                return LetterGrade.AA;
            case "BA":
                return LetterGrade.BA;
            case "BB":
                return LetterGrade.BB;
            case "CB":
                return LetterGrade.CB;
            case "CC":
                return LetterGrade.CC;
            case "DC":
                return LetterGrade.DC;
            case "DD":
                return LetterGrade.DD;
            case "FD":
                return LetterGrade.FD;
            case "FF":
                return LetterGrade.FF;
        }
        throw new Exception("The grade must be one of the following; AA, BA, BB, CB, CC, DC, DD, FD, FF.");
    }

    public LetterGrade getRandomFailGrade(Settings s) {
        List<LetterGrade> grades = new ArrayList<>();

        for (LetterGrade letterGrade: LetterGrade.values()) {
            if (s.success.smaller(letterGrade)) {
                grades.add(letterGrade);
            }
        }

        Collections.shuffle(grades);

        return grades.get(0);
    }

    public LetterGrade getRandomSuccessGrade(Settings s) {
        List<LetterGrade> grades = new ArrayList<>();

        for (LetterGrade letterGrade: LetterGrade.values()) {
            if (s.success.greaterOrEqual(letterGrade)) {
                grades.add(letterGrade);
            }
        }

        Collections.shuffle(grades);

        return grades.get(0);
    }
}
