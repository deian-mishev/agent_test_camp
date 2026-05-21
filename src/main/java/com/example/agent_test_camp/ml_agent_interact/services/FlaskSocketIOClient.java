package com.example.agent_test_camp.ml_agent_interact.services;

import com.example.agent_test_camp.ml_agent_interact.configuration.ProjectProperties;
import io.socket.client.IO;
import io.socket.client.Socket;

import java.net.URISyntaxException;
import java.util.List;
import java.util.function.Consumer;

public class FlaskSocketIOClient {

  private Socket socket;
  private final ProjectProperties.AgentProperties agentProperties;
  private final Consumer<String> frameListener;
  private final Consumer<String> onEpisodeEnd;
  private boolean connected = false;

  public FlaskSocketIOClient(
      Consumer<String> frameListener,
      Consumer<String> onEpisodeEnd,
      ProjectProperties.AgentProperties agentProperties) {
    this.frameListener = frameListener;
    this.agentProperties = agentProperties;
    this.onEpisodeEnd = onEpisodeEnd;
  }

  public synchronized void connect(String env, String player) throws URISyntaxException {
    IO.Options options = new IO.Options();
    options.forceNew = true;
    options.reconnection = true;
    options.query = "env=" + env + "&ai_player=" + player;

    socket = IO.socket(agentProperties.getUrl(), options);

    socket.on(
        Socket.EVENT_CONNECT,
        args -> {
          connected = true;
          System.out.println("Connected to Flask Socket.IO server");
        });

    socket.on(
        "frame",
        args -> {
          if (args.length > 0 && args[0] instanceof String) {
            frameListener.accept((String) args[0]);
          }
        });

    socket.on(
        "episode_end",
        args -> {
          System.out.println("Episode ended.");
          if (onEpisodeEnd != null) {
            onEpisodeEnd.accept("Episode ended");
          }
          disconnect();
        });

    socket.on(
        Socket.EVENT_DISCONNECT,
        args -> {
          connected = false;
          System.out.println("Disconnected from Flask Socket.IO server");
        });

    socket.connect();
  }

  public void sendInput(List<String> keys) {
    if (socket != null && socket.connected()) {
      socket.emit("input", keys);
    } else {
      System.err.println("Socket.IO client not connected. Not sending input.");
    }
  }

  public void disconnect() {
    if (socket != null) {
      socket.off();
      socket.disconnect();
      socket.close();
      connected = false;
    }
  }

  public boolean isConnected() {
    return connected;
  }
}
