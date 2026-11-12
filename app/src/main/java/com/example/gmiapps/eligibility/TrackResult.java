package com.example.gmiapps.eligibility;

import java.util.Collections;
import java.util.List;

/** Result of checking one {@link ProgrammeTrack}. */
public final class TrackResult {

    private final ProgrammeTrack track;
    private final EligibilityStatus status;
    private final List<UnmetRequirement> unmet;
    private final Condition condition;

    private TrackResult(ProgrammeTrack track, EligibilityStatus status, List<UnmetRequirement> unmet,
                        Condition condition) {
        this.track = track;
        this.status = status;
        this.unmet = Collections.unmodifiableList(unmet);
        this.condition = condition;
    }

    static TrackResult eligible(ProgrammeTrack track, Condition condition) {
        return new TrackResult(track, EligibilityStatus.ELIGIBLE,
                Collections.<UnmetRequirement>emptyList(), condition);
    }

    static TrackResult routeNotListed(ProgrammeTrack track) {
        return new TrackResult(track, EligibilityStatus.ROUTE_NOT_LISTED,
                Collections.<UnmetRequirement>emptyList(), Condition.NONE);
    }

    static TrackResult notEligible(ProgrammeTrack track, List<UnmetRequirement> unmet) {
        return new TrackResult(track, EligibilityStatus.NOT_ELIGIBLE, unmet, Condition.NONE);
    }

    public ProgrammeTrack getTrack() {
        return track;
    }

    public EligibilityStatus getStatus() {
        return status;
    }

    /** Requirements that were not met (only for {@link EligibilityStatus#NOT_ELIGIBLE}). */
    public List<UnmetRequirement> getUnmet() {
        return unmet;
    }

    /** A condition attached to the admission (only for matches), or {@link Condition#NONE}. */
    public Condition getCondition() {
        return condition;
    }

    /** True if the student meets the requirements. */
    public boolean isMatch() {
        return status == EligibilityStatus.ELIGIBLE;
    }
}
