import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

public class ServeurChatUDP {
    private int portPrincipal;
    private DatagramSocket socketPrincipale;
    private Map<String, ClientInfo> clients;

    public ServeurChatUDP(int portPrincipal) {
        this.portPrincipal = portPrincipal;
        this.clients = new HashMap<>();
    }

    public DatagramSocket allouerSocket() throws Exception {
        return new DatagramSocket(0);
    }

    public void ajouterClient(ClientInfo info) {
        clients.put(info.getPseudo(), info);
    }

    public void supprimerClient(String pseudo) {
        clients.remove(pseudo);
    }

    public void diffuser(String message, String expediteur) {
        for (ClientInfo client : clients.values()) {
            if (!client.getPseudo().equals(expediteur)) {
                try {
                    byte[] data = message.getBytes();
                    DatagramPacket paquet = new DatagramPacket(data, data.length, client.getAdresseIP(), client.getPort());
                    socketPrincipale.send(paquet);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
    }

    public static void main(String[] args) { // Méthode principale
        try {
            // 1 - Création du canal
            DatagramSocket socketServeur = new DatagramSocket(null);
            // 2 - Réservation du port
            InetSocketAddress adresse = new InetSocketAddress("localhost", 9000);
            socketServeur.bind(adresse);
            byte[] recues = new byte[1024]; // tampon d'emission
            byte[] envoyees; // tampon de reception
            Map<String, ClientInfo> clients = new HashMap<>();
            while (true) {
                // 3 - Recevoir
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Reçu: " + message);
                // 4 - Émettre
                InetAddress adrClient = paquetRecu.getAddress();
                int prtClient = paquetRecu.getPort();
                if (message.startsWith("JOIN:")) {
                    String pseudo = message.substring(5).trim();
                    // a)
                    DatagramSocket socketDediee = new DatagramSocket(0);
                    int portDedie = socketDediee.getLocalPort();
                    // b)
                    String reponse = "PORT:" + portDedie;
                    envoyees = reponse.getBytes();
                    DatagramPacket paquetEnvoye = new DatagramPacket(envoyees, envoyees.length, adrClient, prtClient);
                    socketServeur.send(paquetEnvoye);
                    // c)
                    ClientInfo info = new ClientInfo(pseudo, adrClient, portDedie);
                    clients.put(pseudo, info);
                    new Thread(new GestionnaireClient(socketDediee, pseudo, clients)).start();
                }
            }
            // 5 - Libérer le canal
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}