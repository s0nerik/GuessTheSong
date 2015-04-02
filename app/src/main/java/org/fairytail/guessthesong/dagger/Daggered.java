package org.fairytail.guessthesong.dagger;

public class Daggered {
    public Daggered() {
        Injector.inject(this);
    }
}
