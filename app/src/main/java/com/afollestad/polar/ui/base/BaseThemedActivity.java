package com.afollestad.polar.ui.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.v4.view.OnApplyWindowInsetsListener;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.WindowInsetsCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.afollestad.assent.AssentActivity;
import com.afollestad.materialdialogs.util.DialogUtils;
import com.afollestad.polar.R;
import com.afollestad.polar.util.Utils;

/**
 * @author Aidan Follestad (afollestad)
 */
public abstract class BaseThemedActivity extends AssentActivity {

    private static final String CONFIG_DARK_THEME = "config_dark_theme";

    private boolean mLastDarkTheme = false;

    public abstract Toolbar getToolbar();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        mLastDarkTheme = darkTheme();
        setTheme(getCurrentTheme());
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("PrivateResource")
    @Override
    protected void onStart() {
        super.onStart();
        final Toolbar toolbar = getToolbar();
        if (toolbar != null) {
            final int tintColor = DialogUtils.resolveColor(this, R.attr.tab_icon_color);
            toolbar.setTitleTextColor(tintColor);
            Utils.setOverflowButtonColor(this, tintColor);

            if (Utils.isColorLight(tintColor)) {
                toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat_Light);
            } else {
                toolbar.setPopupTheme(R.style.ThemeOverlay_AppCompat);
            }
        }
    }

    protected void applyTopInset(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                int systemWindowInsetTop = insets.getSystemWindowInsetTop();
                v.setPaddingRelative(0, systemWindowInsetTop, 0, v.getPaddingBottom());
                return insets;
            }
        });
    }

    protected void applyBottomInset(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @Override
            public WindowInsetsCompat onApplyWindowInsets(View v, WindowInsetsCompat insets) {
                int systemWindowInsetBottom = insets.getSystemWindowInsetBottom();
                v.setPaddingRelative(0, v.getPaddingTop(), 0, systemWindowInsetBottom);
                return insets;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mLastDarkTheme != darkTheme())
            recreate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        themeMenu(this, menu);
        return super.onCreateOptionsMenu(menu);
    }

    protected boolean isTranslucent() {
        return false;
    }

    @StyleRes
    private int getCurrentTheme() {
        if (isTranslucent()) {
            if (!mLastDarkTheme)
                return R.style.AppTheme_Light_Translucent;
            return R.style.AppTheme_Dark_Translucent;
        } else {
            if (!mLastDarkTheme)
                return R.style.AppTheme_Light;
            return R.style.AppTheme_Dark;
        }
    }

    public final void darkTheme(boolean newValue) {
        PreferenceManager.getDefaultSharedPreferences(this).edit()
                .putBoolean(CONFIG_DARK_THEME, newValue).commit();
    }

    public final boolean darkTheme() {
        if (!getResources().getBoolean(R.bool.allow_theme_switching))
            darkTheme(getResources().getBoolean(R.bool.dark_theme_default));
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(CONFIG_DARK_THEME, getResources().getBoolean(R.bool.dark_theme_default));
    }

    public static void themeMenu(Context context, Menu menu) {
        final int tintColor = DialogUtils.resolveColor(context, R.attr.tab_icon_color);
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item.getIcon() != null)
                item.setIcon(Utils.tintDrawable(item.getIcon(), tintColor));
        }
    }
}