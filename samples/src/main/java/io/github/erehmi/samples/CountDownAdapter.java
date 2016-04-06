package io.github.erehmi.samples;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import io.github.erehmi.countdown.CountDownTask;
import io.github.erehmi.countdown.CountDownTimers.OnCountDownListener;

/**
 * @author WhatsAndroid
 */
public class CountDownAdapter extends ArrayAdapter<CountDownInfo> {
    private static final String TAG = "CountDownAdapter";

    private CountDownTask mCountDownTask;

    public CountDownAdapter(Context context, List<CountDownInfo> objects) {
        super(context, 0, objects);
    }

    public void setCountDownTask(CountDownTask countDownTask) {
        if (!Objects.equals(mCountDownTask, countDownTask)) {
            mCountDownTask = countDownTask;
            notifyDataSetChanged();
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder = null;
        if (convertView == null) {
            mViewHolder = new ViewHolder(convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false));
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        CountDownInfo countDownInfo = getItem(position);
        if (countDownInfo.millis > 0) {
            startCountDown(position, countDownInfo, mViewHolder);
        } else {
            cancelCountDown(position, countDownInfo, mViewHolder);
        }
        return convertView;
    }

    private void startCountDown(final int position, final CountDownInfo countDownInfo, ViewHolder mViewHolder) {
        if (mCountDownTask != null) {
            mCountDownTask.until(mViewHolder, countDownInfo.millis, countDownInfo.countDownInterval, new OnCountDownListener<ViewHolder>() {
                @Override
                public void onTick(ViewHolder viewAware, long millisUntilFinished) {
                    doOnTick(position, viewAware, millisUntilFinished, countDownInfo.countDownInterval);
                }

                @Override
                public void onFinish(ViewHolder viewAware) {
                    doOnFinish(position, viewAware);
                }
            });
        }
    }

    private void doOnTick(int position, ViewHolder mViewHolder, long millisUntilFinished, long countDownInterval) {
        mViewHolder.mTextTitle.setText(String.valueOf(position));

        mViewHolder.mTextCountDowner.setText(String.valueOf((millisUntilFinished + countDownInterval - 1) / countDownInterval));
    }

    private void doOnFinish(int position, ViewHolder mViewHolder) {
        mViewHolder.mTextTitle.setText(String.valueOf(position));

        mViewHolder.mTextCountDowner.setText("DONE.");
    }

    private void cancelCountDown(int position, CountDownInfo countDownInfo, ViewHolder mViewHolder) {
        if (mCountDownTask != null) {
            mCountDownTask.cancel(mViewHolder);
        }

        mViewHolder.mTextTitle.setText(String.valueOf(position));

        mViewHolder.mTextCountDowner.setText(null);
    }

    private final static class ViewHolder {
        TextView mTextTitle, mTextCountDowner;

        public ViewHolder(View convertView) {
            mTextTitle = (TextView) convertView.findViewById(android.R.id.text1);
            mTextCountDowner = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(this);
        }
    }
}
