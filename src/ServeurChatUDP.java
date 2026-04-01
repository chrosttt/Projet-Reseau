import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class ServeurChatUDP {

    private DatagramSocket socketServeur;
    private final ConcurrentHashMap<String, ClientInfo> clients = new ConcurrentHashMap<>();

    public void demarrer() {
        try {
            socketServeur = new DatagramSocket(null);
            socketServeur.bind(new InetSocketAddress("localhost", 9000));
            byte[] recues = new byte[1024];

            while (true) {
                DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
                socketServeur.receive(paquetRecu);
                String message = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
                System.out.println("Reçu: " + message);

                InetAddress adrClient = paquetRecu.getAddress();
                int prtClient = paquetRecu.getPort();

                if (message.startsWith("JOIN:")) {
                    String pseudo = message.substring(5).trim();
                    ClientInfo info = enregistrerClient(pseudo, adrClient, prtClient);
                    allouerSocketEtRepondre(info);
                }
            }
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    private ClientInfo enregistrerClient(String pseudo, InetAddress adresse, int port) {
        ClientInfo info = new ClientInfo(pseudo, adresse, port);
        clients.put(pseudo, info);
        return info;
    }

    private void allouerSocketEtRepondre(ClientInfo client) {
        try {
            DatagramSocket socketDediee = new DatagramSocket(0);

            String reponse = "PORT:" + socketDediee.getLocalPort();
            byte[] envoyees = reponse.getBytes();
            DatagramPacket paquetEnvoye = new DatagramPacket(
                    envoyees,
                    envoyees.length,
                    client.getAdresseIP(),
                    client.getPort()
            );
            socketServeur.send(paquetEnvoye);

            new Thread(new GestionnaireClient(client, socketDediee, clients)).start();
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public static void main(String[] args) {
        new ServeurChatUDP().demarrer();
    }
}