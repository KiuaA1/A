package net.kdt.pojavlaunch.prefs.screens;


import android.Manifest;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.recyclerview.widget.RecyclerView;

import net.kdt.pojavlaunch.LauncherActivity;
import git.artdeell.mojo.R;
import net.kdt.pojavlaunch.prefs.LauncherPreferences;

/**
 * Preference for the main screen, any sub-screen should inherit this class for consistent behavior,
 * overriding only onCreatePreferences
 */
public class LauncherPreferenceFragment extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {
    protected Runnable mVisibilityUpdater = () -> {};

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.setBackgroundResource(R.drawable.a_background);

        RecyclerView recyclerView = view.findViewById(androidx.preference.R.id.recycler_view);
        if (recyclerView == null) return;

        recyclerView.setBackgroundResource(R.drawable.a_background);
        recyclerView.setClipToPadding(false);
        int horizontal = getResources().getDimensionPixelSize(R.dimen._12sdp);
        int vertical = getResources().getDimensionPixelSize(R.dimen._8sdp);
        recyclerView.setPadding(horizontal, vertical, horizontal, vertical);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View child,
                                        @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.bottom = vertical;
            }
        });

        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override public void onChildViewAttachedToWindow(@NonNull View child) {
                stylePreferenceRow(child);
            }
            @Override public void onChildViewDetachedFromWindow(@NonNull View child) { }
        });
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            stylePreferenceRow(recyclerView.getChildAt(i));
        }
    }

    private void stylePreferenceRow(View row) {
        if (row.findViewById(android.R.id.title) == null) return;
        row.setBackgroundResource(R.drawable.a_list_item);
        int horizontal = getResources().getDimensionPixelSize(R.dimen._14sdp);
        int vertical = getResources().getDimensionPixelSize(R.dimen._10sdp);
        row.setPadding(horizontal, vertical, horizontal, vertical);
        stylePreferenceChildren(row);
    }

    private void stylePreferenceChildren(View view) {
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (textView.getId() == android.R.id.summary) {
                textView.setTextColor(requireContext().getColor(R.color.secondary_text));
            } else if (textView.getId() == android.R.id.title || textView.getId() == R.id.seekbar_value) {
                textView.setTextColor(requireContext().getColor(R.color.primary_text));
            }
        } else if (view instanceof ImageView) {
            ImageView imageView = (ImageView) view;
            Drawable drawable = imageView.getDrawable();
            if (drawable != null) drawable.setTint(requireContext().getColor(R.color.primary_text));
        }
        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                stylePreferenceChildren(group.getChildAt(i));
            }
        }
    }

    @Override
    public void onCreatePreferences(Bundle b, String str) {
        mVisibilityUpdater = this::updateVisibility;
        addPreferencesFromResource(R.xml.pref_main);
        setupNotificationRequestPreference();
    }

    private void updateVisibility(){
        requirePreference("notification_permission_request").setVisible(!getLauncherActivity().checkForPermission(33, Manifest.permission.POST_NOTIFICATIONS));
    }

    private void setupNotificationRequestPreference() {
        Preference mRequestNotificationPermissionPreference = requirePreference("notification_permission_request");
        Activity activity = getActivity();
        if(activity instanceof LauncherActivity) {
            mRequestNotificationPermissionPreference.setOnPreferenceClickListener(preference -> {
                ((LauncherActivity) activity).askForPermission(33, Manifest.permission.POST_NOTIFICATIONS);
                return true;
            });
        }else{
            mRequestNotificationPermissionPreference.setVisible(false);
        }
        updateVisibility();
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        if(sharedPreferences != null) sharedPreferences.registerOnSharedPreferenceChangeListener(this);
        mVisibilityUpdater.run();
    }

    @Override
    public void onPause() {
        SharedPreferences sharedPreferences = getPreferenceManager().getSharedPreferences();
        if(sharedPreferences != null) sharedPreferences.unregisterOnSharedPreferenceChangeListener(this);
        super.onPause();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences p, String s) {
        LauncherPreferences.loadPreferences(getContext());
    }

    protected Preference requirePreference(CharSequence key) {
        Preference preference = findPreference(key);
        if(preference != null) return preference;
        throw new IllegalStateException("Preference "+key+" is null");
    }
    @SuppressWarnings("unchecked")
    protected <T extends Preference> T requirePreference(CharSequence key, Class<T> preferenceClass) {
        Preference preference = requirePreference(key);
        if(preferenceClass.isInstance(preference)) return (T)preference;
        throw new IllegalStateException("Preference "+key+" is not an instance of "+preferenceClass.getSimpleName());
    }
    protected LauncherActivity getLauncherActivity(){
        return ((LauncherActivity) getActivity());
    }
}
