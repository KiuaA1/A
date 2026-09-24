package net.kdt.pojavlaunch;

import static net.kdt.pojavlaunch.Tools.shareLog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import git.artdeell.mojo.R;

@Keep
public class ExitActivity extends AppCompatActivity {
    @SuppressLint("StringFormatInvalid")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int code = -1;
        boolean isSignal = false;
        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            code = extras.getInt("code",-1);
            isSignal = extras.getBoolean("isSignal", false);
        }
        String message = isSignal ? getString(R.string.mcn_abort_title) : getString(R.string.mcn_exit_title, code);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton(R.string.main_share_logs, (d, which) -> shareLog(this))
                .setOnDismissListener(d -> ExitActivity.this.finish())
                .create();
        styleDialog(dialog);
        dialog.show();
    }

    private void styleDialog(AlertDialog dialog) {
        if (dialog.getWindow() != null) dialog.getWindow().setBackgroundDrawableResource(R.drawable.a_dialog);
        TextView message = dialog.findViewById(android.R.id.message);
        if (message != null) message.setTextColor(getColor(R.color.secondary_text));
        Button positive = dialog.findViewById(android.R.id.button1);
        if (positive != null) { positive.setTextColor(getColor(R.color.a_accent_text)); positive.setAllCaps(false); }
    }

    @SuppressWarnings("unused")
    public static void showExitMessage(Context ctx, int code, boolean isSignal) {
        if((!isSignal && code == 0)) {
            if(ctx != null) Tools.restartLauncherActivity(ctx);
            System.exit(0);
            return;
        }
        Object lock = new Object();
        Tools.runOnUiThread(()->{
            Intent i = new Intent(ctx,ExitActivity.class);
            i.putExtra("code",code);
            i.putExtra("isSignal", isSignal);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            ctx.startActivity(i);
            synchronized (lock) { lock.notify(); }
        });
        synchronized (lock) {
            try { lock.wait(); } catch (InterruptedException e) { Log.e("ExitActivity", "Waiting on lock failed: "+e); }
        }
        System.exit(0);
    }
}