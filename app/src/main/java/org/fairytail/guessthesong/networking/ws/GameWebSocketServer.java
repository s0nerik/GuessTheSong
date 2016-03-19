//package org.fairytail.guessthesong.networking.ws;
//
//import com.google.gson.Gson;
//import com.squareup.otto.Bus;
//
//import org.fairytail.guessthesong.App;
//import org.fairytail.guessthesong.dagger.Injector;
//import org.fairytail.guessthesong.events.networking.AllClientsStartedPlaybackEvent;
//import org.fairytail.guessthesong.events.networking.ClientReadyEvent;
//import org.fairytail.guessthesong.events.networking.ClientStateChangedEvent;
//import org.fairytail.guessthesong.events.networking.PlayersReadyToStartEvent;
//import org.fairytail.guessthesong.model.game.Game;
//import org.fairytail.guessthesong.networking.entities.ClientInfo;
//import org.fairytail.guessthesong.networking.entities.SocketMessage;
//import org.fairytail.guessthesong.player.Player;
//import org.java_websocket.WebSocket;
//import org.java_websocket.handshake.ClientHandshake;
//import org.java_websocket.server.WebSocketServer;
//
//import java.net.InetSocketAddress;
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//import javax.inject.Inject;
//
//import ru.noties.debug.Debug;
//
//import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.GAME;
//import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.PLAYBACK_START;
//import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.PREPARE_AND_SEEK;
//import static org.fairytail.guessthesong.networking.entities.SocketMessage.Message.START_GAME;
//import static org.fairytail.guessthesong.networking.entities.SocketMessage.Type.REQUEST;
//import static org.fairytail.guessthesong.networking.entities.SocketMessage.Type.OFFER;
//
//public class GameWebSocketServer extends WebSocketServer {
//
//    private static final int TIMEOUT = 10 * 1000; // 10 seconds
//
//    private Set<WebSocket> readyToStart = new HashSet<>();
//    private Set<WebSocket> readyToStartGame = new HashSet<>();
//
//    private Map<WebSocket, ClientInfo> clientInfoMap = new HashMap<>();
//
//    private long lastMessageTime = -1;
//
//    @Inject
//    Gson gson;
//
//    @Inject
//    Bus bus;
//
//    @Inject
//    Player player;
//
//    private Game game;
//    private int currentQuizIndex = 0;
//
//    public GameWebSocketServer(InetSocketAddress address) {
//        super(address);
//        Injector.inject(this);
////        bus.register(this);
//    }
//
//    public void initWithGame(Game game) {
//        this.game = game;
//    }
//
//    @Override
//    public void onOpen(WebSocket conn, ClientHandshake handshake) {
//        Debug.d("GameWebSocketServer: New connection");
//        Debug.d("GameWebSocketServer: connections.size() = " + connections().size());
//        conn.send(gson.toJson(new SocketMessage(OFFER, GAME, gson.toJson(game))));
////        conn.send(gson.toJson(new SocketMessage(SocketMessage.Type.REQUEST, SocketMessage.Message.CLIENT_INFO)));
//    }
//
//    @Override
//    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
//        Debug.d("GameWebSocketServer: Close connection");
//        Debug.d("GameWebSocketServer: connections.size() = "+connections().size());
//        bus.post(new ClientStateChangedEvent(clientInfoMap.get(conn), ClientStateChangedEvent.State.DISCONNECTED));
//    }
//
//    @Override
//    public void onMessage(WebSocket conn, String message) {
//        Debug.d("WebSocket message: \"" + message + "\"");
//
//        SocketMessage socketMessage = gson.fromJson(message, SocketMessage.class);
//        String body = socketMessage.body;
//
//        if (socketMessage.type == REQUEST) {
//            Debug.e("Can't process message: " + socketMessage.message.name());
//        } else if (socketMessage.type == OFFER) { // Client returned some status (Assume OK)
////            if (socketMessage.status == OK) {
////                switch (socketMessage.message) {
////                    case GAME:
////                        processReadyToStart(conn, START_GAME);
////                        break;
////                    case START_GAME:
////                        conn.send(gson.toJson(new SocketMessage(OFFER, PREPARE_AND_SEEK, gson.toJson(new StartFromEntity(0, game.getQuizzes().get(0).getCorrectSong())))));
////                        break;
////                    case PREPARE_AND_SEEK:
////                        processReadyToStart(conn, PLAYBACK_START);
////                        break;
////                    default:
////                        Debug.e("Unknown message: "+socketMessage.message.name());
////                }
////            }
//            switch (socketMessage.message) {
//                case CLIENT_INFO:
//                    Debug.d("webSocket: CLIENT_INFO");
//                    processClientInfo(conn, gson.fromJson(body, ClientInfo.class));
//                    break;
//                case GAME:
//                    Debug.d("webSocket: GAME");
//                    processReadyToStartGame(conn);
//                    break;
//                case START_GAME:
//                    Debug.d("webSocket: START_GAME");
//                    processReadyToPrepare(conn);
//                    break;
//                case PREPARE_AND_SEEK:
//                    Debug.d("webSocket: PREPARE_AND_SEEK");
//                    processReadyToStart(conn);
//                    break;
//                default:
//                    Debug.e("Unknown message: "+socketMessage.message.name());
//            }
//        }
//
//        lastMessageTime = System.currentTimeMillis();
//    }
//
//    @Override
//    public void onError(WebSocket conn, Exception ex) {
//        Debug.e("GameWebSocketServer onError:\n", ex);
//    }
//
//    public void startMultiplayerGame() {
//        for (WebSocket socket : readyToStartGame) {
//            if (socket.isClosed()) {
//                readyToStartGame.remove(socket);
//            }
//        }
//        if(!readyToStartGame.isEmpty() && readyToStartGame.size() == connections().size()) {
//            for (WebSocket socket : connections()) {
//                socket.send(gson.toJson(new SocketMessage(OFFER, START_GAME)));
//            }
//            readyToStart.clear();
//        } else {
//            Debug.e("Not all players are ready to start game.");
//        }
//    }
//
//    public void proceedToNextQuiz() {
//        int quizzesSize = game.getQuizzes().size();
//
//        if (currentQuizIndex + 1 < quizzesSize) {
//            currentQuizIndex++;
//        } else {
//            currentQuizIndex = 0;
//        }
//
//        for (WebSocket s : readyToStartGame) {
//            processReadyToPrepare(s);
//        }
//    }
//
//    private void processReadyToStartGame(WebSocket conn) {
//        readyToStartGame.add(conn);
//
//        if (readyToStartGame.size() == connections().size()) {
//            App.bus.post(new PlayersReadyToStartEvent());
//        }
//    }
//
//    private void processReadyToPrepare(WebSocket conn) {
//        player.prepareAndSeekTo(game.getQuizzes().get(currentQuizIndex).getCorrectSong(), 40 * 1000, null);
//        conn.send(gson.toJson(
//                new SocketMessage(OFFER, PREPARE_AND_SEEK,
//                                  gson.toJson(new StartFromEntity(40 * 1000, 0))
//                )
//        ));
//    }
//
//    private void processReadyToStart(WebSocket conn) {
//        Debug.d("processReadyToStart");
//        Debug.d("Ready: "+ readyToStart);
//
//        readyToStart.add(conn);
//
//        bus.post(new ClientReadyEvent(conn));
//
//        if(readyToStart.size() == connections().size()) {
//            player.start();
//            game.getQuizzes().get(currentQuizIndex).start();
//            for (WebSocket socket : connections()) {
//                socket.send(gson.toJson(new SocketMessage(OFFER, PLAYBACK_START)));
//            }
//
//            bus.post(new AllClientsStartedPlaybackEvent());
//            readyToStart.clear();
//        }
//    }
//
//    private void processClientInfo(WebSocket conn, ClientInfo info) {
//        clientInfoMap.put(conn, info);
//        bus.post(new ClientStateChangedEvent(info, ClientStateChangedEvent.State.CONNECTED));
//    }
//
//}
