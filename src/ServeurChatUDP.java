import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Map;

public class ServeurChatUDP {
    private int portPrincipal;
    private DatagramSocket socketPrincipale;
    private Map<String, ClientInfo> clients;

    public ServeurChatUDP( int portPrincipal){
        this.portPrincipal = portPrincipal;
    }

    public static void main(String[] args) {// Méthode principale
        this.PortPrincipal= port; // Port d'écoute du serveur
        this.SocketPrincipale = DatagramSocket;
        this.map<String, clientInfo>;
        } catch (Exception e) {
            System.err.println(e);

        }
    }
}