package fatworm.util;

public interface Predicate<T> {
    public boolean apply(T object);
}
