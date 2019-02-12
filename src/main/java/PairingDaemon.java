import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.websocket.Session;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * WebSocketServer
 */
public class PairingDaemon {

  private static Map<String, WebSocketControlServer> pendingSessions = new HashMap<String, WebSocketControlServer>();
  private static Random random = new SecureRandom();

  /**
   * This method returns the Session Pairing Identifier used to pair the control session with an incoming stream session.
   * @param session The control session for which to generate pairing information.
   * @return The SPID under which the session was registered.
   */
  public static String getSPID(Session session, WebSocketControlServer srv) {
    String spid = session.getId() + session.getProtocolVersion() + session.hashCode() + new BigInteger(130, random).toString(32);
    spid = new String(Base64.encodeBase64(spid.getBytes()));
    pendingSessions.put(spid, srv);
    return spid;
  }

  /**
   * This method retrieves the server associated with an SPID from the wait list.
   * @param spid The Session Pairing Identifier for which to get the Session.
   * @return The Session paired to the spid, or null if none.
   */
  public static WebSocketControlServer getServer(String spid) {
    return pendingSessions.remove(spid);
  }
}