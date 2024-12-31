package Main;

import Connection.ConnessioneServerSocket;
import GUI.Home;
import GUI.Partita;

import javax.swing.*;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {

        final ConnessioneServerSocket server = new ConnessioneServerSocket();

        Thread tentativoDiConnessione = new Thread( ()->{

            try{
                server.startConnection("192.168.1.98",8080);

            }catch (IOException ex){
                JOptionPane.showMessageDialog(null,"Connessione al server fallita", "Errore", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });

        tentativoDiConnessione.start();

        //Impostazione limite di tempo per connettersi
        try {
            tentativoDiConnessione.join(30000);  // Aspetta massimo 30 secondi
            if (tentativoDiConnessione.isAlive()) {
                JOptionPane.showMessageDialog(null,"Connessione al server fallita", "Errore", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            System.exit(0);
        }

        //Inserimeno username player
        String username = sendUsernamePlayer(server);

        //Avvia Home
        new Home(server,username);
    }

    private static String sendUsernamePlayer(ConnessioneServerSocket server){

        //Inserimento username player
        String input = null;
        boolean isValid = false;

        // Ciclo finché l'input non è valido (non deve essere solo numeri)
        while(!isValid){

            input = JOptionPane.showInputDialog(null, "Inserisci username player:");

            // Controlla se l'utente ha premuto "Annulla" o ha chiuso la finestra
            if (input == null)
                JOptionPane.showMessageDialog(null, "Hai annullato l'inserimento.");

            // Verifica che l'input non sia composto solo da numeri
            if (input.matches(".*[a-zA-Z]+.*")) {
                // L'input contiene almeno una lettera, è valido
                if(input.length()<=120)
                    isValid = true;
            } else {
                // L'input contiene solo numeri
                JOptionPane.showMessageDialog(null, "L'input non può essere composto solo da numeri e non può essere più lungo di 120 caratteri. Riprova.");
            }

            if(isValid) {

                //Controlla unicità username
                String respose = "ko";
                try {
                    respose = server.sendUsernamePlayer(input.toLowerCase()+'\0');
                } catch (IOException ex) {
                    System.out.println("Errore invio username");
                }

                if(respose.equals("busy")) {

                    isValid = false;
                    JOptionPane.showMessageDialog(null, "Username già in uso al momento, cambia username");
                }

                if(respose.equals("pieno")) {

                    isValid = false;
                    JOptionPane.showMessageDialog(null, "Server al completo, riprovare più tardi");
                }
            }
        }

        return input.toLowerCase();
    }
}