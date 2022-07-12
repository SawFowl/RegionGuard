package sawfowl.regionguard.utils.worldedit.cuihandle.utils;

/**
 * Used to create a new strong reference to an object that can be separately dropped.
 *
 * @param <T> the inner object
 */
class Token<T> {
    final T inner;

    Token(T inner) {
        this.inner = inner;
    }
}