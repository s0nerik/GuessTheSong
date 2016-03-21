package org.fairytail.guessthesong.helpers;

import com.bluelinelabs.logansquare.LoganSquare;

import lombok.SneakyThrows;

public class JSON {

    @SneakyThrows
    public static <E> E parseSilently(String s, Class<E> clazz) {
        return LoganSquare.parse(s, clazz);
    }

}
