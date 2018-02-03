package dustit.clientapp.utils.containers;

public class Container<V> {
    private V v;

    public V get() {
        return v;
    }

    public void put(V v) {
        this.v = v;
    }
}
