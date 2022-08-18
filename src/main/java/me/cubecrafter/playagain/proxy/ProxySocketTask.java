package me.cubecrafter.playagain.proxy;

import com.andrei1058.bedwars.proxy.arenamanager.ArenaManager;
import com.andrei1058.bedwars.proxy.socketmanager.ArenaSocketTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import me.cubecrafter.playagain.PlayAgain;
import me.cubecrafter.playagain.utils.TextUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.UUID;

public class ProxySocketTask implements Runnable {

    private final ProxySocket proxySocket;
    private final Socket socket;
    private Scanner in;
    private PrintWriter out;
    private final Gson gson = new Gson();

    public ProxySocketTask(ProxySocket proxySocket, Socket socket) {
        this.socket = socket;
        this.proxySocket = proxySocket;
        try {
            in = new Scanner(socket.getInputStream());
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (socket.isConnected()) {
            if (in.hasNext()) {
                String message = in.next();
                if (message.isEmpty()) continue;
                System.out.println("MESSAGE RECEIVED: " + message);
                List<Map<String, String>> data = gson.fromJson(message, new TypeToken<List<Map<String, String>>>() {}.getType());
                String server = data.get(0).get("server");
                ArenaSocketTask socket = ArenaManager.getSocketByServer(server);
                for (Map<String, String> map : data) {
                    JsonObject msg = new JsonObject();
                    msg.addProperty("type", "PLD");
                    msg.addProperty("uuid", map.get("uuid"));
                    msg.addProperty("lang_iso", map.get("iso"));
                    msg.addProperty("target", map.get("party") == null ? "" : map.get("party"));
                    msg.addProperty("arena_identifier", map.get("id"));
                    socket.getOut().println(msg);
                    if (Boolean.parseBoolean(map.get("local"))) {
                        PlayAgain.getInstance().getProxyListener().addCache(UUID.fromString(map.get("uuid")), server);
                    }
                }
            } else {
                proxySocket.getTasks().remove(this);
                TextUtil.info("Remote game server disconnected: " + socket.getInetAddress().getHostAddress() + ":" + socket.getPort());
                break;
            }
        }
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
