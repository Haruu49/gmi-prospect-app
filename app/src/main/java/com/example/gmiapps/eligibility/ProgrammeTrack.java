package com.example.gmiapps.eligibility;

import androidx.annotation.StringRes;

import com.example.gmiapps.R;
import com.example.gmiapps.data.Programme;

/**
 * A group of programmes that share the same entry requirements. GMI publishes diploma
 * requirements per standard (BEM, MQA, MBOT), and GAPP has two routes (YTP MARA and
 * self-sponsored), so the checker works with these tracks. Each diploma track has its own
 * programme card; both GAPP tracks share the GAPP card.
 */
public enum ProgrammeTrack {
    DIPLOMA_BEM(R.string.track_diploma_bem, R.string.track_diploma_bem_list,
            Programme.ID_DIPLOMA_BEM, R.string.note_engineering_medical),
    DIPLOMA_MQA_ENGINEERING(R.string.track_diploma_mqa_eng, R.string.track_diploma_mqa_eng_list,
            Programme.ID_DIPLOMA_MECHANICAL, R.string.note_engineering_medical),
    DIPLOMA_COMPUTING(R.string.track_diploma_computing, R.string.track_diploma_computing_list,
            Programme.ID_DIPLOMA_SOFTWARE, R.string.note_computing),
    DIPLOMA_CREATIVE_MULTIMEDIA(R.string.track_diploma_multimedia, R.string.track_diploma_multimedia_list,
            Programme.ID_DIPLOMA_MULTIMEDIA, 0),
    DIPLOMA_MBOT(R.string.track_diploma_mbot, R.string.track_diploma_mbot_list,
            Programme.ID_DIPLOMA_MBOT, R.string.note_engineering_medical),
    GAPP_YTP_MARA(R.string.track_gapp_ytp, R.string.track_gapp_ytp_list,
            Programme.ID_GAPP, R.string.note_gapp_ytp),
    GAPP_SELF_SPONSORED(R.string.track_gapp_self, R.string.track_gapp_self_list,
            Programme.ID_GAPP, R.string.note_gapp_self),
    GUFP(R.string.track_gufp, R.string.track_gufp_list,
            Programme.ID_GUFP, R.string.note_gufp);

    @StringRes private final int titleRes;
    @StringRes private final int subtitleRes;
    private final String programmeId;
    @StringRes private final int noteRes;

    ProgrammeTrack(@StringRes int titleRes, @StringRes int subtitleRes, String programmeId,
                   @StringRes int noteRes) {
        this.titleRes = titleRes;
        this.subtitleRes = subtitleRes;
        this.programmeId = programmeId;
        this.noteRes = noteRes;
    }

    @StringRes
    public int getTitleRes() {
        return titleRes;
    }

    @StringRes
    public int getSubtitleRes() {
        return subtitleRes;
    }

    /** Id of the programme card this track belongs to (for "View details"). */
    public String getProgrammeId() {
        return programmeId;
    }

    /** Extra condition shown on matching cards, or 0 if none. */
    @StringRes
    public int getNoteRes() {
        return noteRes;
    }
}
