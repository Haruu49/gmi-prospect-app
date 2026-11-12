package com.example.gmiapps;

import android.os.Bundle;

import androidx.annotation.StringRes;

import com.example.gmiapps.util.AnimUtils;
import com.example.gmiapps.util.IntentUtils;

/**
 * About &amp; Contact: GMI introduction, vision and mission, official contact details,
 * enquiry channels, location map and social media links.
 */
public class AboutContactActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdgeDisplay();
        setContentView(R.layout.activity_about_contact);
        setupScreen(R.string.about_title);

        setupInfoRow(R.id.row_address, R.drawable.ic_place,
                getString(R.string.contact_address_label), getString(R.string.contact_address_value),
                v -> IntentUtils.openMap(this, getString(R.string.map_query)));
        setupInfoRow(R.id.row_phone, R.drawable.ic_call,
                getString(R.string.contact_phone_label), getString(R.string.contact_phone_value),
                v -> IntentUtils.dial(this, getString(R.string.phone_main_dial)));
        setupInfoRow(R.id.row_fax, R.drawable.ic_print,
                getString(R.string.contact_fax_label), getString(R.string.contact_fax_value), null);
        setupInfoRow(R.id.row_email, R.drawable.ic_email,
                getString(R.string.contact_email_label), getString(R.string.contact_email_value),
                v -> IntentUtils.email(this, getString(R.string.email_address), getString(R.string.email_subject)));
        setupInfoRow(R.id.row_whatsapp, R.drawable.ic_chat,
                getString(R.string.contact_whatsapp_label), getString(R.string.contact_whatsapp_value),
                v -> openLink(R.string.url_whatsapp));
        setupInfoRow(R.id.row_website, R.drawable.ic_language,
                getString(R.string.contact_website_label), getString(R.string.contact_website_value),
                v -> openLink(R.string.url_gmi_home));

        findViewById(R.id.button_enquiry_form).setOnClickListener(v -> openLink(R.string.url_contact));
        findViewById(R.id.button_maps).setOnClickListener(v ->
                IntentUtils.openMap(this, getString(R.string.map_query)));
        findViewById(R.id.button_official_map).setOnClickListener(v -> openLink(R.string.url_official_map));

        findViewById(R.id.chip_facebook).setOnClickListener(v -> openLink(R.string.url_facebook));
        findViewById(R.id.chip_instagram).setOnClickListener(v -> openLink(R.string.url_instagram));
        findViewById(R.id.chip_tiktok).setOnClickListener(v -> openLink(R.string.url_tiktok));
        findViewById(R.id.chip_youtube).setOnClickListener(v -> openLink(R.string.url_youtube));
        findViewById(R.id.chip_linkedin).setOnClickListener(v -> openLink(R.string.url_linkedin));

        if (savedInstanceState == null) {
            AnimUtils.revealChildren(findViewById(R.id.content), 0, 100, 70);
            AnimUtils.pop(findViewById(R.id.about_logo), 250);
        }
    }

    private void openLink(@StringRes int urlRes) {
        IntentUtils.openUrl(this, getString(urlRes));
    }
}
