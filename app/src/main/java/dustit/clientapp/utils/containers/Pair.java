package dustit.clientapp.utils.containers;

/**
 * Created by nikita on 30.01.18.
 */

public class Pair<V, A> {
    private V v;
    private A a;

    public Pair(V v, A a) {
        this.v = v;
        this.a = a;
    }

    public A getMem() {
        return a;
    }

    public V getPosition() {
        return v;
    }
}
