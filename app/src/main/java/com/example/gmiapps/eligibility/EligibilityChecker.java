package com.example.gmiapps.eligibility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Compares a student's grades with GMI's published minimum entry requirements.
 *
 * <p>Sources (checked 5 November 2026, GAPP and diploma details re-confirmed 10 November 2026):
 * <ul>
 *   <li>Diploma: GMI Programme Guide May 2026 (rev 02), pp. 36-40, and the
 *       Diploma Entry Requirement PDF on gmi.edu.my/diploma-programme-admission/</li>
 *   <li>GAPP: gmi.edu.my/a-level-programme-admission/ and Programme Guide May 2026, p. 45</li>
 *   <li>GUFP: gmi.edu.my/foundation-programme-admission/ and Programme Guide May 2026, p. 50</li>
 * </ul>
 *
 * <p>Only the school-leaver routes (SPM, O-Level/IGCSE, UEC) are checked automatically.
 * Where GMI's wording is unclear, the checker reads it in the student's favour: A- counts
 * as an "A" grade for GAPP (YTP MARA), and Additional Mathematics counts as a
 * science/technical subject for the diplomas.
 * A match can also carry a {@link Condition} that GMI attaches to the admission.
 */
public class EligibilityChecker {

    /** The 4 named subjects of the GAPP (YTP MARA) "8As" requirement. */
    private static final Set<Subject> GAPP_YTP_NAMED = EnumSet.of(
            Subject.ENGLISH, Subject.ADDITIONAL_MATHEMATICS, Subject.PHYSICS, Subject.CHEMISTRY);

    /** The 5 named subjects of the GAPP self-sponsored requirement (all routes). */
    private static final Set<Subject> GAPP_SELF_NAMED = EnumSet.of(Subject.ENGLISH, Subject.MATHEMATICS,
            Subject.ADDITIONAL_MATHEMATICS, Subject.PHYSICS, Subject.CHEMISTRY);

    /** The 4 named subjects of the GUFP IGCSE/O-Level requirement. */
    private static final Set<Subject> GUFP_OLEVEL_NAMED = EnumSet.of(
            Subject.MATHEMATICS, Subject.PHYSICS, Subject.CHEMISTRY, Subject.ADDITIONAL_MATHEMATICS);

    /** Checks every programme track and returns one result per track. */
    public List<TrackResult> check(EntryRoute route, Map<Subject, String> grades) {
        List<TrackResult> results = new ArrayList<>();
        for (ProgrammeTrack track : ProgrammeTrack.values()) {
            results.add(checkTrack(track, route, grades));
        }
        return results;
    }

    /** Checks one programme track. */
    public TrackResult checkTrack(ProgrammeTrack track, EntryRoute route, Map<Subject, String> grades) {
        if (!isRouteListed(track, route)) {
            return TrackResult.routeNotListed(track);
        }
        Map<Subject, String> safeGrades = grades == null
                ? Collections.<Subject, String>emptyMap() : new EnumMap<>(grades);

        Rules rules = evaluate(track, route, safeGrades);
        if (rules.unmet.isEmpty()) {
            return TrackResult.eligible(track, rules.condition);
        }
        return TrackResult.notEligible(track, rules.unmet);
    }

    /** Which routes GMI lists for each track (SPM, O-Level, UEC). */
    static boolean isRouteListed(ProgrammeTrack track, EntryRoute route) {
        switch (track) {
            case DIPLOMA_MQA_ENGINEERING:
            case DIPLOMA_MBOT:
                return route == EntryRoute.SPM || route == EntryRoute.O_LEVEL || route == EntryRoute.UEC;
            case GAPP_YTP_MARA:
                return route == EntryRoute.SPM;
            case DIPLOMA_BEM:
            case DIPLOMA_COMPUTING:
            case DIPLOMA_CREATIVE_MULTIMEDIA:
            case GAPP_SELF_SPONSORED:
            case GUFP:
            default:
                return route == EntryRoute.SPM || route == EntryRoute.O_LEVEL;
        }
    }

    /**
     * Applies the published rules for one track.
     *
     * @return the unmet requirements (empty list = all met) and any admission condition
     */
    private Rules evaluate(ProgrammeTrack track, EntryRoute route, Map<Subject, String> grades) {
        GradeScale scale = route.getScale();
        Rules rules = new Rules(scale, grades);
        // "Credit" for SPM and "Grade C" for O-Level are both C; GMI's "Grade B" for UEC is B6.
        String credit = scale.getCreditGrade();

        switch (track) {
            case DIPLOMA_BEM:
                // BEM Standard (ETAC), Guide p. 39
                // SPM: at least 3 credits incl. Mathematics + 1 Science/Technical + 1 other subject.
                // O-Level: Grade C in the same pattern AND pass in English.
                rules.mathsScienceOther(credit);
                if (route == EntryRoute.O_LEVEL) {
                    rules.pass(Subject.ENGLISH);
                }
                break;

            case DIPLOMA_MQA_ENGINEERING:
                // MQA Standard: Engineering and Engineering Technology (Precision Tooling,
                // Innovative Product Design), Guide p. 36
                // SPM credit / O-Level C / UEC B in Mathematics + 1 Sci/Tech/Voc + 1 other AND pass in English.
                rules.mathsScienceOther(credit);
                rules.pass(Subject.ENGLISH);
                break;

            case DIPLOMA_COMPUTING:
                // MQA Standard: Computing (Software Engineering), Guide p. 37
                // SPM / O-Level: at least 3 credits in any subjects, inclusive of Mathematics.
                rules.minSubjects(3, credit);
                if (route == EntryRoute.SPM) {
                    // Without SPM Mathematics the student can still be admitted, but must pass
                    // a reinforcement Mathematics subject in semester 1 (or before enrolment).
                    rules.minGradeOrCondition(Subject.MATHEMATICS, credit, Condition.REINFORCEMENT_MATHS);
                } else {
                    rules.minGrade(Subject.MATHEMATICS, credit);
                }
                break;

            case DIPLOMA_CREATIVE_MULTIMEDIA:
                // MQA Standard: Creative Multimedia Technology, Guide p. 38
                if (route == EntryRoute.SPM) {
                    // SPM: at least 3 credits.
                    rules.minSubjects(3, credit);
                } else {
                    // O-Level: Grade C in Mathematics + 1 Sci/Tech/Voc + 1 other AND pass in English.
                    rules.mathsScienceOther(credit);
                    rules.pass(Subject.ENGLISH);
                }
                break;

            case DIPLOMA_MBOT:
                // MBOT Standard (Cyber Security Technology, Industrial Design,
                // Machine Tools Maintenance), Guide p. 40 and Diploma Entry Requirement PDF
                // SPM: 3 credits in any subjects; O-Level: Grade C in 3 subjects; UEC: Grade B in 3 subjects.
                rules.minSubjects(3, credit);
                break;

            case GAPP_YTP_MARA:
                // YTP MARA: SPM with "A" for English, Additional Mathematics, Physics,
                // Chemistry and 4 other subjects (8As). GAPP admission page; Guide p. 45.
                // GMI does not say whether A- counts as an "A"; the checker counts A-, A and A+.
                String gradeA = "A-";
                for (Subject subject : GAPP_YTP_NAMED) {
                    rules.minGrade(subject, gradeA);
                }
                rules.otherSubjects(4, gradeA, GAPP_YTP_NAMED);
                break;

            case GAPP_SELF_SPONSORED:
                // Private / self-sponsored: "C" in English, Mathematics, Additional Mathematics,
                // Physics and Chemistry, plus Bahasa Melayu (SPM) or 1 other subject (IGCSE/O-Level).
                // GAPP admission page; Guide p. 45.
                for (Subject subject : GAPP_SELF_NAMED) {
                    rules.minGrade(subject, credit);
                }
                if (route == EntryRoute.SPM) {
                    rules.minGrade(Subject.BAHASA_MELAYU, credit);
                } else {
                    rules.otherSubjects(1, credit, GAPP_SELF_NAMED);
                }
                break;

            case GUFP:
                // GUFP admission requirement (foundation admission page; Guide p. 50).
                if (route == EntryRoute.SPM) {
                    // SPM: minimum Grade C in BM, Mathematics, English, Add Maths, Physics, Chemistry.
                    rules.minGrade(Subject.BAHASA_MELAYU, credit);
                    rules.minGrade(Subject.MATHEMATICS, credit);
                    rules.minGrade(Subject.ENGLISH, credit);
                    rules.minGrade(Subject.ADDITIONAL_MATHEMATICS, credit);
                    rules.minGrade(Subject.PHYSICS, credit);
                    rules.minGrade(Subject.CHEMISTRY, credit);
                } else {
                    // IGCSE/O-Level: Grade C in 5 subjects inclusive of Mathematics, Physics,
                    // Chemistry, Additional Mathematics and any other 1 subject.
                    for (Subject subject : GUFP_OLEVEL_NAMED) {
                        rules.minGrade(subject, credit);
                    }
                    rules.otherSubjects(1, credit, GUFP_OLEVEL_NAMED);
                }
                break;
        }
        return rules;
    }

    /** Small helper that records every requirement a student does not meet. */
    private static final class Rules {
        private final GradeScale scale;
        private final Map<Subject, String> grades;
        private final List<UnmetRequirement> unmet = new ArrayList<>();
        private Condition condition = Condition.NONE;

        Rules(GradeScale scale, Map<Subject, String> grades) {
            this.scale = scale;
            this.grades = grades;
        }

        private boolean meets(Subject subject, String minimum) {
            return scale.meets(grades.get(subject), minimum);
        }

        /** Number of subjects (not in {@code excluded}) at or above {@code minimum}. */
        private int count(String minimum, Set<Subject> excluded) {
            int total = 0;
            for (Subject subject : Subject.values()) {
                if (!excluded.contains(subject) && meets(subject, minimum)) {
                    total++;
                }
            }
            return total;
        }

        void minGrade(Subject subject, String minimum) {
            if (!meets(subject, minimum)) {
                unmet.add(UnmetRequirement.minGrade(subject, minimum));
            }
        }

        /** Like {@link #minGrade}, but a missing grade adds a condition instead of a failure. */
        void minGradeOrCondition(Subject subject, String minimum, Condition ifNotMet) {
            if (!meets(subject, minimum)) {
                condition = ifNotMet;
            }
        }

        void pass(Subject subject) {
            if (!meets(subject, scale.getPassGrade())) {
                unmet.add(UnmetRequirement.pass(subject));
            }
        }

        void minSubjects(int required, String minimum) {
            int actual = count(minimum, EnumSet.noneOf(Subject.class));
            if (actual < required) {
                unmet.add(UnmetRequirement.minSubjects(required, minimum, actual));
            }
        }

        void otherSubjects(int required, String minimum, Set<Subject> named) {
            int actual = count(minimum, named);
            if (actual < required) {
                unmet.add(UnmetRequirement.otherSubjects(required, minimum, actual));
            }
        }

        /**
         * "Mathematics + 1 Science/Technical subject + 1 other subject", each at or above
         * {@code minimum}. Needs Maths, at least one science/technical subject, and at
         * least two non-Maths subjects in total (the science subject plus one more).
         */
        void mathsScienceOther(String minimum) {
            boolean maths = meets(Subject.MATHEMATICS, minimum);
            int science = 0;
            int nonMaths = 0;
            for (Subject subject : Subject.values()) {
                if (subject == Subject.MATHEMATICS || !meets(subject, minimum)) {
                    continue;
                }
                nonMaths++;
                if (subject.isScienceTechnical()) {
                    science++;
                }
            }
            if (!maths) {
                unmet.add(UnmetRequirement.minGrade(Subject.MATHEMATICS, minimum));
            }
            if (science == 0) {
                unmet.add(UnmetRequirement.scienceTechnical(minimum));
            }
            if (nonMaths < 2) {
                unmet.add(UnmetRequirement.minSubjects(3, minimum, nonMaths + (maths ? 1 : 0)));
            }
        }
    }
}
