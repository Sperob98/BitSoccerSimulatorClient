package GUI;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;

public class TabellaSquadreInCorso extends JTable {

    // Renderer personalizzato per evidenziare meglio la riga selezionata
    DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (isSelected) {
                cell.setBackground(Color.GREEN);
                cell.setForeground(Color.WHITE);
            } else {
                cell.setBackground(Color.WHITE);
                cell.setForeground(Color.BLACK);
            }
            return cell;
        }
    };

    public TabellaSquadreInCorso(){

        //Font e Dimensione delle celle
        setFont(new Font("Verdana", Font.PLAIN, 18));
        setRowHeight(25);
        setBackground(Color.WHITE);
        setForeground(Color.BLACK);

        // Cambia il font dell'header della tabella
        JTableHeader header = getTableHeader();
        header.setFont(new Font("Verdana", Font.PLAIN, 10));
        header.setPreferredSize(new Dimension(header.getWidth(), 35)); // Altezza dell'header

        //Disabilita la selezione delle celle e abilita solo quella delle righe
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        setOpaque(false);
        ((JComponent)getDefaultRenderer(Object.class)).setOpaque(false);
    }

    public void setCustomRenderer(){

        // Applica il renderer personalizzato a tutte le colonne
        for (int i = 0; i < getColumnCount(); i++) {
            getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }
    }
}
