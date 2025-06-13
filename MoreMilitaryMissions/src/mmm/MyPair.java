package mmm;

import java.util.Comparator;
import java.util.Objects;

public final class MyPair<A extends Comparable<A>, B extends Comparable<B>> implements Comparable<MyPair<A, B>> {
    public A one = null;
    public B two = null;

    public MyPair(A one, B two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public int hashCode() {
        return Objects.hash(one, two);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof MyPair)) return false;
        MyPair<?, ?> pair = (MyPair<?, ?>) obj;
        return Objects.equals(one, pair.one) && Objects.equals(two, pair.two);

    }

    @Override
    public String toString() {
        return "(" + one + ", " + two + ")";
    }

    @Override
    public int compareTo(MyPair<A, B> o) {
        return Comparator.<MyPair<A, B>, A>comparing(x -> x.one)
                .thenComparing(x -> x.two)
                .compare(this, o);
    }
}
