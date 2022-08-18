package me.cubecrafter.playagain.arena;

import lombok.Data;

@Data
public class ArenaData {

    private final String server;
    private final String name;
    private final String id;
    private final String group;
    private final int maxPlayers;
    private final int maxInTeam;
    private String state;
    private int players;

}
