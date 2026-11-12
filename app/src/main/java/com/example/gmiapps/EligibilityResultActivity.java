package com.example.gmiapps;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.example.gmiapps.eligibility.Condition;
import com.example.gmiapps.eligibility.EligibilityChecker;
import com.example.gmiapps.eligibility.EligibilityStatus;
import com.example.gmiapps.eligibility.EntryRoute;
import com.example.gmiapps.eligibility.Subject;
import com.example.gmiapps.eligibility.TrackResult;
import com.example.gmiapps.eligibility.UnmetRequirement;
import com.example.gmiapps.util.AnimUtils;
import com.example.gmiapps.util.IntentUtils;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Runs the {@link EligibilityChecker} and shows every matching programme. The
 * "Apply Online" card appears whenever at least one programme matches.
 */
public class EligibilityResultActivity extends BaseActivity {

    private static final String EXTRA_ROUTE = "com.example.gmiapps.extra.ROUTE";
    private static final String EXTRA_GRADES = "com.example.gmiapps.extra.GRADES";

    /**
     * @param grades one grade per {@link Subject} in declaration order; "" = not taken
     */
    public static Intent newIntent(Context context, EntryRoute route, String[] grades) {
        return new Intent(context, EligibilityResultActivity.class)
                .putExtra(EXTRA_ROUTE, route.name())
                .putExtra(EXTRA_GRADES, grades);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdgeDisplay();
        setContentView(R.layout.activity_eligibility_result);
        setupScreen(R.string.result_title);

        EntryRoute route = EntryRoute.valueOf(getIntent().getStringExtra(EXTRA_ROUTE));
        Map<Subject, String> grades = readGrades(getIntent().getStringArrayExtra(EXTRA_GRADES));
        List<TrackResult> results = new EligibilityChecker().check(route, grades);

        int matches = 0;
        for (TrackResult result : results) {
            if (result.isMatch()) {
                matches++;
            }
        }
        showSummary(route, matches);
        showResults(results);

        if (savedInstanceState == null) {
            AnimUtils.pop(findViewById(R.id.summary_icon), 150);
            AnimUtils.reveal(findViewById(R.id.card_apply), 250);
            AnimUtils.revealChildren(findViewById(R.id.results_container), 0, 350, 60);
            if (matches > 0) {
                AnimUtils.pulse(findViewById(R.id.button_apply), 900);
            }
        }

        findViewById(R.id.button_apply).setOnClickListener(v ->
                IntentUtils.openUrl(this, getString(R.string.url_apply_online)));
        findViewById(R.id.button_check_again).setOnClickListener(v -> finish());
        findViewById(R.id.button_contact).setOnClickListener(v ->
                startActivity(new Intent(this, AboutContactActivity.class)));
    }

    private static Map<Subject, String> readGrades(String[] values) {
        Map<Subject, String> grades = new EnumMap<>(Subject.class);
        Subject[] subjects = Subject.values();
        if (values != null) {
            for (int i = 0; i < subjects.length && i < values.length; i++) {
                if (!TextUtils.isEmpty(values[i])) {
                    grades.put(subjects[i], values[i]);
                }
            }
        }
        return grades;
    }

    private void showSummary(EntryRoute route, int matches) {
        TextView title = findViewById(R.id.summary_title);
        TextView body = findViewById(R.id.summary_body);
        ImageView icon = findViewById(R.id.summary_icon);
        ((TextView) findViewById(R.id.summary_route))
                .setText(getString(R.string.result_route, getString(route.getLabelRes())));

        if (matches > 0) {
            title.setText(getResources().getQuantityString(R.plurals.result_summary_count, matches, matches));
            body.setText(R.string.result_summary_body);
            icon.setImageResource(R.drawable.ic_check_circle);
            // Requirement: show "Apply Online" whenever an eligible programme is found.
            findViewById(R.id.card_apply).setVisibility(View.VISIBLE);
        } else {
            title.setText(R.string.result_summary_none);
            body.setText(R.string.result_summary_none_body);
            icon.setImageResource(R.drawable.ic_info);
            findViewById(R.id.card_apply).setVisibility(View.GONE);
        }
    }

    /** Groups the results by status and adds a section header plus cards for each group. */
    private void showResults(List<TrackResult> results) {
        ViewGroup container = findViewById(R.id.results_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (EligibilityStatus status : EligibilityStatus.values()) {
            List<TrackResult> group = new ArrayList<>();
            for (TrackResult result : results) {
                if (result.getStatus() == status) {
                    group.add(result);
                }
            }
            if (group.isEmpty()) {
                continue;
            }
            TextView header = (TextView) inflater.inflate(R.layout.item_section_header, container, false);
            header.setText(sectionTitleFor(status));
            container.addView(header);
            for (TrackResult result : group) {
                container.addView(createResultCard(inflater, container, result));
            }
        }
    }

    private View createResultCard(LayoutInflater inflater, ViewGroup parent, TrackResult result) {
        View card = inflater.inflate(R.layout.item_result, parent, false);
        TextView status = card.findViewById(R.id.result_status);
        ((TextView) card.findViewById(R.id.result_title)).setText(result.getTrack().getTitleRes());
        ((TextView) card.findViewById(R.id.result_subtitle)).setText(result.getTrack().getSubtitleRes());
        ((TextView) card.findViewById(R.id.result_details)).setText(detailsFor(result));

        switch (result.getStatus()) {
            case ELIGIBLE:
                styleStatus(status, R.string.status_eligible, R.color.status_eligible_bg, R.color.status_eligible_text);
                break;
            case ROUTE_NOT_LISTED:
                styleStatus(status, R.string.status_unlisted, R.color.status_unlisted_bg, R.color.status_unlisted_text);
                break;
            case NOT_ELIGIBLE:
            default:
                styleStatus(status, R.string.status_not_eligible, R.color.status_not_bg, R.color.status_not_text);
                break;
        }

        String programmeId = result.getTrack().getProgrammeId();
        card.findViewById(R.id.result_view_programme).setOnClickListener(v ->
                startActivity(ProgrammeDetailActivity.newIntent(this, programmeId)));
        return card;
    }

    /** Builds the bullet text shown on a card: notes for matches, reasons otherwise. */
    private String detailsFor(TrackResult result) {
        List<String> lines = new ArrayList<>();
        switch (result.getStatus()) {
            case ELIGIBLE:
                if (result.getCondition() != Condition.NONE) {
                    lines.add(getString(result.getCondition().getMessageRes()));
                }
                if (result.getTrack().getNoteRes() != 0) {
                    lines.add(getString(result.getTrack().getNoteRes()));
                }
                break;
            case ROUTE_NOT_LISTED:
                lines.add(getString(R.string.result_unlisted_reason));
                break;
            case NOT_ELIGIBLE:
            default:
                for (UnmetRequirement unmet : result.getUnmet()) {
                    lines.add(describe(unmet));
                }
                break;
        }
        StringBuilder text = new StringBuilder();
        for (String line : lines) {
            if (text.length() > 0) {
                text.append('\n');
            }
            text.append("• ").append(line);
        }
        return text.toString();
    }

    /** Turns an unmet requirement into a sentence using string resources. */
    private String describe(UnmetRequirement unmet) {
        switch (unmet.getType()) {
            case MIN_GRADE:
                return getString(R.string.reason_min_grade, getString(unmet.getSubject().getLabelRes()), unmet.getGrade());
            case PASS:
                return getString(R.string.reason_pass, getString(unmet.getSubject().getLabelRes()));
            case MIN_SUBJECTS:
                return getResources().getQuantityString(R.plurals.reason_min_subjects, unmet.getRequired(),
                        unmet.getRequired(), unmet.getGrade(), unmet.getActual());
            case SCIENCE_TECHNICAL:
                return getString(R.string.reason_science_technical, unmet.getGrade());
            case OTHER_SUBJECTS:
            default:
                return getResources().getQuantityString(R.plurals.reason_other_subjects, unmet.getRequired(),
                        unmet.getRequired(), unmet.getGrade(), unmet.getActual());
        }
    }

    private void styleStatus(TextView view, @StringRes int textRes, @ColorRes int bgRes, @ColorRes int textColorRes) {
        view.setText(textRes);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, bgRes)));
        view.setTextColor(ContextCompat.getColor(this, textColorRes));
    }

    @StringRes
    private static int sectionTitleFor(EligibilityStatus status) {
        switch (status) {
            case ELIGIBLE:
                return R.string.result_section_eligible;
            case ROUTE_NOT_LISTED:
                return R.string.result_section_unlisted;
            case NOT_ELIGIBLE:
            default:
                return R.string.result_section_not;
        }
    }
}
