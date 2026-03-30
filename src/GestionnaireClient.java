import  java.lang.Thread;
import  java.lang.Runnable;
import  java.lang.System;
import  java.lang.Override;
import  java.lang.Class;
import  java.util.concurrent.ConcurrentHashMap;
import  java.net.DatagramSocket;




class GestionnaireClient implements Runnable{

    @Override
    public void run(ClientInfo CI, DatagramSocket DS, ConcurrentHashMap CH) { /// code executed by the thread
        // diffuser le message de bienvenue à tous les clients;
        ClientInfo = CI;



    }

    public void DiffuserMessage(String message, ConcurrentHashMap CH) {
        // code pour diffuser le message à tous les clients

}

public class GFG{

    public static void main(String[] args) {
        GestionnaireClient GC = new GestionnaireClient();
        Thread t = new Thread(GC);
        t.start();   // starts a new thread so, calls run()
    }
}