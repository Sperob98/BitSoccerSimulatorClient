package GUI;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class RenderListPartita extends JLabel implements ListCellRenderer<String> {

    private String turnoPlayer;

    private String playerClient;

    private ArrayList<String> playersInfortunato = new ArrayList<>();

    private ArrayList<String> playersPenalizzato = new ArrayList<>();

    public RenderListPartita(){

        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

        // Imposta il testo dell'elemento
        setText(value);

        // Cambia il font
        setFont(new Font("Arial", Font.PLAIN, 18));

        // Controlla se l'elemento è il giocatore di turno e cambia il colore di sfondo
        if ( (value.equals(turnoPlayer)) || (value.equals(turnoPlayer + (" (capitano)"))) ) {
            setBackground(Color.GREEN);  // Colore di sfondo per il giocatore di turno
            setForeground(Color.BLACK);   // Colore del testo per il giocatore di turno
        } else if( (value.equals(playerClient)) || (value.equals(playerClient + " (capitano)")) ) {
            setBackground(Color.gray);   // Colore di sfondo del player client
            setForeground(Color.BLACK);   // Colore del testo player client
        } else{
            setBackground(Color.WHITE);   // Colore di sfondo normale
            setForeground(Color.BLACK);   // Colore del testo normale
        }

        if( (playersInfortunato.contains(value)) || (contieneCapitano(playersInfortunato,value)) ){
            setBackground(Color.RED);   // Colore di sfondo del player infortunato
            setForeground(Color.BLACK);   // Colore del testo player infortunato
        }

        return this;
    }

    public void setTurnoPlayer(String turnoPlayer) {

        this.turnoPlayer = turnoPlayer;
    }

    public void setPlayerClient(String playerClient) {
        this.playerClient = playerClient;
    }

    public void addPlayerInfortunato(String playerInfortunato) {

        this.playersInfortunato.add(playerInfortunato);
    }

    public void removePlayerInfortunato(String playerInfortunato){

        this.playersInfortunato.remove(playerInfortunato);
    }

    public void addPlayerPenalizzato(String playerPenalizzato){

        this.playersPenalizzato.add(playerPenalizzato);
    }

    public void removePlayerPenalizzato(String playerPenalizzato){

        this.playersPenalizzato.remove(playerPenalizzato);
    }

    private boolean contieneCapitano(ArrayList<String> list, String value){

        if(value.contains(" (capitano)")){

            int index = value.indexOf(" (capitano)");
            String player = value.substring(0,index).trim();
            if(list.contains(player)) return true;
        }

        return false;
    }
}
