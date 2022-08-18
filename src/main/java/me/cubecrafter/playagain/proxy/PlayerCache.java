package me.cubecrafter.playagain.proxy;

import lombok.Data;

import java.util.UUID;

@Data
public class PlayerCache {

    private final UUID uuid;
    private final String server;
    private final long time;

    public boolean isExpired() {
        return System.currentTimeMillis() - time > 10000;
    }

}
