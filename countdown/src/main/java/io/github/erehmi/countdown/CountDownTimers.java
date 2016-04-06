package io.github.erehmi.countdown;

import android.os.CountDownTimer;
import android.os.SystemClock;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.List;

/**
 * @author WhatsAndroid
 */
public class CountDownTimers<T> {
    private static final String TAG = "CountDownTimers";

    private final long mCountDownInterval;
    private long mMaxMillis;

    private CountDownTimer mCountDownTimer;

    public interface OnCountDownListener<T> {
        void onTick(T viewAware, long millisUntilFinished);

        void onFinish(T viewAware);
    }

    private static class CountDownInfo {
        ViewAware viewAware;
        long millis;
        OnCountDownListener listener;

        public CountDownInfo(ViewAware viewAware, long millis, OnCountDownListener listener) {
            this.viewAware = viewAware;
            this.millis = millis;
            this.listener = listener;
        }
    }

    private SparseArray<CountDownInfo> mCountDownInfoSparseArray;

    CountDownTimers(long countDownInterval) {
        mCountDownInterval = countDownInterval;
    }

    public long getMaxMillis() {
        return mMaxMillis;
    }

    public long getCountDownInterval() {
        return mCountDownInterval;
    }

    protected void until(T aware, long millis, OnCountDownListener listener) {
        ViewAware viewAware = new ViewAware(aware);
        CountDownInfo countDownInfo = new CountDownInfo(viewAware, millis, listener);
        ensureCountDownInfoSparseArray();

        long currentMillis = SystemClock.elapsedRealtime();
        int id = viewAware.getId();
        if (doOnTickOrFinish(countDownInfo, currentMillis)) {
            mCountDownInfoSparseArray.remove(id);
            return;
        }

        mCountDownInfoSparseArray.append(id, countDownInfo);

        long millisInFuture = millis - currentMillis;
        if ((millisInFuture > 0) && (millis > mMaxMillis)) {
            millisInFuture = adjustMillisInFuture(millisInFuture);
            LogUtils.d("create CountDownTimer: " + millisInFuture);

            mMaxMillis = millis;
            cancelCountDownTimer();

            mCountDownTimer = new CountDownTimer(millisInFuture, mCountDownInterval) {
                @Override
                public void onTick(long millisUntilFinished) {
                    LogUtils.d("CountDownTimer#onTick() # millisUntilFinished: " + millisUntilFinished);
                    doOnTick(millisUntilFinished);
                }

                @Override
                public void onFinish() {
                    LogUtils.d("onFinish()");
                    doOnFinish();
                }
            }.start();
        }
    }

    private void ensureCountDownInfoSparseArray() {
        if (mCountDownInfoSparseArray == null) {
            mCountDownInfoSparseArray = new SparseArray<>();
        }
    }

    // reduce error for the last one
    private long adjustMillisInFuture(long millisInFuture) {
        //millisInFuture = (millisInFuture + (mCountDownInterval - 1)) / mCountDownInterval * mCountDownInterval;
        return millisInFuture + mCountDownInterval;
    }

    private void doOnTick(long maxMillisUntilFinished) {
        if (mCountDownInfoSparseArray != null) {
            List<CountDownInfo> removeList = new ArrayList<>();
            long currentMillis = SystemClock.elapsedRealtime();
            for (int i = 0; i < mCountDownInfoSparseArray.size(); i++) {
                CountDownInfo countDownInfo = mCountDownInfoSparseArray.valueAt(i);
                if (doOnTickOrFinish(countDownInfo, currentMillis)) {
                    removeList.add(countDownInfo);
                }
            }

            if (!removeList.isEmpty()) {
                for (CountDownInfo countDownInfo : removeList) {
                    int id = countDownInfo.viewAware.getId();
                    mCountDownInfoSparseArray.remove(id);
                }
            }
        }
    }

    private boolean doOnTickOrFinish(CountDownInfo countDownInfo, long currentMillis) {
        LogUtils.d("doOnTickOrFinish() # id: " + countDownInfo.viewAware.getId());

        if (countDownInfo.millis > currentMillis) {
            doOnTick(countDownInfo, currentMillis);
            return false;
        } else {
            doOnFinish(countDownInfo);
            return true;
        }
    }

    private void doOnTick(CountDownInfo countDownInfo, long currentMillis) {
        long millis = countDownInfo.millis;
        OnCountDownListener listener = countDownInfo.listener;

        ViewAware<T> viewAware = countDownInfo.viewAware;
        T aware = viewAware.getWrappedAware();
        if (millis > currentMillis && aware != null && listener != null) {
            listener.onTick(aware, millis - currentMillis);
        }
    }

    private void doOnFinish(CountDownInfo countDownInfo) {
        ViewAware<T> viewAware = countDownInfo.viewAware;
        int id = viewAware.getId();
        LogUtils.d("doOnFinish() # id: " + id);

        T aware = viewAware.getWrappedAware();
        OnCountDownListener listener = countDownInfo.listener;
        if (aware != null && listener != null) {
            listener.onFinish(aware);
        }
    }

    private void doOnFinish() {
        if (mCountDownInfoSparseArray != null) {
            for (int i = 0; i < mCountDownInfoSparseArray.size(); i++) {
                CountDownInfo countDownInfo = mCountDownInfoSparseArray.valueAt(i);
                doOnFinish(countDownInfo);
            }
            resetCountDownInfos();
        }
    }

    protected void cancel(T aware) {
        ViewAware viewAware = new ViewAware(aware);
        int id = viewAware.getId();
        if (mCountDownInfoSparseArray != null) {
            CountDownInfo countDownInfo = mCountDownInfoSparseArray.get(id);
            if (countDownInfo != null) {
                mCountDownInfoSparseArray.remove(id);
            }
        }
    }

    protected void cancel() {
        resetCountDownInfos();
        cancelCountDownTimer();
    }

    private void resetCountDownInfos() {
        if (mCountDownInfoSparseArray != null) {
            mCountDownInfoSparseArray.clear();
            mCountDownInfoSparseArray = null;
        }
    }

    private void cancelCountDownTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }
}
