package com.example.gmiapps.eligibility;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * An ordered list of grades for one examination, best grade first.
 * Used to answer questions such as "is B+ at least a C?".
 */
public final class GradeScale {

    /** SPM: a credit is A+ to C, a pass is A+ to E (G = fail). */
    public static final GradeScale SPM = new GradeScale(
            new String[]{"A+", "A", "A-", "B+", "B", "C+", "C", "D", "E", "G"}, "C", "E");

    /** Cambridge O-Level / IGCSE letter grades: credit is A* to C, U = ungraded. */
    public static final GradeScale O_LEVEL = new GradeScale(
            new String[]{"A*", "A", "B", "C", "D", "E", "F", "G", "U"}, "C", "G");

    /** UEC: GMI's "Grade B" is B6 or better; a pass is C8 or better (F9 = fail). */
    public static final GradeScale UEC = new GradeScale(
            new String[]{"A1", "A2", "B3", "B4", "B5", "B6", "C7", "C8", "F9"}, "B6", "C8");

    private final List<String> grades;
    private final String creditGrade;
    private final String passGrade;

    private GradeScale(String[] grades, String creditGrade, String passGrade) {
        this.grades = Collections.unmodifiableList(Arrays.asList(grades));
        this.creditGrade = creditGrade;
        this.passGrade = passGrade;
    }

    /** All grades, best first (used to fill the grade dropdowns). */
    public String[] getGrades() {
        return grades.toArray(new String[0]);
    }

    /** Lowest grade that counts as a credit (SPM/O-Level "C", UEC "B6"). */
    public String getCreditGrade() {
        return creditGrade;
    }

    /** Lowest grade that counts as a pass. */
    public String getPassGrade() {
        return passGrade;
    }

    public boolean isValid(String grade) {
        return grade != null && grades.contains(grade);
    }

    /**
     * Returns true if {@code grade} is equal to or better than {@code minimum}.
     * A missing or unknown grade (subject not taken) never meets a minimum.
     */
    public boolean meets(String grade, String minimum) {
        if (!isValid(grade) || !isValid(minimum)) {
            return false;
        }
        return grades.indexOf(grade) <= grades.indexOf(minimum);
    }
}
