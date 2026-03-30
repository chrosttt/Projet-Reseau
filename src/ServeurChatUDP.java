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
        try {
// 1 - Création du canal
            DatagramSocket socketServeur = new DatagramSocket(null);
// 2 - Réservation du port
            InetSocketAddress adresse = new InetSocketAddress("localhost", 9000);
            socketServeur.bind(adresse);
            byte[] recues = new byte[1024]; // tampon d'emission
            byte[] envoyees; // tampon de reception
            while (true) {
// 3 - Recevoir
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Reçu: " + message);
// 4 - Émettre
                InetAddress adrClient = paquetRecu.getAddress();
                int prtClient = paquetRecu.getPort();
                String reponse = "Accusé de réception";
                envoyees = reponse.getBytes();
                DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adrClient, prtClient);
                socketServeur.send(paquetEnvoye);
            }
// 5 - Libérer le canal
        } catch (Exception e) {
            System.err.println(e);

        }
    }
}