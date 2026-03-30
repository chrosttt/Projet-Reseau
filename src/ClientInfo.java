//encapsule les informations d’un client (pseudo, adresse IP, port)
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ClientInfo {
    private String pseudo;
    private InetAddress adresseIP;
    private int port;
   //  private DatagramSocket socket;

    public ClientInfo(String pseudo, InetAddress adresseIP, int port){
        ConcurrentHashMap<Integer, String> map = new ConcurrentHashMap<>();
        this.port = port;
        this.adresseIP = adresseIP;
        this.pseudo = pseudo;

    }
    public String getPseudo (){
        return pseudo;
    }
    public InetAddress getAdresseIP(){
        return adresseIP;
    }
    public int getPort (){
        return port;
    }
   // public DatagramSocket getSocket() {
     //   return socket;
    // }
}