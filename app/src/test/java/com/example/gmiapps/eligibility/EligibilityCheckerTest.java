package com.example.gmiapps.eligibility;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Unit tests for {@link EligibilityChecker}. Each test describes a student and checks
 * the result against GMI's published requirements.
 */
public class EligibilityCheckerTest {

    private final EligibilityChecker checker = new EligibilityChecker();

    /** Builds a grade map from pairs: subject, grade, subject, grade... */
    private static Map<Subject, String> grades(Object... pairs) {
        Map<Subject, String> map = new EnumMap<>(Subject.class);
        for (int i = 0; i < pairs.length; i += 2) {
            map.put((Subject) pairs[i], (String) pairs[i + 1]);
        }
        return map;
    }

    private TrackResult check(ProgrammeTrack track, EntryRoute route, Map<Subject, String> grades) {
        return checker.checkTrack(track, route, grades);
    }

    // ---------- GradeScale ----------

    @Test
    public void gradeScale_spmCreditAndPass() {
        assertTrue(GradeScale.SPM.meets("A+", "C"));
        assertTrue(GradeScale.SPM.meets("C", "C"));
        assertFalse(GradeScale.SPM.meets("D", "C"));
        assertTrue(GradeScale.SPM.meets("E", GradeScale.SPM.getPassGrade()));
        assertFalse(GradeScale.SPM.meets("G", GradeScale.SPM.getPassGrade()));
        assertFalse(GradeScale.SPM.meets(null, "C"));
        assertFalse(GradeScale.SPM.meets("A*", "C")); // not an SPM grade
    }

    @Test
    public void gradeScale_oLevelAndUec() {
        assertTrue(GradeScale.O_LEVEL.meets("A*", "C"));
        assertFalse(GradeScale.O_LEVEL.meets("D", "C"));
        assertTrue(GradeScale.O_LEVEL.meets("G", GradeScale.O_LEVEL.getPassGrade()));
        assertFalse(GradeScale.O_LEVEL.meets("U", GradeScale.O_LEVEL.getPassGrade()));
        assertTrue(GradeScale.UEC.meets("B6", "B6"));
        assertFalse(GradeScale.UEC.meets("C7", "B6"));
    }

    // ---------- Strong SPM science student ----------

    @Test
    public void spmStraightAStudent_matchesEveryTrack() {
        Map<Subject, String> g = grades(
                Subject.BAHASA_MELAYU, "A", Subject.ENGLISH, "A+", Subject.MATHEMATICS, "A+",
                Subject.ADDITIONAL_MATHEMATICS, "A", Subject.PHYSICS, "A", Subject.CHEMISTRY, "A+",
                Subject.BIOLOGY, "A", Subject.OTHER_1, "A");
        List<TrackResult> results = checker.check(EntryRoute.SPM, g);
        assertEquals(ProgrammeTrack.values().length, results.size());
        for (TrackResult r : results) {
            assertEquals(r.getTrack().name(), EligibilityStatus.ELIGIBLE, r.getStatus());
            assertEquals(r.getTrack().name(), Condition.NONE, r.getCondition());
        }
    }

    // ---------- Diploma ----------

    @Test
    public void diplomaBem_spmMathsPhysicsPlusOne_isEligible_evenWithWeakEnglish() {
        // BEM SPM route has no English requirement.
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "C", Subject.PHYSICS, "B",
                Subject.BAHASA_MELAYU, "C+", Subject.ENGLISH, "G");
        assertEquals(EligibilityStatus.ELIGIBLE, check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.SPM, g).getStatus());
        // MQA engineering needs a pass in English, so it fails.
        TrackResult mqa = check(ProgrammeTrack.DIPLOMA_MQA_ENGINEERING, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, mqa.getStatus());
        assertTrue(mqa.getUnmet().contains(UnmetRequirement.pass(Subject.ENGLISH)));
    }

    @Test
    public void diplomaBem_withoutScienceCredit_isNotEligible() {
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "B", Subject.BAHASA_MELAYU, "B",
                Subject.OTHER_1, "C", Subject.PHYSICS, "D");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.scienceTechnical("C")));
    }

    @Test
    public void diplomaBem_addMathsCountsAsScience_isEligible() {
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "B", Subject.ADDITIONAL_MATHEMATICS, "C",
                Subject.ENGLISH, "C");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.ELIGIBLE, r.getStatus());
        assertTrue(r.isMatch());
    }

    @Test
    public void diplomaBem_mathsAndScienceOnly_needsThirdSubject() {
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "A", Subject.PHYSICS, "A");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.minSubjects(3, "C", 2)));
    }

    @Test
    public void diplomaBem_oLevel_needsEnglishPass() {
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "C", Subject.CHEMISTRY, "B",
                Subject.OTHER_1, "C", Subject.ENGLISH, "U");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.O_LEVEL, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.pass(Subject.ENGLISH)));

        g.put(Subject.ENGLISH, "E");
        assertEquals(EligibilityStatus.ELIGIBLE, check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.O_LEVEL, g).getStatus());
    }

    @Test
    public void diplomaComputing_spmWithoutMaths_isEligibleWithReinforcementMaths() {
        Map<Subject, String> noMaths = grades(Subject.ENGLISH, "A", Subject.BAHASA_MELAYU, "A",
                Subject.OTHER_1, "B", Subject.MATHEMATICS, "D");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_COMPUTING, EntryRoute.SPM, noMaths);
        assertEquals(EligibilityStatus.ELIGIBLE, r.getStatus());
        assertEquals(Condition.REINFORCEMENT_MATHS, r.getCondition());

        // With a Maths credit there is no condition.
        noMaths.put(Subject.MATHEMATICS, "C");
        r = check(ProgrammeTrack.DIPLOMA_COMPUTING, EntryRoute.SPM, noMaths);
        assertEquals(EligibilityStatus.ELIGIBLE, r.getStatus());
        assertEquals(Condition.NONE, r.getCondition());
    }

    @Test
    public void diplomaComputing_stillNeedsThreeCredits() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "A", Subject.BAHASA_MELAYU, "B",
                Subject.MATHEMATICS, "E");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_COMPUTING, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertEquals(1, r.getUnmet().size());
        assertTrue(r.getUnmet().contains(UnmetRequirement.minSubjects(3, "C", 2)));
    }

    @Test
    public void diplomaComputing_oLevelNeedsMathsCredit() {
        Map<Subject, String> noMaths = grades(Subject.ENGLISH, "A", Subject.PHYSICS, "A",
                Subject.OTHER_1, "B", Subject.MATHEMATICS, "D");
        TrackResult r = check(ProgrammeTrack.DIPLOMA_COMPUTING, EntryRoute.O_LEVEL, noMaths);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.minGrade(Subject.MATHEMATICS, "C")));
    }

    @Test
    public void diplomaMultimediaAndMbot_spmNeedAnyThreeCredits() {
        Map<Subject, String> noMaths = grades(Subject.ENGLISH, "A", Subject.BAHASA_MELAYU, "A",
                Subject.OTHER_1, "B", Subject.MATHEMATICS, "D");
        // Creative Multimedia (SPM) only needs any 3 credits.
        assertEquals(EligibilityStatus.ELIGIBLE,
                check(ProgrammeTrack.DIPLOMA_CREATIVE_MULTIMEDIA, EntryRoute.SPM, noMaths).getStatus());
        // MBOT (SPM) only needs any 3 credits.
        assertEquals(EligibilityStatus.ELIGIBLE,
                check(ProgrammeTrack.DIPLOMA_MBOT, EntryRoute.SPM, noMaths).getStatus());
    }

    @Test
    public void uec_onlyMqaEngineeringAndMbotAreListed() {
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "B5", Subject.PHYSICS, "B6",
                Subject.ENGLISH, "C8", Subject.OTHER_1, "A2");
        assertEquals(EligibilityStatus.ELIGIBLE,
                check(ProgrammeTrack.DIPLOMA_MQA_ENGINEERING, EntryRoute.UEC, g).getStatus());
        assertEquals(EligibilityStatus.ELIGIBLE,
                check(ProgrammeTrack.DIPLOMA_MBOT, EntryRoute.UEC, g).getStatus());
        assertEquals(EligibilityStatus.ROUTE_NOT_LISTED,
                check(ProgrammeTrack.DIPLOMA_BEM, EntryRoute.UEC, g).getStatus());
        assertEquals(EligibilityStatus.ROUTE_NOT_LISTED,
                check(ProgrammeTrack.GUFP, EntryRoute.UEC, g).getStatus());
        assertEquals(EligibilityStatus.ROUTE_NOT_LISTED,
                check(ProgrammeTrack.GAPP_SELF_SPONSORED, EntryRoute.UEC, g).getStatus());
    }

    // ---------- GAPP ----------

    @Test
    public void gappYtp_eightAs_isEligible_andOnlyForSpm() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "A", Subject.ADDITIONAL_MATHEMATICS, "A+",
                Subject.PHYSICS, "A", Subject.CHEMISTRY, "A", Subject.BAHASA_MELAYU, "A",
                Subject.MATHEMATICS, "A+", Subject.BIOLOGY, "A", Subject.OTHER_1, "A");
        assertEquals(EligibilityStatus.ELIGIBLE, check(ProgrammeTrack.GAPP_YTP_MARA, EntryRoute.SPM, g).getStatus());
        assertEquals(EligibilityStatus.ROUTE_NOT_LISTED,
                check(ProgrammeTrack.GAPP_YTP_MARA, EntryRoute.O_LEVEL, g).getStatus());
    }

    @Test
    public void gappYtp_aMinusCountsAsA_isEligible() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "A", Subject.ADDITIONAL_MATHEMATICS, "A",
                Subject.PHYSICS, "A-", Subject.CHEMISTRY, "A", Subject.BAHASA_MELAYU, "A",
                Subject.MATHEMATICS, "A", Subject.BIOLOGY, "A-", Subject.OTHER_1, "A");
        TrackResult r = check(ProgrammeTrack.GAPP_YTP_MARA, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.ELIGIBLE, r.getStatus());
    }

    @Test
    public void gappYtp_tooFewOtherAs_isNotEligible() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "A", Subject.ADDITIONAL_MATHEMATICS, "A",
                Subject.PHYSICS, "A", Subject.CHEMISTRY, "A", Subject.BAHASA_MELAYU, "A",
                Subject.MATHEMATICS, "A", Subject.BIOLOGY, "B+");
        TrackResult r = check(ProgrammeTrack.GAPP_YTP_MARA, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.otherSubjects(4, "A-", 2)));
    }

    @Test
    public void gappSelfSponsored_spmNeedsSixCsIncludingBahasaMelayu() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "C", Subject.MATHEMATICS, "B",
                Subject.ADDITIONAL_MATHEMATICS, "C+", Subject.PHYSICS, "C", Subject.CHEMISTRY, "C");
        TrackResult r = check(ProgrammeTrack.GAPP_SELF_SPONSORED, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertEquals(1, r.getUnmet().size());
        assertEquals(UnmetRequirement.minGrade(Subject.BAHASA_MELAYU, "C"), r.getUnmet().get(0));

        g.put(Subject.BAHASA_MELAYU, "C");
        assertEquals(EligibilityStatus.ELIGIBLE,
                check(ProgrammeTrack.GAPP_SELF_SPONSORED, EntryRoute.SPM, g).getStatus());

        g.put(Subject.CHEMISTRY, "D");
        r = check(ProgrammeTrack.GAPP_SELF_SPONSORED, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertEquals(1, r.getUnmet().size());
        assertEquals(UnmetRequirement.minGrade(Subject.CHEMISTRY, "C"), r.getUnmet().get(0));
    }

    @Test
    public void gappSelfSponsored_oLevelNeedsOneOtherSubject() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "C", Subject.MATHEMATICS, "B",
                Subject.ADDITIONAL_MATHEMATICS, "C", Subject.PHYSICS, "A", Subject.CHEMISTRY, "C");
        TrackResult r = check(ProgrammeTrack.GAPP_SELF_SPONSORED, EntryRoute.O_LEVEL, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.otherSubjects(1, "C", 0)));

        // Bahasa Melayu is not required for O-Level, but counts as the other subject.
        g.put(Subject.BAHASA_MELAYU, "C");
        assertEquals(EligibilityStatus.ELIGIBLE,
                check(ProgrammeTrack.GAPP_SELF_SPONSORED, EntryRoute.O_LEVEL, g).getStatus());
    }

    // ---------- GUFP ----------

    @Test
    public void gufpSpm_needsBahasaMelayu() {
        Map<Subject, String> g = grades(Subject.ENGLISH, "B", Subject.MATHEMATICS, "A",
                Subject.ADDITIONAL_MATHEMATICS, "B", Subject.PHYSICS, "B", Subject.CHEMISTRY, "C");
        TrackResult r = check(ProgrammeTrack.GUFP, EntryRoute.SPM, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.minGrade(Subject.BAHASA_MELAYU, "C")));

        g.put(Subject.BAHASA_MELAYU, "C");
        assertEquals(EligibilityStatus.ELIGIBLE, check(ProgrammeTrack.GUFP, EntryRoute.SPM, g).getStatus());
    }

    @Test
    public void gufpOLevel_needsOneOtherSubjectAtC() {
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "B", Subject.PHYSICS, "C",
                Subject.CHEMISTRY, "C", Subject.ADDITIONAL_MATHEMATICS, "C", Subject.ENGLISH, "D");
        TrackResult r = check(ProgrammeTrack.GUFP, EntryRoute.O_LEVEL, g);
        assertEquals(EligibilityStatus.NOT_ELIGIBLE, r.getStatus());
        assertTrue(r.getUnmet().contains(UnmetRequirement.otherSubjects(1, "C", 0)));

        g.put(Subject.ENGLISH, "C");
        assertEquals(EligibilityStatus.ELIGIBLE, check(ProgrammeTrack.GUFP, EntryRoute.O_LEVEL, g).getStatus());
    }

    // ---------- Edge cases ----------

    @Test
    public void noGrades_matchesNothing() {
        for (TrackResult r : checker.check(EntryRoute.SPM, grades())) {
            assertFalse(r.getTrack().name(), r.isMatch());
        }
    }

    @Test
    public void otherRoute_isNeverAssessed() {
        for (TrackResult r : checker.check(EntryRoute.OTHER, grades(Subject.MATHEMATICS, "A"))) {
            assertEquals(EligibilityStatus.ROUTE_NOT_LISTED, r.getStatus());
        }
    }

    @Test
    public void gradesFromAnotherScale_areIgnored() {
        // An SPM grade typed into the O-Level route must not count.
        Map<Subject, String> g = grades(Subject.MATHEMATICS, "A+", Subject.PHYSICS, "A+", Subject.OTHER_1, "A+");
        assertEquals(EligibilityStatus.NOT_ELIGIBLE,
                check(ProgrammeTrack.DIPLOMA_MBOT, EntryRoute.O_LEVEL, g).getStatus());
    }
}
