package org.fairytail.guessthesong.networking.entities;

import com.bluelinelabs.logansquare.annotation.JsonField;
import com.bluelinelabs.logansquare.annotation.JsonObject;
import com.peak.salut.SalutDevice;

import lombok.Getter;
import lombok.Setter;

@JsonObject
public class PlayerInfo {
    @JsonField
    public String id;

    @Getter
    @Setter
    private SalutDevice device;
}