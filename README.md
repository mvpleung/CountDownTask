CountDownTask
=============

<img src="https://github.com/mvpleung/CountDownTask/blob/master/art/countdown.gif" width="320" height="480" />

用法
----
###
			mCountDownTask.until(convertView, 6000, 1000, new OnCountDownListener<View>() {
                @Override
                public void onTick(View viewAware, long millisUntilFinished) {
                    //TODO
                }

                @Override
                public void onFinish(View viewAware) {
                    //TODO
                }
            });
###

http://www.cnblogs.com/erehmi/p/5305742.html
