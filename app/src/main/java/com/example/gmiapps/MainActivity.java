package com.example.gmiapps;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.splashscreen.SplashScreen;
import androidx.core.splashscreen.SplashScreenViewProvider;

import com.example.gmiapps.data.Programme;
import com.example.gmiapps.data.ProgrammeRepository;
import com.example.gmiapps.util.AnimUtils;
import com.example.gmiapps.util.IntentUtils;

/**
 * Home screen: welcome banner, GMI introduction, highlights, programme cards,
 * shortcuts to the eligibility checker and About &amp; Contact, and quick contact rows.
 */
public class MainActivity extends BaseActivity {

    /** How long the splash screen stays visible on a cold start. */
    private static final long SPLASH_DURATION_MS = 900;

    private boolean keepSplashOnScreen = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // The splash screen must be installed before super.onCreate().
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            splashScreen.setKeepOnScreenCondition(() -> keepSplashOnScreen);
            splashScreen.setOnExitAnimationListener(this::animateSplashExit);
            new Handler(Looper.getMainLooper()).postDelayed(
                    () -> keepSplashOnScreen = false, SPLASH_DURATION_MS);
        }

        enableEdgeToEdgeDisplay();
        setContentView(R.layout.activity_main);
        applySystemBarInsets(findViewById(R.id.root), findViewById(R.id.hero), findViewById(R.id.content),
                findViewById(R.id.status_bar_scrim));

        findViewById(R.id.button_explore).setOnClickListener(v -> openProgrammes());
        findViewById(R.id.button_see_all).setOnClickListener(v -> openProgrammes());
        findViewById(R.id.button_check).setOnClickListener(v -> openEligibility());
        findViewById(R.id.card_eligibility).setOnClickListener(v -> openEligibility());
        findViewById(R.id.card_about).setOnClickListener(v ->
                startActivity(new Intent(this, AboutContactActivity.class)));

        showProgrammeCards();
        setupContactRows();
    }

    /**
     * Splash exit: the splash (with the GMI logo) zooms in slightly and fades out, while
     * the Home content animates in underneath at the same time.
     */
    private void animateSplashExit(SplashScreenViewProvider splash) {
        playEntranceAnimation();
        if (!AnimUtils.enabled()) {
            splash.remove();
            return;
        }
        // Animate the whole splash view; getIconView() can be missing on some launches.
        splash.getView().animate()
                .alpha(0f)
                .scaleX(1.15f)
                .scaleY(1.15f)
                .setDuration(400)
                .withEndAction(splash::remove)
                .start();
    }

    /** Home entrance: banner items slide up one by one, then the cards below follow. */
    private void playEntranceAnimation() {
        AnimUtils.revealChildren(findViewById(R.id.hero), 0, 100, 70);
        AnimUtils.growFromStart(findViewById(R.id.hero_accent), 350);
        // Index 0 is the banner itself, so start from the welcome card (index 1).
        AnimUtils.revealChildren(findViewById(R.id.content), 1, 450, 60);
    }

    /** Adds one compact card per programme from the repository. */
    private void showProgrammeCards() {
        ViewGroup container = findViewById(R.id.programme_container);
        LayoutInflater inflater = LayoutInflater.from(this);
        for (Programme programme : ProgrammeRepository.getAll()) {
            View card = inflater.inflate(R.layout.item_programme_compact, container, false);
            ((ImageView) card.findViewById(R.id.programme_icon)).setImageResource(programme.getIconRes());
            ((TextView) card.findViewById(R.id.programme_short)).setText(programme.getShortNameRes());
            ((TextView) card.findViewById(R.id.programme_name)).setText(programme.getNameRes());
            ((TextView) card.findViewById(R.id.programme_tagline)).setText(programme.getTaglineRes());
            card.setOnClickListener(v -> startActivity(
                    ProgrammeDetailActivity.newIntent(this, programme.getId())));
            container.addView(card);
        }
    }

    private void setupContactRows() {
        setupInfoRow(R.id.home_row_phone, R.drawable.ic_call,
                getString(R.string.contact_phone_label), getString(R.string.contact_phone_value),
                v -> IntentUtils.dial(this, getString(R.string.phone_main_dial)));
        setupInfoRow(R.id.home_row_email, R.drawable.ic_email,
                getString(R.string.contact_email_label), getString(R.string.contact_email_value),
                v -> IntentUtils.email(this, getString(R.string.email_address), getString(R.string.email_subject)));
        setupInfoRow(R.id.home_row_address, R.drawable.ic_place,
                getString(R.string.contact_address_label), getString(R.string.contact_address_value),
                v -> IntentUtils.openMap(this, getString(R.string.map_query)));
    }

    private void openProgrammes() {
        startActivity(new Intent(this, ProgrammesActivity.class));
    }

    private void openEligibility() {
        startActivity(new Intent(this, EligibilityActivity.class));
    }
}
