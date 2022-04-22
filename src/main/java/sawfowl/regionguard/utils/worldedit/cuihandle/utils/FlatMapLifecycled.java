package sawfowl.regionguard.utils.worldedit.cuihandle.utils;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;

class FlatMapLifecycled<T, U> implements Lifecycled<U> {
    private final LifecycledCallbackHandler<U> events = new LifecycledCallbackHandler<>(this);
    private Lifecycled<U> mapped;
    private Token<FlatMapLifecycled<T, U>> mappedToken;
    @Nullable
    private U value;

    FlatMapLifecycled(Lifecycled<T> upstream, Function<T, Lifecycled<U>> mapper) {
        upstream.events().onInvalidated(this, (this$, up) -> {
            boolean fire = this$.value != null;
            this$.value = null;
            // drop `mapped` hooks if needed
            this$.mappedToken = null;
            this$.mapped = null;
            if (fire) {
                this$.events.fireInvalidated();
            }
        });
        upstream.events().onNewValue(this, (this$, up) -> {
            this$.mapped = mapper.apply(up.valueOrThrow());
            this$.mappedToken = new Token<>(this$);
            mapped.events().onInvalidated(this$.mappedToken, (token, mapped$) -> {
                boolean fire = token.inner.value != null;
                token.inner.value = null;
                // note we do not drop the token here, onNewValue may be called again
                if (fire) {
                    this$.events.fireInvalidated();
                }
            });
            mapped.events().onNewValue(this$.mappedToken, (token, mapped$) -> {
                U newValue = mapped$.valueOrThrow();
                boolean fire = token.inner.value != newValue;
                token.inner.value = newValue;
                if (fire) {
                    this$.events.fireOnNewValue();
                }
            });
        });
    }

    @Override
    public Optional<U> value() {
        return Optional.ofNullable(value);
    }

    @Override
    public boolean isValid() {
        return value != null;
    }

    @Override
    public Events<U> events() {
        return events;
    }
}