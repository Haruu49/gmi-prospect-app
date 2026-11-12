package com.example.gmiapps;

import android.content.Intent;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.gmiapps.eligibility.EntryRoute;
import com.example.gmiapps.eligibility.Subject;
import com.example.gmiapps.util.AnimUtils;
import com.example.gmiapps.util.IntentUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;

import java.util.EnumMap;
import java.util.Map;

/**
 * Eligibility checker input screen. The student picks an entry route and a grade for
 * each subject; the results are calculated on {@link EligibilityResultActivity}.
 */
public class EligibilityActivity extends BaseActivity {

    private static final String STATE_ROUTE = "state_route";
    private static final String STATE_GRADES = "state_grades";

    private final Map<Subject, MaterialAutoCompleteTextView> gradeInputs = new EnumMap<>(Subject.class);
    private EntryRoute selectedRoute = EntryRoute.SPM;
    private String notTaken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdgeDisplay();
        setContentView(R.layout.activity_eligibility);
        setupScreen(R.string.eligibility_title);
        notTaken = getString(R.string.grade_not_taken);

        buildSubjectRows();

        String[] savedGrades = null;
        if (savedInstanceState != null) {
            selectedRoute = EntryRoute.valueOf(savedInstanceState.getString(STATE_ROUTE, EntryRoute.SPM.name()));
            savedGrades = savedInstanceState.getStringArray(STATE_GRADES);
        }
        showRoute(selectedRoute, savedGrades);

        RadioGroup routeGroup = findViewById(R.id.route_group);
        routeGroup.check(radioIdFor(selectedRoute));
        routeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            EntryRoute route = routeFor(checkedId);
            if (route != selectedRoute) {
                selectedRoute = route;
                // Animates the swap between the grade card and the "other route" card.
                TransitionManager.beginDelayedTransition(findViewById(R.id.content));
                showRoute(route, null);
            }
        });

        findViewById(R.id.button_check).setOnClickListener(v -> checkEligibility());
        findViewById(R.id.button_reset).setOnClickListener(v -> showRoute(selectedRoute, null));
        findViewById(R.id.button_other_requirements).setOnClickListener(v ->
                IntentUtils.openUrl(this, getString(R.string.url_diploma_admission)));
        findViewById(R.id.button_other_contact).setOnClickListener(v ->
                startActivity(new Intent(this, AboutContactActivity.class)));

        if (savedInstanceState == null) {
            AnimUtils.revealChildren(findViewById(R.id.content), 0, 100, 70);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_ROUTE, selectedRoute.name());
        outState.putStringArray(STATE_GRADES, readGradesAsArray());
    }

    /** Creates one "subject + grade dropdown" row for every {@link Subject}. */
    private void buildSubjectRows() {
        ViewGroup container = findViewById(R.id.subject_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Subject subject : Subject.values()) {
            View row = inflater.inflate(R.layout.item_subject_grade, container, false);
            TextView label = row.findViewById(R.id.subject_label);
            MaterialAutoCompleteTextView input = row.findViewById(R.id.grade_input);
            label.setText(subject.getLabelRes());
            // Unique ids so the label is announced for its dropdown by screen readers.
            input.setId(View.generateViewId());
            label.setLabelFor(input.getId());
            input.setSaveEnabled(false); // grades are saved manually in onSaveInstanceState
            gradeInputs.put(subject, input);
            container.addView(row);
        }
    }

    /**
     * Updates the screen for a route: grade lists, help note, and whether the grade
     * card or the "other route" card is shown.
     *
     * @param restoredGrades grades to restore after rotation, or null to reset
     */
    private void showRoute(EntryRoute route, String[] restoredGrades) {
        boolean gradesSupported = route.supportsGradeEntry();
        findViewById(R.id.card_grades).setVisibility(gradesSupported ? View.VISIBLE : View.GONE);
        findViewById(R.id.action_row).setVisibility(gradesSupported ? View.VISIBLE : View.GONE);
        findViewById(R.id.card_other).setVisibility(gradesSupported ? View.GONE : View.VISIBLE);
        if (!gradesSupported) {
            return;
        }

        ((TextView) findViewById(R.id.route_note)).setText(noteFor(route));

        String[] scaleGrades = route.getScale().getGrades();
        String[] options = new String[scaleGrades.length + 1];
        options[0] = notTaken;
        System.arraycopy(scaleGrades, 0, options, 1, scaleGrades.length);

        Subject[] subjects = Subject.values();
        for (int i = 0; i < subjects.length; i++) {
            MaterialAutoCompleteTextView input = gradeInputs.get(subjects[i]);
            input.setSimpleItems(options);
            String value = notTaken;
            if (restoredGrades != null && i < restoredGrades.length
                    && route.getScale().isValid(restoredGrades[i])) {
                value = restoredGrades[i];
            }
            input.setText(value, false);
        }
    }

    /** Reads the dropdowns. Missing grades are stored as empty strings. */
    private String[] readGradesAsArray() {
        Subject[] subjects = Subject.values();
        String[] grades = new String[subjects.length];
        for (int i = 0; i < subjects.length; i++) {
            String value = gradeInputs.get(subjects[i]).getText().toString();
            grades[i] = value.equals(notTaken) ? "" : value;
        }
        return grades;
    }

    private void checkEligibility() {
        String[] grades = readGradesAsArray();
        boolean anyGrade = false;
        for (String grade : grades) {
            if (!grade.isEmpty()) {
                anyGrade = true;
                break;
            }
        }
        if (!anyGrade) {
            AnimUtils.shake(findViewById(R.id.button_check));
            Snackbar.make(findViewById(R.id.root), R.string.eligibility_error_no_grades, Snackbar.LENGTH_LONG).show();
            return;
        }
        startActivity(EligibilityResultActivity.newIntent(this, selectedRoute, grades));
    }

    private int noteFor(EntryRoute route) {
        switch (route) {
            case O_LEVEL:
                return R.string.eligibility_note_olevel;
            case UEC:
                return R.string.eligibility_note_uec;
            case SPM:
            default:
                return R.string.eligibility_note_spm;
        }
    }

    private static EntryRoute routeFor(int radioId) {
        if (radioId == R.id.route_olevel) return EntryRoute.O_LEVEL;
        if (radioId == R.id.route_uec) return EntryRoute.UEC;
        if (radioId == R.id.route_other) return EntryRoute.OTHER;
        return EntryRoute.SPM;
    }

    private static int radioIdFor(EntryRoute route) {
        switch (route) {
            case O_LEVEL:
                return R.id.route_olevel;
            case UEC:
                return R.id.route_uec;
            case OTHER:
                return R.id.route_other;
            case SPM:
            default:
                return R.id.route_spm;
        }
    }
}
