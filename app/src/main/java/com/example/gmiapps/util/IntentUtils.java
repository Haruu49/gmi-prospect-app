package com.example.gmiapps.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

import com.example.gmiapps.R;

/** Opens links, the phone dialler, email and maps with implicit intents. */
public final class IntentUtils {

    private IntentUtils() {
    }

    public static void openUrl(Context context, String url) {
        start(context, new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
    }

    /** Opens the dialler with the number filled in (no CALL_PHONE permission needed). */
    public static void dial(Context context, String phoneNumber) {
        start(context, new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber)));
    }

    public static void email(Context context, String address, String subject) {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:" + address));
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        start(context, intent);
    }

    /** Opens a maps app at the address; falls back to Google Maps in the browser. */
    public static void openMap(Context context, String query) {
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + Uri.encode(query)));
        try {
            context.startActivity(mapIntent);
        } catch (ActivityNotFoundException e) {
            openUrl(context, "https://www.google.com/maps/search/?api=1&query=" + Uri.encode(query));
        }
    }

    private static void start(Context context, Intent intent) {
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context, R.string.error_no_app, Toast.LENGTH_SHORT).show();
        }
    }
}
