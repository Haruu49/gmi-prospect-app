package com.example.gmiapps.data;

import androidx.annotation.DrawableRes;
import androidx.annotation.StringRes;

/**
 * One programme card: GAPP, one of the 5 diploma groups, or GUFP. All text is stored as
 * string resource ids so it stays in strings.xml.
 */
public final class Programme {

    public static final String ID_DIPLOMA_MECHANICAL = "diploma_mechanical";
    public static final String ID_DIPLOMA_SOFTWARE = "diploma_software";
    public static final String ID_DIPLOMA_MULTIMEDIA = "diploma_multimedia";
    public static final String ID_DIPLOMA_BEM = "diploma_bem";
    public static final String ID_DIPLOMA_MBOT = "diploma_mbot";
    public static final String ID_GAPP = "gapp";
    public static final String ID_GUFP = "gufp";

    private final String id;
    @DrawableRes private final int iconRes;
    @StringRes private final int shortNameRes;
    @StringRes private final int nameRes;
    @StringRes private final int taglineRes;
    @StringRes private final int summaryRes;
    @StringRes private final int overviewRes;
    @StringRes private final int intakeRes;
    @StringRes private final int durationRes;
    @StringRes private final int pathwayRes;
    @StringRes private final int requirementSummaryRes;
    @StringRes private final int offeredRes;
    @StringRes private final int requirementsRes;
    @StringRes private final int sharedRequirementsRes;
    @StringRes private final int sourcesRes;
    @StringRes private final int officialUrlRes;

    private Programme(Builder b) {
        id = b.id;
        iconRes = b.iconRes;
        shortNameRes = b.shortNameRes;
        nameRes = b.nameRes;
        taglineRes = b.taglineRes;
        summaryRes = b.summaryRes;
        overviewRes = b.overviewRes;
        intakeRes = b.intakeRes;
        durationRes = b.durationRes;
        pathwayRes = b.pathwayRes;
        requirementSummaryRes = b.requirementSummaryRes;
        offeredRes = b.offeredRes;
        requirementsRes = b.requirementsRes;
        sharedRequirementsRes = b.sharedRequirementsRes;
        sourcesRes = b.sourcesRes;
        officialUrlRes = b.officialUrlRes;
    }

    public String getId() { return id; }
    @DrawableRes public int getIconRes() { return iconRes; }
    @StringRes public int getShortNameRes() { return shortNameRes; }
    @StringRes public int getNameRes() { return nameRes; }
    @StringRes public int getTaglineRes() { return taglineRes; }
    @StringRes public int getSummaryRes() { return summaryRes; }
    @StringRes public int getOverviewRes() { return overviewRes; }
    @StringRes public int getIntakeRes() { return intakeRes; }
    @StringRes public int getDurationRes() { return durationRes; }
    @StringRes public int getPathwayRes() { return pathwayRes; }
    @StringRes public int getRequirementSummaryRes() { return requirementSummaryRes; }
    @StringRes public int getOfferedRes() { return offeredRes; }
    @StringRes public int getRequirementsRes() { return requirementsRes; }
    /** Requirements shared by several programmes (shown after the programme's own), or 0. */
    @StringRes public int getSharedRequirementsRes() { return sharedRequirementsRes; }
    @StringRes public int getSourcesRes() { return sourcesRes; }
    @StringRes public int getOfficialUrlRes() { return officialUrlRes; }

    /** Builder keeps the many resource ids readable in {@link ProgrammeRepository}. */
    public static final class Builder {
        private final String id;
        private int iconRes;
        private int shortNameRes;
        private int nameRes;
        private int taglineRes;
        private int summaryRes;
        private int overviewRes;
        private int intakeRes;
        private int durationRes;
        private int pathwayRes;
        private int requirementSummaryRes;
        private int offeredRes;
        private int requirementsRes;
        private int sharedRequirementsRes;
        private int sourcesRes;
        private int officialUrlRes;

        public Builder(String id) { this.id = id; }

        public Builder icon(@DrawableRes int res) { iconRes = res; return this; }
        public Builder names(@StringRes int shortName, @StringRes int name, @StringRes int tagline) {
            shortNameRes = shortName;
            nameRes = name;
            taglineRes = tagline;
            return this;
        }
        public Builder summary(@StringRes int res) { summaryRes = res; return this; }
        public Builder overview(@StringRes int res) { overviewRes = res; return this; }
        public Builder keyFacts(@StringRes int intake, @StringRes int duration, @StringRes int pathway) {
            intakeRes = intake;
            durationRes = duration;
            pathwayRes = pathway;
            return this;
        }
        public Builder requirementSummary(@StringRes int res) { requirementSummaryRes = res; return this; }
        public Builder offered(@StringRes int res) { offeredRes = res; return this; }
        public Builder requirements(@StringRes int res) { requirementsRes = res; return this; }
        public Builder sharedRequirements(@StringRes int res) { sharedRequirementsRes = res; return this; }
        public Builder sources(@StringRes int res) { sourcesRes = res; return this; }
        public Builder officialUrl(@StringRes int res) { officialUrlRes = res; return this; }

        public Programme build() { return new Programme(this); }
    }
}
