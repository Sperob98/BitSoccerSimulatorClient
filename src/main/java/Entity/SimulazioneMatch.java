package Entity;

import GUI.RenderListPartita;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class SimulazioneMatch {

    private boolean avvioPartita;
    private JTextPane cronaca;

    private JList<String> listPlayersA;
    private DefaultListModel<String> modelListPlayerA;

    private  JList<String> listPlayersB;
    private DefaultListModel<String> modelListPlayerB;

    private RenderListPartita renderListA;

    private RenderListPartita renderListB;

    private JLabel scoreA;

    private JLabel scoreB;

    private JLabel time;

    private int countA = 0;
    private int countB = 0;


    public SimulazioneMatch(){

        avvioPartita = false;

        cronaca = new JTextPane();
        cronaca.setEditable(false);

        renderListA = new RenderListPartita();
        renderListB = new RenderListPartita();

        listPlayersA = new JList<>();
        modelListPlayerA = new DefaultListModel<>();
        listPlayersA.setModel(modelListPlayerA);
        listPlayersA.setCellRenderer(renderListA);

        listPlayersB = new JList<>();
        modelListPlayerB = new DefaultListModel<>();
        listPlayersB.setModel(modelListPlayerB);
        listPlayersB.setCellRenderer(renderListB);

        scoreA = new JLabel("0");
        scoreB = new JLabel("0");

        time = new JLabel();
    }

    public boolean isAvvioPartita() {
        return avvioPartita;
    }
    public JTextPane getCronaca() {
        return cronaca;
    }

    public JList<String> getListPlayersA() {
        return listPlayersA;
    }

    public JList<String> getListPlayersB() {
        return listPlayersB;
    }

    public DefaultListModel<String> getModelListPlayerA() {
        return modelListPlayerA;
    }

    public DefaultListModel<String> getModelListPlayerB() {
        return modelListPlayerB;
    }

    public JLabel getScoreA() {
        return scoreA;
    }

    public JLabel getScoreB() {
        return scoreB;
    }

    public JLabel getTime() {
        return time;
    }

    public void setAvvioPartita(boolean avvioPartita) {
        this.avvioPartita = avvioPartita;
    }

    public void setPlayerAttualeRender(String player){

        renderListA.setTurnoPlayer(player);

        renderListB.setTurnoPlayer(player);
    }

    public void setPlayerClient(String player){

        renderListA.setPlayerClient(player);

        renderListB.setPlayerClient(player);
    }

    public void addInfortunio(String player){

        renderListA.addPlayerInfortunato(player);

        renderListB.addPlayerInfortunato(player);
    }

    public void removeInfortunio(String player){

        renderListA.removePlayerInfortunato(player);

        renderListB.removePlayerInfortunato(player);
    }

    public void addPenalizzazione(String player){

        renderListA.addPlayerPenalizzato(player);

        renderListB.addPlayerPenalizzato(player);
    }

    public void removePenalizzazione(String player){

        renderListA.removePlayerPenalizzato(player);

        renderListB.removePlayerPenalizzato(player);
    }

    public void incrementaScoreA(){

        countA++;
        scoreA.setText(String.valueOf(countA));

        scoreA.setFont(new Font("Verdana",Font.PLAIN,28));
        scoreA.setForeground(Color.red);
        try {
            Thread.sleep(3000); // Pausa per 2 secondi (2000 millisecondi)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scoreA.setFont(new Font("Verdana",Font.PLAIN,20));
        scoreA.setForeground(Color.WHITE);
    }

    public void incrementaScoreB(){

        countB++;
        scoreB.setText(String.valueOf(countB));

        scoreB.setFont(new Font("Verdana",Font.PLAIN,30));
        scoreB.setForeground(Color.red);
        try {
            Thread.sleep(3000); // Pausa per 2 secondi (2000 millisecondi)
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        scoreB.setFont(new Font("Verdana",Font.PLAIN,20));
        scoreB.setForeground(Color.WHITE);
    }

    public void appendToPane(JTextPane textPane, String msg, Color color, int alignment, Font font){

        StyledDocument doc = textPane.getStyledDocument();

        // Definisce un nuovo attributo di colore e centratura
        SimpleAttributeSet attributeSet = new SimpleAttributeSet();
        StyleConstants.setForeground(attributeSet, color);  // Imposta il colore del testo
        StyleConstants.setAlignment(attributeSet, alignment); // Imposta l'allineamento
        StyleConstants.setFontFamily(attributeSet, font.getFamily()); // Imposta il tipo di font
        StyleConstants.setFontSize(attributeSet, font.getSize()); // Imposta la dimensione del font
        StyleConstants.setBold(attributeSet, font.isBold());      // Imposta il grassetto
        StyleConstants.setItalic(attributeSet, font.isItalic());  // Imposta il corsivo

        try {
            // Inserisci il testo con gli attributi di stile
            int len = doc.getLength();
            doc.insertString(len, msg, attributeSet);

            // Applica gli attributi di paragrafo per centratura o altro allineamento
            doc.setParagraphAttributes(len, msg.length(), attributeSet, false);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
