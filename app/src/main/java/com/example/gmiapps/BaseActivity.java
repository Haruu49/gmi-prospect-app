package com.example.gmiapps;

import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.SystemBarStyle;
import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.appbar.MaterialToolbar;

/**
 * Shared behaviour for every screen: edge-to-edge drawing, system bar padding,
 * the top app bar and the icon/label/value info rows.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * Draws the app behind the status and navigation bars (required when targeting
     * Android 15+). The status bar uses light icons because every screen has a dark
     * blue header.
     */
    protected void enableEdgeToEdgeDisplay() {
        EdgeToEdge.enable(this, SystemBarStyle.dark(Color.TRANSPARENT));
    }

    /**
     * Pads views so nothing is hidden behind the system bars.
     *
     * @param root       screen root (gets left/right padding)
     * @param topView    view under the status bar (app bar or banner)
     * @param bottomView last scrolling container (gets bottom padding)
     */
    protected void applySystemBarInsets(View root, View topView, View bottomView) {
        applySystemBarInsets(root, topView, bottomView, null);
    }

    /**
     * Same as {@link #applySystemBarInsets(View, View, View)}, and also sizes a fixed
     * {@code statusBarScrim} view to the status bar height (used on the Home screen).
     */
    protected void applySystemBarInsets(View root, View topView, View bottomView, View statusBarScrim) {
        final int topStart = topView.getPaddingTop();
        final int bottomStart = bottomView.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, 0, bars.right, 0);
            topView.setPadding(topView.getPaddingLeft(), topStart + bars.top,
                    topView.getPaddingRight(), topView.getPaddingBottom());
            bottomView.setPadding(bottomView.getPaddingLeft(), bottomView.getPaddingTop(),
                    bottomView.getPaddingRight(), bottomStart + bars.bottom);
            if (statusBarScrim != null) {
                statusBarScrim.getLayoutParams().height = bars.top;
                statusBarScrim.requestLayout();
            }
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** Standard layout setup for inner screens: app bar with title and back arrow. */
    protected void setupScreen(@StringRes int titleRes) {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(titleRes);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        applySystemBarInsets(findViewById(R.id.root), findViewById(R.id.app_bar), findViewById(R.id.content));
    }

    /** The toolbar back arrow returns to the previous screen. */
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    /**
     * Fills an included {@code item_contact_row} layout.
     *
     * @param listener click action, or null for a plain information row
     */
    protected void setupInfoRow(@IdRes int rowId, @DrawableRes int iconRes,
                                CharSequence label, CharSequence value,
                                View.OnClickListener listener) {
        View row = findViewById(rowId);
        ((ImageView) row.findViewById(R.id.contact_icon)).setImageResource(iconRes);
        ((TextView) row.findViewById(R.id.contact_label)).setText(label);
        ((TextView) row.findViewById(R.id.contact_value)).setText(value);
        if (listener != null) {
            row.setOnClickListener(listener);
        } else {
            row.setClickable(false);
            row.setBackground(null);
        }
    }
}
