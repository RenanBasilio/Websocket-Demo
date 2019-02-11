import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint("/upstream")
public class WebSocketServer implements Runnable {

  private Session _session;
  private Thread _thread;

  @OnOpen
  public void onOpen(Session session) {
    System.out.println("Opened connection for client ( sessionId = " + _session.getId() + " )");
    _session = session;
    _thread = new Thread(this);
    _thread.start();
  }

  @OnClose
  public void onClose(Session session) {
    _thread.interrupt();
    try {
      _thread.join();
      System.out.println("Client closed the connection ( sessionId = " + _session.getId() + " )");
    } catch (InterruptedException e) {
      System.err.println("Failed to join client thread: Interrupted");
      e.printStackTrace();
    }
  }

  @OnError
  public void onError(Throwable error) {
    error.printStackTrace();
  }

  @OnMessage
  public String onMessage(String message, Session session) {
    System.out.println("Message from the client: " + message);
    String echoMsg = "Echo from the server : " + message;
    return echoMsg;
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        _session.getBasicRemote().sendText("Hello from the server!");
        Thread.sleep(1000);
      } catch ( InterruptedException e ) {
        break;
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }
}