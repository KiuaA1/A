package net.kdt.pojavlaunch;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import git.artdeell.mojo.R;

public class FatalErrorActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if(extras == null) { finish(); return; }
        boolean storageAllow = extras.getBoolean("storageAllow", false);
        Throwable throwable = (Throwable) extras.getSerializable("throwable");
        final String stackTrace = throwable != null ? Tools.printToString(throwable) : "<null>";
        String strSavePath = extras.getString("savePath");
        String errHeader = storageAllow ? "Crash stack trace saved to " + strSavePath + "." : "Storage permission is required to save crash stack trace!";

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.error_fatal)
                .setMessage(errHeader + "\n\n" + stackTrace)
                .setPositiveButton(android.R.string.ok, (p1, p2) -> finish())
                .setNegativeButton(R.string.global_restart, (p1, p2) -> startActivity(new Intent(FatalErrorActivity.this, LauncherActivity.class)))
                .setNeutralButton(android.R.string.copy, (p1, p2) -> {
                    ClipboardManager mgr = (ClipboardManager) FatalErrorActivity.this.getSystemService(CLIPBOARD_SERVICE);
                    mgr.setPrimaryClip(ClipData.newPlainText("error", stackTrace));
                    finish();
                })
                .setCancelable(false)
                .create();
        styleDialog(dialog);
        dialog.show();
    }

    private void styleDialog(AlertDialog dialog) {
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(R.drawable.a_dialog);
        TextView title = dialog.findViewById(androidx.appcompat.R.id.alertTitle);
        TextView message = dialog.findViewById(android.R.id.message);
        if (title != null) title.setTextColor(getColor(R.color.primary_text));
        if (message != null) message.setTextColor(getColor(R.color.secondary_text));
        for (int id : new int[]{android.R.id.button1, android.R.id.button2, android.R.id.button3}) {
            Button b = dialog.findViewById(id);
            if (b != null) { b.setTextColor(getColor(R.color.a_accent_text)); b.setAllCaps(false); }
        }
    }

    public static void showError(Context ctx, String savePath, boolean storageAllow, Throwable th) {
        Intent fatalErrorIntent = new Intent(ctx, FatalErrorActivity.class);
        fatalErrorIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        fatalErrorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        fatalErrorIntent.putExtra("throwable", th);
        fatalErrorIntent.putExtra("savePath", savePath);
        fatalErrorIntent.putExtra("storageAllow", storageAllow);
        ctx.startActivity(fatalErrorIntent);
    }
}