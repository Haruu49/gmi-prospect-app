package com.example.gmiapps.eligibility;

import androidx.annotation.StringRes;

import com.example.gmiapps.R;

/**
 * A condition GMI attaches to an admission. The student is eligible, but must also do
 * something after being accepted. It is shown as a note on the result card.
 */
public enum Condition {
    NONE(0),
    /** Software Engineering (SPM) without Mathematics: pass reinforcement Mathematics in semester 1. */
    REINFORCEMENT_MATHS(R.string.condition_reinforcement_maths);

    @StringRes
    private final int messageRes;

    Condition(@StringRes int messageRes) {
        this.messageRes = messageRes;
    }

    @StringRes
    public int getMessageRes() {
        return messageRes;
    }
}
