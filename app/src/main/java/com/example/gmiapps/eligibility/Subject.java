package com.example.gmiapps.eligibility;

import androidx.annotation.StringRes;

import com.example.gmiapps.R;

/**
 * Subjects shown in the eligibility checker. The order here is the order of the rows
 * on screen.
 */
public enum Subject {
    BAHASA_MELAYU(R.string.subject_bahasa_melayu, false),
    ENGLISH(R.string.subject_english, false),
    MATHEMATICS(R.string.subject_mathematics, false),
    ADDITIONAL_MATHEMATICS(R.string.subject_add_maths, true),
    PHYSICS(R.string.subject_physics, true),
    CHEMISTRY(R.string.subject_chemistry, true),
    BIOLOGY(R.string.subject_biology, true),
    SCIENCE_TECHNICAL_OTHER(R.string.subject_science_technical, true),
    OTHER_1(R.string.subject_other_1, false),
    OTHER_2(R.string.subject_other_2, false),
    OTHER_3(R.string.subject_other_3, false);

    @StringRes
    private final int labelRes;
    private final boolean scienceTechnical;

    Subject(@StringRes int labelRes, boolean scienceTechnical) {
        this.labelRes = labelRes;
        this.scienceTechnical = scienceTechnical;
    }

    @StringRes
    public int getLabelRes() {
        return labelRes;
    }

    /**
     * True for subjects that count as "Science/Technical" in GMI's diploma requirements.
     * GMI does not say whether Additional Mathematics counts; the checker counts it.
     */
    public boolean isScienceTechnical() {
        return scienceTechnical;
    }
}
