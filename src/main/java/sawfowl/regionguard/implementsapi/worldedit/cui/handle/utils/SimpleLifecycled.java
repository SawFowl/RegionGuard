package sawfowl.regionguard.implementsapi.worldedit.cui.handle.utils;

import java.util.Objects;
import java.util.Optional;

import javax.annotation.Nullable;

public final class SimpleLifecycled<T> implements Lifecycled<T> {

	public static <T> SimpleLifecycled<T> valid(T value) {
		return new SimpleLifecycled<>(Objects.requireNonNull(value));
	}

	public static <T> SimpleLifecycled<T> invalid() {
		return new SimpleLifecycled<>(null);
	}

	private final LifecycledCallbackHandler<T> events = new LifecycledCallbackHandler<>(this);
	@Nullable
	private T value;

	private SimpleLifecycled(@Nullable T value) {
		this.value = value;
	}

	/**
	 * Set the value of this lifecycled and fire the new value event.
	 *
	 * @param value the value
	 */
	public void newValue(T value) {
		// Ensure lifecycle constraints are upheld.
		invalidate();
		this.value = Objects.requireNonNull(value);
		events.fireOnNewValue();
	}

	/**
	 * Remove the value of this lifecycled and fire the invalidated event.
	 */
	public void invalidate() {
		boolean fire = this.value != null;
		this.value = null;
		if (fire) {
			events.fireInvalidated();
		}
	}

	@Override
	public Optional<T> value() {
		return Optional.ofNullable(value);
	}

	@Override
	public Events<T> events() {
		return events;
	}
}
