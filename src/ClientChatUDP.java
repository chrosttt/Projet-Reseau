
// gère la saisie clavier et crée un thread d’écoute pour la réception
public class ClientChatUDP {
    public static void main(String[] args){// Méthode principale
        try {
            // 1 - Création du canal
            DatagramSocket socketClient = new DatagramSocket();
            InetAddress adresseClient = InetAddress.getByName("localhost");
            byte[] envoyees; // tampon d'émission
            byte[] recues = new byte[1024]; // tampon de réception
            // 2 - Émettre
            String message = "Salve !";
            envoyees = message.getBytes();
            DatagramPacket messageEnvoye = new DatagramPacket(envoyees, envoyees.length, adresseClient, 6666);
            socketClient.send(messageEnvoye);
            // 3 - Recevoir
            DatagramPacket paquetRecu = new DatagramPacket(recues, recues.length);
            socketClient.receive(paquetRecu);
            String reponse = new String(paquetRecu.getData(), 0, paquetRecu.getLength());
            System.out.println("Depuis le serveur: " + reponse);
            // 4 - Libérer le canal
            socketClient.close();
        } catch (Exception e) {
            System.err.println(e);
        }
    }
    class MonThread extends Thread {
        @Override
        public void run() { /* code exécuté dans le thread */ }
    }
new MonThread().start();
}






 // démarrage