package io.github.erehmi.countdown;

import android.os.SystemClock;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.github.erehmi.countdown.CountDownTimers.OnCountDownListener;

/**
 * @author WhatsAndroid
 */
public class CountDownTask<T> {
    private static final String TAG = "CountDownTask";

    public static CountDownTask create() {
        return new CountDownTask();
    }

    private volatile Map<Long, CountDownTimers> mMap;
    private volatile SparseArray<CountDownTimers> mViewIds;

    public CountDownTimers get(long countDownInterval) {
        return get(countDownInterval, false);
    }

    protected CountDownTimers get(long countDownInterval, boolean createIfNotExisted) {
        if (!createIfNotExisted) {
            return (mMap != null) ? mMap.get(countDownInterval) : null;
        }

        if (mMap == null) {
            synchronized (this) {
                if (mMap == null) {
                    mMap = Collections.synchronizedMap(new LinkedHashMap<Long, CountDownTimers>());
                }
            }
        }

        synchronized (this) {
            CountDownTimers timer = mMap.get(countDownInterval);
            if (timer == null) {
                timer = new CountDownTimers(countDownInterval);
                mMap.put(countDownInterval, timer);
            }
            return timer;
        }
    }

    public List<CountDownTimers> getAll() {
        List<CountDownTimers> list = null;
        if (mMap != null) {
            synchronized (this) {
                for (CountDownTimers task : mMap.values()) {
                    if (list == null) {
                        list = new ArrayList<>();
                    }
                    list.add(task);
                }
            }
        }
        return list;
    }

    public CountDownTask until(T aware, long millis, long countDownInterval, OnCountDownListener listener) {
        CountDownTimers timers = get(countDownInterval, true);
        addViewIds(aware, timers);
        timers.until(aware, millis, listener);
        return this;
    }

    private CountDownTimers addViewIds(T aware, CountDownTimers timers) {
        if (mViewIds == null) {
            synchronized (this) {
                if (mViewIds == null) {
                    mViewIds = new SparseArray<>();
                }
            }
        }

        int id = new ViewAware(aware).getId();
        synchronized (this) {
            CountDownTimers oldTimers = mViewIds.get(id);
            if (oldTimers != timers) {
                if (oldTimers != null) {
                    oldTimers.cancel(aware);
                }
                mViewIds.append(id, timers);
            }
            return oldTimers;
        }
    }

    public void cancel(T aware) {
        CountDownTimers timers = removeViewIds(aware);
        boolean empty = false;
        if (mViewIds != null) {
            synchronized (this) {
                empty = (mViewIds.size() == 0);
            }
        }
        if (empty) {
            cancel();
        } else if (timers != null) {
            timers.cancel(aware);
        }
    }

    private CountDownTimers removeViewIds(T aware) {
        CountDownTimers timers = null;
        int id = new ViewAware(aware).getId();

        if (mViewIds != null) {
            synchronized (this) {
                timers = mViewIds.get(id);
                if (timers != null) {
                    mViewIds.remove(id);
                }
            }
        }
        return timers;
    }

    protected void cancel(long countDownInterval) {
        CountDownTimers task = remove(countDownInterval);
        if (task != null) {
            task.cancel();
        }
    }

    protected CountDownTimers remove(long countDownInterval) {
        if (mMap != null) {
            synchronized (this) {
                return mMap.remove(countDownInterval);
            }
        }
        return null;
    }

    public void cancel() {
        if (mMap != null) {
            synchronized (this) {
                for (CountDownTimers task : mMap.values()) {
                    if (task != null) {
                        task.cancel();
                    }
                }
                mMap.clear();
            }
        }

        if (mViewIds != null) {
            synchronized (this) {
                mViewIds.clear();
            }
        }
    }

    public static long elapsedRealtime() {
        return SystemClock.elapsedRealtime();
    }
}
