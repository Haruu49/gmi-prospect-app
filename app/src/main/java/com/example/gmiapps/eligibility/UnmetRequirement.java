package com.example.gmiapps.eligibility;

import androidx.annotation.NonNull;

import java.util.Objects;

/**
 * One requirement the student did not meet. It stores data only; the screen turns it
 * into text using string resources, so no user-facing text is hard-coded here.
 */
public final class UnmetRequirement {

    public enum Type {
        /** Need at least {grade} in {subject}. */
        MIN_GRADE,
        /** Need a pass in {subject}. */
        PASS,
        /** Need {grade} or better in at least {required} subjects. */
        MIN_SUBJECTS,
        /** Need {grade} or better in a science/technical subject. */
        SCIENCE_TECHNICAL,
        /** Need {grade} or better in {required} other subjects. */
        OTHER_SUBJECTS
    }

    private final Type type;
    private final Subject subject;
    private final String grade;
    private final int required;
    private final int actual;

    private UnmetRequirement(Type type, Subject subject, String grade, int required, int actual) {
        this.type = type;
        this.subject = subject;
        this.grade = grade;
        this.required = required;
        this.actual = actual;
    }

    public static UnmetRequirement minGrade(Subject subject, String grade) {
        return new UnmetRequirement(Type.MIN_GRADE, subject, grade, 0, 0);
    }

    public static UnmetRequirement pass(Subject subject) {
        return new UnmetRequirement(Type.PASS, subject, null, 0, 0);
    }

    public static UnmetRequirement minSubjects(int required, String grade, int actual) {
        return new UnmetRequirement(Type.MIN_SUBJECTS, null, grade, required, actual);
    }

    public static UnmetRequirement scienceTechnical(String grade) {
        return new UnmetRequirement(Type.SCIENCE_TECHNICAL, null, grade, 1, 0);
    }

    public static UnmetRequirement otherSubjects(int required, String grade, int actual) {
        return new UnmetRequirement(Type.OTHER_SUBJECTS, null, grade, required, actual);
    }

    public Type getType() {
        return type;
    }

    public Subject getSubject() {
        return subject;
    }

    public String getGrade() {
        return grade;
    }

    public int getRequired() {
        return required;
    }

    public int getActual() {
        return actual;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UnmetRequirement)) return false;
        UnmetRequirement that = (UnmetRequirement) o;
        return required == that.required && actual == that.actual && type == that.type
                && subject == that.subject && Objects.equals(grade, that.grade);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, subject, grade, required, actual);
    }

    @NonNull
    @Override
    public String toString() {
        return type + "(" + subject + ", " + grade + ", " + required + ", " + actual + ")";
    }
}
