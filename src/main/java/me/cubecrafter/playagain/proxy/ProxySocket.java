package me.cubecrafter.playagain.proxy;

import lombok.Getter;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.config.Configuration;
import me.cubecrafter.playagain.utils.TextUtil;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ProxySocket {

    @Getter
    private final List<ProxySocketTask> tasks = new ArrayList<>();
    private ServerSocket serverSocket;
    private boolean listen = true;

    public ProxySocket() {
        try {
            serverSocket = new ServerSocket(Configuration.BUNGEE_LOBBY_PORT.getAsInt());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Bukkit.getScheduler().runTaskAsynchronously(PlayAgain.getInstance(), () -> {
            while (listen) {
                try {
                    Socket socket = serverSocket.accept();
                    ProxySocketTask task = new ProxySocketTask(this, socket);
                    tasks.add(task);
                    Bukkit.getScheduler().runTaskAsynchronously(PlayAgain.getInstance(), task);
                    TextUtil.info("Remote game server connected: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void sendMessage(String message) {
        for (ProxySocketTask task : tasks) {
            task.sendMessage(message);
        }
    }

    public void close() {
        listen = false;
        for (ProxySocketTask task : tasks) {
            task.close();
        }
        tasks.clear();
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
