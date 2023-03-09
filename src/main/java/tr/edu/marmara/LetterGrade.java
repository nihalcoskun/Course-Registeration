package tr.edu.marmara;

public enum LetterGrade {
    AA("AA"),
    BA("BA"),
    BB("BB"),
    CB("CB"),
    CC("CC"),
    DC("DC"),
    DD("DD"),
    FD("FD"),
    FF("FF");

    private final String letter;

    LetterGrade(String letter) {
        this.letter = letter;
    }

    public boolean greaterOrEqual(LetterGrade grade) {
        return letter.compareTo(grade.letter) >= 0;
    }

    public boolean smaller(LetterGrade grade) {
        return !greaterOrEqual(grade);
    }

    public double successRate() {
        if (this == AA) return 4;
        if (this == BA) return 3.5;
        if (this == BB) return 3;
        if (this == CB) return 2.5;
        if (this == CC) return 2;
        if (this == DC) return 1.5;
        if (this == DD) return 1;
        if (this == FD) return 0.5;
        return 0;
    }

    @Override
    public String toString() {
        return letter;
    }
}
