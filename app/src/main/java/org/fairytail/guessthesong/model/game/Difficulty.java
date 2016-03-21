package org.fairytail.guessthesong.model.game;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.bluelinelabs.logansquare.typeconverters.StringBasedTypeConverter;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@JsonObject
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Difficulty implements Serializable {

    // Must be one of R.array.difficulties
    public enum Level {
        EASY, MEDIUM, HARD
    }

    public static class LevelConverter extends StringBasedTypeConverter<Level> {
        @Override
        public Level getFromString(String s) {
            return Level.valueOf(s);
        }

        public String convertToString(Level object) {
            return object.toString();
        }
    }

    @JsonField
    String name;

    @JsonField
    long songDuration;
    @JsonField
    int variants;
    @JsonField
    boolean proposeSimilarStyles;

    @JsonField(typeConverter = LevelConverter.class)
    Level level;

    public static class Factory {

        public static Difficulty create(Level level) {
            switch (level) {
                case EASY:
                    return Difficulty.builder()
                            .level(level)
                            .name("Easy")
                            .songDuration(1000 * 30) // 30 seconds
                            .proposeSimilarStyles(false)
                            .variants(3)
                            .level(level)
                            .build();
                case MEDIUM:
                    return Difficulty.builder()
                            .level(level)
                            .name("Medium")
                            .songDuration(1000 * 20)  // 20 seconds
                            .proposeSimilarStyles(false)
                            .variants(4)
                            .build();
                case HARD:
                    return Difficulty.builder()
                            .level(level)
                            .name("Hard")
                            .songDuration(1000 * 10)  // 10 seconds
                            .proposeSimilarStyles(true)
                            .variants(6)
                            .build();
            }
            return null;
        }

    }

}