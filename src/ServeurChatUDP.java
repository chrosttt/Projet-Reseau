import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ServeurChatUDP {
    public static void main(String[] args) {
        try {
            // 1 - Création du canal
            DatagramSocket socketServeur = new DatagramSocket(null);
            // 2 - Réservation du port
            InetSocketAddress adresse = new InetSocketAddress("localhost", 9000);
            socketServeur.bind(adresse);
            byte[] recues = new byte[1024];
            byte[] envoyees;
            ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>(); // corrigé
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
                    ClientInfo info = new ClientInfo(pseudo, adrClient, prtClient);
                    clients.put(pseudo, info);
                    new Thread(new GestionnaireClient(info, null, socketDediee, clients)).start();
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
