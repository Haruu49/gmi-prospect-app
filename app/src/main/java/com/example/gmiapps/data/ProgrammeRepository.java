package com.example.gmiapps.data;

import com.example.gmiapps.R;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Provides the programmes shown in the app: GAPP, the 5 diploma groups (each group shares
 * one set of entry requirements) and GUFP. The data is static (from strings.xml), so the
 * app works offline and needs no database.
 */
public final class ProgrammeRepository {

    private static final List<Programme> PROGRAMMES = Collections.unmodifiableList(Arrays.asList(
            new Programme.Builder(Programme.ID_GAPP)
                    .icon(R.drawable.ic_flight)
                    .names(R.string.prog_gapp_short, R.string.prog_gapp_name, R.string.prog_gapp_tagline)
                    .summary(R.string.prog_gapp_summary)
                    .overview(R.string.prog_gapp_overview)
                    .keyFacts(R.string.prog_gapp_intake, R.string.prog_gapp_duration, R.string.prog_gapp_pathway)
                    .requirementSummary(R.string.prog_gapp_requirement_summary)
                    .offered(R.string.prog_gapp_offered)
                    .requirements(R.string.prog_gapp_requirements)
                    .sources(R.string.prog_gapp_sources)
                    .officialUrl(R.string.url_gapp_page)
                    .build(),
            new Programme.Builder(Programme.ID_DIPLOMA_MECHANICAL)
                    .icon(R.drawable.ic_build)
                    .names(R.string.prog_mech_short, R.string.prog_mech_name, R.string.prog_mech_tagline)
                    .summary(R.string.prog_mech_summary)
                    .overview(R.string.prog_mech_overview)
                    .keyFacts(R.string.prog_diploma_intake, R.string.prog_diploma_duration, R.string.prog_mech_pathway)
                    .requirementSummary(R.string.prog_mech_requirement_summary)
                    .offered(R.string.prog_mech_offered)
                    .requirements(R.string.prog_mech_requirements)
                    .sharedRequirements(R.string.prog_diploma_requirements_all)
                    .sources(R.string.prog_mech_sources)
                    .officialUrl(R.string.url_diploma_admission)
                    .build(),
            new Programme.Builder(Programme.ID_DIPLOMA_SOFTWARE)
                    .icon(R.drawable.ic_code)
                    .names(R.string.prog_software_short, R.string.prog_software_name, R.string.prog_software_tagline)
                    .summary(R.string.prog_software_summary)
                    .overview(R.string.prog_software_overview)
                    .keyFacts(R.string.prog_diploma_intake, R.string.prog_diploma_duration, R.string.prog_software_pathway)
                    .requirementSummary(R.string.prog_software_requirement_summary)
                    .offered(R.string.prog_software_offered)
                    .requirements(R.string.prog_software_requirements)
                    .sharedRequirements(R.string.prog_diploma_requirements_all)
                    .sources(R.string.prog_software_sources)
                    .officialUrl(R.string.url_diploma_admission)
                    .build(),
            new Programme.Builder(Programme.ID_DIPLOMA_MULTIMEDIA)
                    .icon(R.drawable.ic_palette)
                    .names(R.string.prog_multimedia_short, R.string.prog_multimedia_name, R.string.prog_multimedia_tagline)
                    .summary(R.string.prog_multimedia_summary)
                    .overview(R.string.prog_multimedia_overview)
                    .keyFacts(R.string.prog_diploma_intake, R.string.prog_diploma_duration, R.string.prog_multimedia_pathway)
                    .requirementSummary(R.string.prog_multimedia_requirement_summary)
                    .offered(R.string.prog_multimedia_offered)
                    .requirements(R.string.prog_multimedia_requirements)
                    .sharedRequirements(R.string.prog_diploma_requirements_all)
                    .sources(R.string.prog_multimedia_sources)
                    .officialUrl(R.string.url_diploma_admission)
                    .build(),
            new Programme.Builder(Programme.ID_DIPLOMA_BEM)
                    .icon(R.drawable.ic_memory)
                    .names(R.string.prog_bem_short, R.string.prog_bem_name, R.string.prog_bem_tagline)
                    .summary(R.string.prog_bem_summary)
                    .overview(R.string.prog_bem_overview)
                    .keyFacts(R.string.prog_diploma_intake, R.string.prog_diploma_duration, R.string.prog_bem_pathway)
                    .requirementSummary(R.string.prog_bem_requirement_summary)
                    .offered(R.string.prog_bem_offered)
                    .requirements(R.string.prog_bem_requirements)
                    .sharedRequirements(R.string.prog_diploma_requirements_all)
                    .sources(R.string.prog_bem_sources)
                    .officialUrl(R.string.url_diploma_admission)
                    .build(),
            new Programme.Builder(Programme.ID_DIPLOMA_MBOT)
                    .icon(R.drawable.ic_security)
                    .names(R.string.prog_mbot_short, R.string.prog_mbot_name, R.string.prog_mbot_tagline)
                    .summary(R.string.prog_mbot_summary)
                    .overview(R.string.prog_mbot_overview)
                    .keyFacts(R.string.prog_diploma_intake, R.string.prog_diploma_duration, R.string.prog_mbot_pathway)
                    .requirementSummary(R.string.prog_mbot_requirement_summary)
                    .offered(R.string.prog_mbot_offered)
                    .requirements(R.string.prog_mbot_requirements)
                    .sharedRequirements(R.string.prog_diploma_requirements_all)
                    .sources(R.string.prog_mbot_sources)
                    .officialUrl(R.string.url_diploma_admission)
                    .build(),
            new Programme.Builder(Programme.ID_GUFP)
                    .icon(R.drawable.ic_account_balance)
                    .names(R.string.prog_gufp_short, R.string.prog_gufp_name, R.string.prog_gufp_tagline)
                    .summary(R.string.prog_gufp_summary)
                    .overview(R.string.prog_gufp_overview)
                    .keyFacts(R.string.prog_gufp_intake, R.string.prog_gufp_duration, R.string.prog_gufp_pathway)
                    .requirementSummary(R.string.prog_gufp_requirement_summary)
                    .offered(R.string.prog_gufp_offered)
                    .requirements(R.string.prog_gufp_requirements)
                    .sources(R.string.prog_gufp_sources)
                    .officialUrl(R.string.url_gufp_page)
                    .build()
    ));

    private ProgrammeRepository() {
    }

    public static List<Programme> getAll() {
        return PROGRAMMES;
    }

    /** Returns the programme with the given id, or null if there is none. */
    public static Programme findById(String id) {
        for (Programme programme : PROGRAMMES) {
            if (programme.getId().equals(id)) {
                return programme;
            }
        }
        return null;
    }
}
