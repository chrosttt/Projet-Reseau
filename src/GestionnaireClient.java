import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

public class GestionnaireClient implements Runnable {

    private static final int TIMEOUT_MS = 60_000;

    private final ClientInfo clientInfo;
    private final DatagramSocket socketDediee;
    private final ConcurrentHashMap<String, ClientInfo> clients;

    public GestionnaireClient(
            ClientInfo info,
            DatagramSocket socket,
            ConcurrentHashMap<String, ClientInfo> clients
    ) {
        this.clientInfo = info;
        this.socketDediee = socket;
        this.clients = clients;
    }

    @Override
    public void run() {
        try {
            System.out.println("GestionnaireClient démarré pour " + clientInfo.getPseudo()
                    + " sur port dédié : " + socketDediee.getLocalPort()
                    + " client port : " + clientInfo.getPort());
            socketDediee.setSoTimeout(TIMEOUT_MS);
            diffuserMessage("BROADCAST: " + clientInfo.getPseudo() + " a rejoint le chat");
            while (true) {
                byte[] buffer = new byte[1024];
                DatagramPacket paquet = new DatagramPacket(buffer, buffer.length);

                try {
                    socketDediee.receive(paquet);
                } catch (SocketTimeoutException e) {
                    traiterTimeout();
                    break;
                }

                String message = new String(paquet.getData(), 0, paquet.getLength()).trim();
                if (message.isEmpty()) {
                    continue;
                }

                if (message.equalsIgnoreCase("EXIT")) {
                    traiterExit();
                    break;
                }

                if (message.equalsIgnoreCase("/liste")) {
                    envoyerListeClients();
                    continue;
                }

                if (message.startsWith("/mp ")) {
                    traiterMessagePrive(message);
                    continue;
                }

                diffuserMessage("MSG:" + clientInfo.getPseudo() + ": " + message);
            }
        } catch (Exception e) {
            if (!socketDediee.isClosed()) {
                System.err.println(e);
            }
        }
    }

    private void envoyerMessage(ClientInfo destinataire, String message) {
        try {
            byte[] data = message.getBytes();
            DatagramPacket paquet = new DatagramPacket(
                    data,
                    data.length,
                    destinataire.getAdresseIP(),
                    destinataire.getPort()
            );
            socketDediee.send(paquet);
        } catch (Exception e) {
            System.err.println(e);
        }
    }

    public void diffuserMessage(String message) {
        for (ClientInfo client : clients.values()) {
            if (!client.getPseudo().equals(clientInfo.getPseudo())) {
                envoyerMessage(client, message);
            }
        }
    }

    private void envoyerListeClients() {
        ArrayList<String> pseudos = new ArrayList<>(clients.keySet());
        Collections.sort(pseudos);
        envoyerMessage(clientInfo, "LISTE: " + String.join(", ", pseudos));
    }

    private void traiterMessagePrive(String message) {
        String[] morceaux = message.split("\\s+", 3);
        if (morceaux.length < 3 || morceaux[2].trim().isEmpty()) {
            envoyerMessage(clientInfo, "Usage: /mp <pseudo> <message>");
            return;
        }

        ClientInfo destinataire = clients.get(morceaux[1]);
        if (destinataire == null) {
            envoyerMessage(clientInfo, "Utilisateur inconnu");
            return;
        }

        String messagePrive = "MP de " + clientInfo.getPseudo() + ": " + morceaux[2].trim();
        envoyerMessage(destinataire, messagePrive);
    }

    public void traiterExit() {
        clients.remove(clientInfo.getPseudo());
        diffuserMessage("BROADCAST: " + clientInfo.getPseudo() + " a quitte le chat");
        socketDediee.close();
    }

    private void traiterTimeout() {
        envoyerMessage(clientInfo, "TIMEOUT");
        clients.remove(clientInfo.getPseudo());
        diffuserMessage("BROADCAST: " + clientInfo.getPseudo() + " a quitte le chat");
        socketDediee.close();
    }
}
