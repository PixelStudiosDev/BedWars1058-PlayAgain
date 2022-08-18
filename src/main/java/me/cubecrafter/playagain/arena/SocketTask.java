package me.cubecrafter.playagain.arena;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.Getter;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.config.Configuration;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SocketTask implements Runnable {

    private final String host = PlayAgain.getInstance().getBedWars().getConfigs().getMainConfig().getList("bungee-settings.lobby-sockets").get(0).split(":")[0];
    private final Gson gson = new Gson();
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public SocketTask() {
        connect();
        Bukkit.getScheduler().runTaskAsynchronously(PlayAgain.getInstance(), this);
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            if (in.hasNext()) {
                String message = in.next();
                List<ArenaData> data = gson.fromJson(message, new TypeToken<List<ArenaData>>() {}.getType());
                BungeeArenaManager manager = PlayAgain.getInstance().getBungeeManager();
                for (ArenaData cached : new ArrayList<>(manager.getArenas())) {
                    if (data.stream().noneMatch(arena -> arena.getServer().equals(cached.getServer()) && arena.getName().equals(cached.getName()))) {
                        manager.removeCache(cached);
                    }
                }
                for (ArenaData arenaData : data) {
                    if (manager.getCache(arenaData.getServer(), arenaData.getName()) == null) {
                        manager.createCache(arenaData);
                    } else {
                        ArenaData cached = manager.getCache(arenaData.getServer(), arenaData.getName());
                        cached.setState(arenaData.getState());
                        cached.setPlayers(arenaData.getPlayers());
                    }
                }
            } else {
                TextUtil.severe("Lost connection with the lobby server! Reconnecting...");
                connect();
                Bukkit.getScheduler().runTaskAsynchronously(PlayAgain.getInstance(), this);
                break;
            }
        }
    }

    private void connect() {
        try {
            socket = new Socket(host, Configuration.BUNGEE_LOBBY_PORT.getAsInt());
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException ignored) {}
            connect();
            return;
        }
        TextUtil.info("Connection with the lobby server established!");
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    public void close() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
