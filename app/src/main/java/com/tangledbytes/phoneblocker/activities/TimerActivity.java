package com.tangledbytes.phoneblocker.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.tangledbytes.phoneblocker.R;
import com.tangledbytes.phoneblocker.utils.BlockerSession;
import com.tangledbytes.phoneblocker.utils.Utils;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.math.BigInteger;

public class TimerActivity extends AppCompatActivity {
    private static final String TAG = TimerActivity.class.getSimpleName();
    TextView tvTimer;
    TextView tvQuote;
    LinearProgressIndicator progressIndicator;
    BlockerSession blockerSession;

    // Represents the duration for which device while be kept unlocked while
    // session is going on
    private final long UNLOCK_DURATION = 3 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        tvTimer = findViewById(R.id.timer);
        tvQuote = findViewById(R.id.quote);
        progressIndicator = findViewById(R.id.progress);
        blockerSession = new BlockerSession(this);
        new UpdaterThread().start();
        new Handler(getMainLooper()).postDelayed(() -> finish(), UNLOCK_DURATION + 250);
    }

    private class UpdaterThread extends Thread {
        @Override
        public void run() {
            long lockTime = System.currentTimeMillis() + UNLOCK_DURATION;
            if(blockerSession.getEndTime() <= lockTime)
                lockTime = blockerSession.getEndTime();
            while(lockTime > System.currentTimeMillis()) {
                Utils.sleepThread(200);
                int[] remainingTime = Utils.getComponents(blockerSession.getRemainingSessionDuration());
                runOnUiThread(() -> {
                    progressIndicator.setProgressCompat(progressIndicator.getProgress() + 10, true);
                    tvTimer.setText(String.format(getString(R.string.remaining_time_format), remainingTime[2], remainingTime[1], remainingTime[0]));
                });
            }
            runOnUiThread(()-> progressIndicator.setProgressCompat(100, true));
        }
    }
}