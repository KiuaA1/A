package net.kdt.pojavlaunch.multirt;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import git.artdeell.mojo.R;

public class MultiRTConfigDialog {
    private AlertDialog mDialog;
    private RecyclerView mDialogView;

    /** Show the dialog, refreshes the adapter data before showing it */
    public void show(){
        refresh();
        mDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged") //only used to completely refresh the list, it is necessary
    public void refresh() {
        RecyclerView.Adapter<?> adapter = mDialogView.getAdapter();
        if(adapter != null) adapter.notifyDataSetChanged();
    }

    /** Build the dialog behavior and style */
    public void prepare(Context activity, ActivityResultLauncher<Object> installJvmLauncher) {
        mDialogView = new RecyclerView(activity);
        mDialogView.setLayoutManager(new LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false));
        RTRecyclerViewAdapter adapter = new RTRecyclerViewAdapter();
        mDialogView.setAdapter(adapter);

        mDialog = new AlertDialog.Builder(activity)
                .setTitle(R.string.multirt_config_title)
                .setView(mDialogView)
                .setPositiveButton(R.string.multirt_config_add, (dialog, which) -> installJvmLauncher.launch(null))
                .setNeutralButton(R.string.multirt_delete_runtime, null)
                .create();

        mDialog.setOnShowListener(dialog -> {
            if (mDialog.getWindow() != null) mDialog.getWindow().setBackgroundDrawableResource(R.drawable.a_dialog);
            android.widget.TextView title = mDialog.findViewById(androidx.appcompat.R.id.alertTitle);
            if (title != null) title.setTextColor(activity.getColor(R.color.primary_text));
            Button positive = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
            Button neutral = mDialog.getButton(AlertDialog.BUTTON_NEUTRAL);
            if (positive != null) {
                positive.setTextColor(activity.getColor(R.color.a_accent_text));
                positive.setAllCaps(false);
            }
            if (neutral != null) {
                neutral.setTextColor(activity.getColor(R.color.secondary_text));
                neutral.setAllCaps(false);
                neutral.setOnClickListener(view -> {
                    boolean isEditing = !adapter.getIsEditing();
                    adapter.setIsEditing(isEditing);
                    neutral.setText(isEditing ? R.string.multirt_config_setdefault : R.string.multirt_delete_runtime);
                });
            }
        });
    }
}
