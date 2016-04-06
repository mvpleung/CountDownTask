package io.github.erehmi.countdown;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * @author WhatsAndroid
 */
class ViewAware<T> {
    private static final String TAG = "ViewAware";

    protected Reference<T> mViewRef;

    public ViewAware(T viewAware) {
        if (viewAware == null) throw new IllegalArgumentException("viewAware must not be null");
        mViewRef = new WeakReference<T>(viewAware);
    }

    public T getWrappedAware() {
        return mViewRef.get();
    }

    public boolean isCollected() {
        return mViewRef.get() == null;
    }

    public int getId() {
        T aware = mViewRef.get();
        return aware == null ? super.hashCode() : aware.hashCode();
    }

}
