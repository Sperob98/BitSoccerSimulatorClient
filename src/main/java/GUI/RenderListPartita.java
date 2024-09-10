package GUI;

import javax.swing.*;
import java.awt.*;

public class RenderListPartita extends JLabel implements ListCellRenderer<String> {

    private String turnoPlayer;

    private String playerClient;

    private String playerInfortunato;

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
        if ( (value.equals(turnoPlayer)) || (value.contains(" (capitano)") && value.equals(turnoPlayer + (" (capitano)"))) ) {
            setBackground(Color.GREEN);  // Colore di sfondo per il giocatore di turno
            setForeground(Color.BLACK);   // Colore del testo per il giocatore di turno
        } else if( (value.equals(playerClient)) || (value.contains(" (capitano)") && value.equals(playerClient + " (capitano)")) ) {
            setBackground(Color.gray);   // Colore di sfondo del player client
            setForeground(Color.BLACK);   // Colore del testo player client
        } else{
            setBackground(Color.WHITE);   // Colore di sfondo normale
            setForeground(Color.BLACK);   // Colore del testo normale
        }

        if( (value.equals(playerInfortunato)) || (value.contains(" (capitano)") && value.equals(playerInfortunato + ( "capitano"))) ){
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

    public void setPlayerInfortunato(String playerInfortunato) {
        this.playerInfortunato = playerInfortunato;
    }
}
