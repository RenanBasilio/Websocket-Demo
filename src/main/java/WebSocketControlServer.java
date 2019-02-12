import java.io.IOException;
import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 * wsControlServer
 */
@ServerEndpoint("/control")
public class WebSocketControlServer {

  private WebSocketStreamServer _stream;
  private Session _session;
  private String _spid;

  public String getSessionId() {
    return _session.getId();
  }

  @OnOpen
  public void onOpen(Session session) {
    _session = session;
    _spid = PairingDaemon.getSPID(session, this);
    try {
      String pair_message = Json.createObjectBuilder().add("type", "pairing").add("spid", _spid).build().toString();
      session.getBasicRemote().sendText(pair_message);
      System.out.println("Sent SPID to client for pairing: " + _spid);
    } catch (IOException e) {
      PairingDaemon.getServer(_spid);
    }
  }

  @OnClose
  public void onClose(Session session) {
    if (_stream != null) _stream.close();
    else PairingDaemon.getServer(_spid);
  }

  @OnError
  public void onError(Throwable error) {
    if (_stream != null) _stream.close();
    else PairingDaemon.getServer(_spid);
    error.printStackTrace();
  }

  @OnMessage
  public void onMessage(String message, Session session) {
    JsonObject object = Json.createParser(new StringReader(message)).getObject();
    if (object.containsKey("type")) {

    }
  }

  public void setStream(WebSocketStreamServer stream) {
    _stream = stream;
  }
}