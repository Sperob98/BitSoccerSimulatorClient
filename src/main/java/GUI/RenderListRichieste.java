package GUI;

import javax.swing.*;
import java.awt.*;

public class RenderListRichieste extends JLabel implements ListCellRenderer<String> {

    public RenderListRichieste(){

        setOpaque(false);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

        // Imposta il testo dell'elemento
        setText(value);

        // Cambia il font
        setFont(new Font("Arial", Font.BOLD, 20));

        // Gestisci la selezione dell'elemento con colori semi-trasparenti
        if (isSelected) {
            setBackground(new Color(0, 0, 255, 100));  // Blu semi-trasparente per lo sfondo selezionato
            setForeground(Color.WHITE);  // Testo bianco per il selezionato
        } else {
            setBackground(new Color(0, 0, 0, 0));  // Trasparente totale per gli elementi non selezionati
            setForeground(Color.BLACK);  // Testo nero per gli elementi non selezionati
        }

        setOpaque(isSelected);  // Rendi opaco solo quando l'elemento è selezionato

        return this;
    }
}
