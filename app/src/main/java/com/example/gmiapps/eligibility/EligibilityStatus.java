package com.example.gmiapps.eligibility;

/** Outcome of checking one programme track. Listed in the order shown on screen. */
public enum EligibilityStatus {
    /** All published minimum requirements are met. */
    ELIGIBLE,
    /** GMI does not list this entry route for the programme. */
    ROUTE_NOT_LISTED,
    /** At least one requirement is not met. */
    NOT_ELIGIBLE
}
