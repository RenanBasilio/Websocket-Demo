import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnOpen;

import java.io.IOException;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;

@ServerEndpoint("/stream")
public class WebSocketStreamServer implements Runnable {

  private WebSocketControlServer _control;
  private Session _session;
  private Thread _thread;
  private String _spid;

  @OnOpen
  public void onOpen(Session session) {
    _spid = session.getRequestParameterMap().get("spid").get(0);
    _control = PairingDaemon.getServer(_spid);
    if (_control != null) {
      _session = session;
      _control.setStream(this);
      _thread = new Thread(this);
      _thread.start();
      System.out.println("Successfully opened paired connection ( control: " + _control.getSessionId() + ", stream: " + _session.getId() + " )");
    } else {
      try {
        session.close();
        System.out.println("Failed to open paired connection: No pending control connection with SPID=" + _spid);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @OnClose
  public void onClose(Session session) {
    if (_thread != null && _thread.isAlive()) {
      _thread.interrupt();
      try {
        _thread.join();
        System.out.println("Client closed stream connection.");
      } catch (InterruptedException e) {
        System.err.println("Failed to join client thread: Interrupted");
        e.printStackTrace();
      }
    }
  }

  @OnError
  public void onError(Throwable error) {
    error.printStackTrace();
  }

  @Override
  public void run() {
    while (!Thread.interrupted()) {
      try {
        for( int i = 0; i < 100; i++ ) {
          _session.getBasicRemote().sendText(Double.toString(i/100.0));
          Thread.sleep(50);
        }
      } catch ( InterruptedException e ) {
        break;
      } catch ( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  public void close() {
    try {
      _session.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}