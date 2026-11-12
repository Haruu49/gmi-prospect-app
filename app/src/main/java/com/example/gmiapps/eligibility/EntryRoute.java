package com.example.gmiapps.eligibility;

import androidx.annotation.StringRes;

import com.example.gmiapps.R;

/** The qualification a student uses to apply. */
public enum EntryRoute {
    SPM(R.string.route_spm, GradeScale.SPM),
    O_LEVEL(R.string.route_olevel, GradeScale.O_LEVEL),
    UEC(R.string.route_uec, GradeScale.UEC),
    /** STPM, SKM, SVM, Matriculation, certificates, APEL... (not assessed automatically). */
    OTHER(R.string.route_other, null);

    @StringRes
    private final int labelRes;
    private final GradeScale scale;

    EntryRoute(@StringRes int labelRes, GradeScale scale) {
        this.labelRes = labelRes;
        this.scale = scale;
    }

    @StringRes
    public int getLabelRes() {
        return labelRes;
    }

    /** Grade scale for this route, or null for {@link #OTHER}. */
    public GradeScale getScale() {
        return scale;
    }

    public boolean supportsGradeEntry() {
        return scale != null;
    }
}
