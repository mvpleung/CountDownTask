package io.github.erehmi.samples;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import io.github.erehmi.countdown.CountDownTask;

public class ListActivity extends AppCompatActivity {
    private static final String TAG = ListActivity.class.getSimpleName();

    private ListView mListView;
    private CountDownAdapter mAdapter;

    private CountDownTask mCountDownTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_content);
        initList1();
    }

    private void initList1() {
        setList(makeList1(getDeadlineMillis()));
    }

    private long getDeadlineMillis() {
        return CountDownTask.elapsedRealtime() + 1000 * 5;
    }

    private List<CountDownInfo> makeList1(long millis) {
        List<CountDownInfo> list = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            CountDownInfo countDownInfo = new CountDownInfo();
            countDownInfo.countDownInterval = (i % 2 == 0) ? 500 : 1000;
            countDownInfo.millis = (i < 5) ? 0 : (millis + 1000 * i);
            list.add(countDownInfo);
        }
        return list;
    }

    private void setList(List<CountDownInfo> list) {
        mListView = (ListView) findViewById(android.R.id.list);
        mAdapter = new CountDownAdapter(this, list);
        mListView.setAdapter(mAdapter);
    }

    private void initList2() {
        setList(makeList2(getDeadlineMillis()));
    }

    private List<CountDownInfo> makeList2(long millis) {
        List<CountDownInfo> list = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            CountDownInfo countDownInfo = new CountDownInfo();
            countDownInfo.countDownInterval = 1000;
            countDownInfo.millis = (i % 2 == 0) ? 0 : (millis + 1000 * i);
            list.add(countDownInfo);
        }
        return list;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_list1:
                cancelCountDown();
                initList1();
                startCountDown();
                break;

            case R.id.action_list2:
                cancelCountDown();
                initList2();
                startCountDown();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startCountDown();
    }

    private void startCountDown() {
        mCountDownTask = CountDownTask.create();
        mAdapter.setCountDownTask(mCountDownTask);
    }

    @Override
    protected void onPause() {
        super.onPause();
        cancelCountDown();
    }

    private void cancelCountDown() {
        mAdapter.setCountDownTask(null);
        mCountDownTask.cancel();
    }
}
