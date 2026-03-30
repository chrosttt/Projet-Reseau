import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.ConcurrentHashMap;

public class GestionnaireClient implements Runnable {

    private ClientInfo clientInfo;
    //private ServeurChatUDP serveur;
    private DatagramSocket socketDediee;
    private ConcurrentHashMap<String, ClientInfo> clients; // corrigé, enlevé = new HashMap()

    public GestionnaireClient(ClientInfo info, ServeurChatUDP serveur, DatagramSocket socket, ConcurrentHashMap<String, ClientInfo> clients) {
        this.clientInfo = info;
        //this.serveur = serveur;
        this.socketDediee = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            // 1 - message de bienvenue
            diffuserMessage("BROADCAST: " + clientInfo.getPseudo() + " a rejoint le chat");

            byte[] buffer = new byte[1024];

            // 2 - boucle de reception
            while (true) {
                DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);
                socketDediee.receive(paquet);
                String message = new String(paquet.getData(), 0, paquet.getLength());

                // 4 - si le client quitte
                if (message.equals("EXIT")) {
                    traiterExit();
                    break;
                }

                // 3 - sinon on diffuse le message
                diffuserMessage("MSG:" + clientInfo.getPseudo() + ": " + message);
            }

        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void diffuserMessage(String message) {
        for (ClientInfo client : clients.values()) {
            if (!client.getPseudo().equals(clientInfo.getPseudo())) {
                try {
                    byte[] data = message.getBytes();
                    DatagramPacket paquet = new DatagramPacket(data, data.length, client.getAdresseIP(), client.getPort());
                    socketDediee.send(paquet);
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        }
    }

    public void traiterExit() {
        clients.remove(clientInfo.getPseudo());
        diffuserMessage("BROADCAST: " + clientInfo.getPseudo() + " a quitté le chat");
        socketDediee.close();
    }
}