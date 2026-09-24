package net.kdt.pojavlaunch.fragments;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import git.artdeell.mojo.R;
import net.kdt.pojavlaunch.Tools;
import net.kdt.pojavlaunch.instances.Instance;
import net.kdt.pojavlaunch.instances.Instances;
import net.kdt.pojavlaunch.instances.InstanceIconProvider;
import java.io.IOException;


public class DeleteConfirmDialogFragment extends DialogFragment {
    private final Instance mInstance = Instances.loadSelectedInstance();

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (mInstance == null) dismiss();
        AlertDialog dialog = new AlertDialog.Builder(requireContext())
                .setTitle(R.string.instance_delete)
                .setMessage(R.string.instance_delete_confirmation)
                .setPositiveButton(R.string.global_delete, (unusedDialog, which) -> {
                    if (mInstance == null) return;
                    InstanceIconProvider.dropIcon(mInstance);
                    Tools.removeCurrentFragment(requireActivity());
                    try {
                        Instances.removeInstance(mInstance);
                    } catch (IOException e) {
                        Tools.showErrorRemote(e);
                    }
                })
                .setNegativeButton(R.string.global_no, null)
                .create();
        dialog.setOnShowListener(d -> {
            if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(R.drawable.a_dialog);
            android.widget.TextView title = dialog.findViewById(androidx.appcompat.R.id.alertTitle);
            android.widget.TextView message = dialog.findViewById(android.R.id.message);
            if (title != null) title.setTextColor(requireContext().getColor(R.color.primary_text));
            if (message != null) message.setTextColor(requireContext().getColor(R.color.secondary_text));
            android.widget.Button positive = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            android.widget.Button negative = dialog.getButton(AlertDialog.BUTTON_NEGATIVE);
            if (positive != null) { positive.setTextColor(requireContext().getColor(R.color.a_accent_text)); positive.setAllCaps(false); }
            if (negative != null) { negative.setTextColor(requireContext().getColor(R.color.secondary_text)); negative.setAllCaps(false); }
        });
        return dialog;
    }
    public static String TAG = "delete_dialog_confirm";
}