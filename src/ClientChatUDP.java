import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class ClientChatUDP {

    private static class ThreadEcouteClient implements Runnable {
        private final DatagramSocket socket;

        private ThreadEcouteClient(DatagramSocket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            while (!socket.isClosed()) {
                try {
                    byte[] buffer = new byte[1024];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());
                    System.out.println(msg);
                } catch (Exception e) {
                    if (!socket.isClosed()) {
                        System.err.println(e);
                    }
                    break;
                }
            }
        }
    }

    private final String pseudo;
    private final InetAddress adresseServeur;
    private final int portServeur;
    private final DatagramSocket socket;
    private int portDedie;
    private boolean connecte;
    private Thread threadEcoute;

    public ClientChatUDP(String pseudo, InetAddress adresseServeur, int portServeur) throws Exception {
        this.pseudo = pseudo;
        this.adresseServeur = adresseServeur;
        this.portServeur = portServeur;
        this.socket = new DatagramSocket();
    }
    // à changer
    public void connecter() throws Exception {
        if (connecte) {
            return;
        }

        String join = "JOIN:" + pseudo;
        byte[] data = join.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, adresseServeur, portServeur);
        socket.send(packet);

        byte[] buffer = new byte[1024];
        DatagramPacket response = new DatagramPacket(buffer, buffer.length);
        socket.receive(response);

        String msg = new String(response.getData(), 0, response.getLength()).trim();
        if (!msg.startsWith("PORT:")) {
            throw new IllegalStateException("Reponse serveur invalide: " + msg);
        }

        String[] morceaux = msg.split(":", 2);
        if (morceaux.length < 2) {
            throw new IllegalStateException("Port dedie manquant dans la reponse du serveur.");
        }

        portDedie = Integer.parseInt(morceaux[1].trim());
        connecte = true;
        // à changer
        threadEcoute = new Thread(new ThreadEcouteClient(socket), "ClientChatUDP-Ecoute-" + pseudo);
        threadEcoute.setDaemon(true);
        threadEcoute.start();
    }

    public void envoyerMessage(String message) throws Exception {
        if (!connecte) {
            throw new IllegalStateException("Le client n'est pas connecte.");
        }

        byte[] data = message.getBytes();
        DatagramPacket packet = new DatagramPacket(data, data.length, adresseServeur, portDedie);
        socket.send(packet);
    }
    // à changer
    public void deconnecter() {
        connecte = false;
        if (!socket.isClosed()) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        ClientChatUDP client = null;

        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Enter pseudo: ");
            String pseudo = scanner.nextLine().trim();
            if (pseudo.isEmpty()) {
                System.err.println("Le pseudo ne peut pas etre vide.");
                return;
            }

            InetAddress serverAddress = InetAddress.getByName("localhost");
            int portServeur = 9000;

            client = new ClientChatUDP(pseudo, serverAddress, portServeur);
            client.connecter();

            while (true) {
                String message = scanner.nextLine();
                if (message.trim().equalsIgnoreCase("exit")) {
                    client.envoyerMessage("EXIT");
                    break;
                }

                if (!message.isBlank()) {
                    client.envoyerMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (client != null) {
                client.deconnecter();
            }
        }
    }
}
