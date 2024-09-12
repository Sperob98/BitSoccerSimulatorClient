package GUI;

import javax.swing.*;
import java.awt.*;

public class RenderListPlayersAccettati extends JLabel implements ListCellRenderer<String> {

    public RenderListPlayersAccettati(){

        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {

        // Imposta il testo dell'elemento
        setText(value);

        // Cambia il font
        setFont(new Font("Arial", Font.BOLD, 24));
        setForeground(Color.BLACK);   // Colore del testo player client
        setBackground(new Color(255,255,255,50));

        return this;
    }
}
