//encapsule les informations d’un client connecté (pseudo, adresse IP, port)
import java.net.InetAddress;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class ClientInfo {
    private String pseudo;
    private InetAddress adresseIP;
    private int port;


    public ClientInfo(String pseudo, InetAddress adresseIP, int port){
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
}