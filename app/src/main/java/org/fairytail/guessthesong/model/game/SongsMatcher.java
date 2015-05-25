package org.fairytail.guessthesong.model.game;

import org.apache.commons.lang3.StringUtils;
import org.fairytail.guessthesong.model.Song;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SongsMatcher {

    private final Song original;
    private final Song proposed;

    public boolean areSimilar() {
        return similarityPercent(original.getTitle(), proposed.getTitle()) >= 0.9d;
    }

    private double similarityPercent(String s1, String s2) {
        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater length
            longer = s2; shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
        return (longerLength - StringUtils.getLevenshteinDistance(longer, shorter)) / (double) longerLength;
    }

}
