package io.github.erehmi.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers.OnCountDownListener;

public class SimpleActivity extends AppCompatActivity implements OnCountDownListener<View> {
    private static final String TAG = SimpleActivity.class.getSimpleName();

    private TextView mCountDownTextView;
    private long mDeadlineMillis;

    private CountDownTask mCountDownTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_text);

        mCountDownTextView = (TextView) findViewById(R.id.count_down);
        mDeadlineMillis = CountDownTask.elapsedRealtime() + 1000 * 60;
    }

    @Override
    protected void onResume() {
        super.onResume();

        mCountDownTask = CountDownTask.create()
                .until(mCountDownTextView, mDeadlineMillis, 1000, this);
    }

    @Override
    public void onTick(View view, long millisUntilFinished) {
        mCountDownTextView.setText(String.valueOf((millisUntilFinished + 999) / 1000));
    }

    @Override
    public void onFinish(View view) {
        mCountDownTextView.setText("DONE.");
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCountDownTask.cancel();
    }
}
