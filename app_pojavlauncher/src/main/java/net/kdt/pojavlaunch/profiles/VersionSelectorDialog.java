package net.kdt.pojavlaunch.profiles;

import static net.kdt.pojavlaunch.extra.ExtraCore.getValue;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ExpandableListView;

import androidx.appcompat.app.AlertDialog;

import net.kdt.pojavlaunch.JVersionList;
import git.artdeell.mojo.R;
import net.kdt.pojavlaunch.extra.ExtraConstants;

public class VersionSelectorDialog {
    public static void open(Context context, boolean hideCustomVersions, VersionSelectorListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ExpandableListView expandableListView = (ExpandableListView) LayoutInflater.from(context)
                .inflate(R.layout.dialog_expendable_list_view , null);
        JVersionList jVersionList = (JVersionList) getValue(ExtraConstants.RELEASE_TABLE);
        JVersionList.Version[] versionArray;
        if(jVersionList == null || jVersionList.versions == null) versionArray = new JVersionList.Version[0];
        else versionArray = jVersionList.versions;
        VersionListAdapter adapter = new VersionListAdapter(versionArray, hideCustomVersions, context);

        expandableListView.setAdapter(adapter);
        builder.setView(expandableListView);
        AlertDialog dialog = builder.create();
        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(R.drawable.a_dialog);
            android.widget.Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            android.widget.Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (positive != null) { positive.setTextColor(context.getColor(R.color.a_accent_text)); positive.setAllCaps(false); }
            if (negative != null) { negative.setTextColor(context.getColor(R.color.secondary_text)); negative.setAllCaps(false); }
        });
        dialog.show();

        expandableListView.setOnChildClickListener((parent, v1, groupPosition, childPosition, id) -> {
            String version = adapter.getChild(groupPosition, childPosition);
            listener.onVersionSelected(version, adapter.isSnapshotSelected(groupPosition));
            dialog.dismiss();
            return true;
        });
    }
}
