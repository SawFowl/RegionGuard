package sawfowl.regionguard.utils.worldedit.cuihandle.utils;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

public interface Lifecycled<T> {

    interface Events<T> {
        /**
         * Add a callback for when this lifecycled is given a new value. Will be called immediately
         * if this lifecycled is currently valid.
         *
         * <p>
         * The callback should not reference the owner, it must only access it via the parameter.
         * This ensures that the owner will be GC-able, otherwise it may be stuck in a reference
         * loop.
         * </p>
         *
         * @param owner when the owner is GC'd, the callback is removed
         * @param callback the callback, will be passed the lifecycled object
         */
        <O> void onNewValue(O owner, BiConsumer<O, ? super Lifecycled<T>> callback);

        /**
         * Add a callback for when this lifecycled is invalidated. Will be called immediately if
         * this lifecycled is currently invalid.
         *
         * <p>
         * The callback should not reference the owner, it must only access it via the parameter.
         * This ensures that the owner will be GC-able, otherwise it may be stuck in a reference
         * loop.
         * </p>
         *
         * @param owner when the owner is GC'd, the callback is removed
         * @param callback the callback, will be passed the lifecycled object
         */
        <O> void onInvalidated(O owner, BiConsumer<O, ? super Lifecycled<T>> callback);
    }

    /**
     * Get the value or {@link Optional#empty()}.
     *
     * @return the value
     */
    Optional<T> value();

    /**
     * Get the value or throw.
     *
     * @return the value
     * @throws IllegalStateException if there is no value
     */
    default T valueOrThrow() throws IllegalStateException {
        return value().orElseThrow(() -> new IllegalStateException("Currently invalid"));
    }

    /**
     * Check for validity, usually without triggering computation.
     *
     * @return if this lifecycled's {@link #value()} is valid
     */
    default boolean isValid() {
        return value().isPresent();
    }

    /**
     * Get the event manager for this lifecycled object.
     *
     * @return the event manager
     */
    Events<T> events();

    /**
     * Map the value.
     *
     * @param mapper the mapper function
     * @param <U> the new type
     * @return the downstream lifecycled
     */
    default <U> Lifecycled<U> map(Function<T, U> mapper) {
        return new MapLifecycled<>(this, mapper);
    }

    /**
     * Filter the value. In other words, create a new lifecycled object where validity is ANDed
     * with the result of calling the filter function.
     *
     * @param filterer the filter function
     * @return the downstream lifecycled
     */
    default Lifecycled<T> filter(Predicate<T> filterer) {
        SimpleLifecycled<T> downstream = SimpleLifecycled.invalid();
        events().onInvalidated(downstream, (d, lifecycled) -> d.invalidate());
        events().onNewValue(downstream, (d, lifecycled) -> {
            T value = lifecycled.valueOrThrow();
            if (filterer.test(value)) {
                d.newValue(value);
            }
        });
        return downstream;
    }

    default <U> Lifecycled<U> flatMap(Function<T, Lifecycled<U>> mapper) {
        return new FlatMapLifecycled<>(this, mapper);
    }
}