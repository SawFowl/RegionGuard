package sawfowl.regionguard.utils.worldedit.cuihandle.utils;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.BiConsumer;

/**
 * Convenience class for implementing the callbacks of {@link Lifecycled}.
 */
public class LifecycledCallbackHandler<T> implements Lifecycled.Events<T> {
    private final Lifecycled<T> lifecycled;
    private final Lock lock = new ReentrantLock();
    private final Map<Object, BiConsumer<?, ? super Lifecycled<T>>> onInvalidatedCallbacks =
        new WeakHashMap<>();
    private final Map<Object, BiConsumer<?, ? super Lifecycled<T>>> onNewValueCallbacks =
        new WeakHashMap<>();

    public LifecycledCallbackHandler(Lifecycled<T> lifecycled) {
        this.lifecycled = lifecycled;
    }

    @Override
    public <O> void onInvalidated(O owner, BiConsumer<O, ? super Lifecycled<T>> callback) {
        lock.lock();
        try {
            onInvalidatedCallbacks.put(owner, callback);
            if (!lifecycled.isValid()) {
                callback.accept(owner, lifecycled);
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public <O> void onNewValue(O owner, BiConsumer<O, ? super Lifecycled<T>> callback) {
        lock.lock();
        try {
            onNewValueCallbacks.put(owner, callback);
            if (lifecycled.isValid()) {
                callback.accept(owner, lifecycled);
            }
        } finally {
            lock.unlock();
        }
    }


    /**
     * Fire {@link #onInvalidated(Object, BiConsumer)} callbacks.
     */
    public void fireInvalidated() {
        lock.lock();
        try {
            for (Map.Entry<Object, BiConsumer<?, ? super Lifecycled<T>>> callback : onInvalidatedCallbacks.entrySet()) {
                Object owner = callback.getKey();
                if (owner == null) {
                    // GC'd, continue
                    continue;
                }
                @SuppressWarnings("unchecked")
                BiConsumer<Object, ? super Lifecycled<T>> cast =
                    (BiConsumer<Object, ? super Lifecycled<T>>) callback.getValue();
                cast.accept(owner, lifecycled);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * Fire {@link #onNewValue(Object, BiConsumer)} callbacks, the {@link Lifecycled#value()} must
     * be available.
     */
    public void fireOnNewValue() {
        lock.lock();
        try {
            for (Map.Entry<Object, BiConsumer<?, ? super Lifecycled<T>>> callback : onNewValueCallbacks.entrySet()) {
                Object owner = callback.getKey();
                if (owner == null) {
                    // GC'd, continue
                    continue;
                }
                @SuppressWarnings("unchecked")
                BiConsumer<Object, ? super Lifecycled<T>> cast =
                    (BiConsumer<Object, ? super Lifecycled<T>>) callback.getValue();
                cast.accept(owner, lifecycled);
            }
        } finally {
            lock.unlock();
        }
    }
}