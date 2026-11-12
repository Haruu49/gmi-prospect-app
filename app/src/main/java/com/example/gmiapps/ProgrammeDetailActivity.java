package com.example.gmiapps;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.gmiapps.data.Programme;
import com.example.gmiapps.data.ProgrammeRepository;
import com.example.gmiapps.util.AnimUtils;
import com.example.gmiapps.util.IntentUtils;

/**
 * Shows one programme: overview, key facts, study content, entry requirements
 * and the official sources used.
 */
public class ProgrammeDetailActivity extends BaseActivity {

    private static final String EXTRA_PROGRAMME_ID = "com.example.gmiapps.extra.PROGRAMME_ID";

    /** Creates an intent that opens the given programme. */
    public static Intent newIntent(Context context, String programmeId) {
        return new Intent(context, ProgrammeDetailActivity.class)
                .putExtra(EXTRA_PROGRAMME_ID, programmeId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Programme programme = ProgrammeRepository.findById(getIntent().getStringExtra(EXTRA_PROGRAMME_ID));
        if (programme == null) {
            finish();
            return;
        }

        enableEdgeToEdgeDisplay();
        setContentView(R.layout.activity_programme_detail);
        setupScreen(R.string.detail_title);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(programme.getShortNameRes());
        }

        ((ImageView) findViewById(R.id.detail_icon)).setImageResource(programme.getIconRes());
        setText(R.id.detail_short, programme.getShortNameRes());
        setText(R.id.detail_name, programme.getNameRes());
        setText(R.id.detail_tagline, programme.getTaglineRes());
        setText(R.id.detail_overview, programme.getOverviewRes());
        setText(R.id.detail_offered, programme.getOfferedRes());
        CharSequence requirements = getText(programme.getRequirementsRes());
        if (programme.getSharedRequirementsRes() != 0) {
            requirements = TextUtils.concat(requirements, "\n\n", getText(programme.getSharedRequirementsRes()));
        }
        ((TextView) findViewById(R.id.detail_requirements)).setText(requirements);
        setText(R.id.detail_sources, programme.getSourcesRes());

        setupInfoRow(R.id.fact_intake, R.drawable.ic_event,
                getString(R.string.detail_intake), getText(programme.getIntakeRes()), null);
        setupInfoRow(R.id.fact_duration, R.drawable.ic_schedule,
                getString(R.string.detail_duration), getText(programme.getDurationRes()), null);
        setupInfoRow(R.id.fact_pathway, R.drawable.ic_trending_up,
                getString(R.string.detail_pathway), getText(programme.getPathwayRes()), null);

        findViewById(R.id.button_check).setOnClickListener(v ->
                startActivity(new Intent(this, EligibilityActivity.class)));
        findViewById(R.id.button_official).setOnClickListener(v ->
                IntentUtils.openUrl(this, getString(programme.getOfficialUrlRes())));

        if (savedInstanceState == null) {
            AnimUtils.pop(findViewById(R.id.detail_icon), 150);
            AnimUtils.revealChildren(findViewById(R.id.detail_header), 1, 200, 60);
            AnimUtils.revealChildren(findViewById(R.id.detail_sections), 0, 350, 70);
        }
    }

    /** setText(int) keeps the bold styling written in strings.xml. */
    private void setText(int viewId, int stringRes) {
        ((TextView) findViewById(viewId)).setText(stringRes);
    }
}
