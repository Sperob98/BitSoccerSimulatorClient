package GUI;

import Connection.ConnessioneServerSocket;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class Home extends JFrame {

    private String player;
    public Home(ConnessioneServerSocket server, String username){

        ///////////////////////////////////////////////CONFIGURAZIONE DELLA CLASSE/////////////////////////////////////////////
        player = username;

        /////////////////////////////////////////IMPOSTAZIONE FINESTRA HOME////////////////////////////////////////////////////////////
        JFrame questoFframe = this;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        setTitle("Home");

        ImageIcon sfondoIcon = new ImageIcon(getClass().getResource("/erbaCalcio.jpg"));
        Image sfondoImage = sfondoIcon.getImage();
        setIconImage(sfondoImage);


        // Crea un JPanel per immagine di background
        JPanel backgroundPanel = new JPanel() {

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (sfondoImage != null) {
                    // Disegna l'immagine di sfondo
                    g.drawImage(sfondoImage, 0, 0, getWidth(), getHeight(), this);
                }
            }

            @Override
            public Dimension getPreferredSize() {
                return new Dimension(sfondoImage.getWidth(this), sfondoImage.getHeight(this));
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////CREAZIONI BORDI DEI PANELLI///////////////////////////////////////////////////////

        //BORDO LISTA SQUADRE
        Border bordoListaSqadre = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloBordoListaSquadre = new TitledBorder(bordoListaSqadre,"SQUADRE IN CREAZIONE");
        titoloBordoListaSquadre.setTitleColor(Color.WHITE);

        //BORDO CREA SQUADRA
        Border bordoCreaquadra = BorderFactory.createLineBorder(Color.BLACK, 1);
        TitledBorder titoloBordoCreaSquadre = new TitledBorder(bordoCreaquadra,"CREA SQUADRA");
        titoloBordoCreaSquadre.setTitleColor(Color.WHITE);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////GESTIONE PANNELLI////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////NORD///////////////////////////////////////////////////////////////////////////////
        //PANNELLO NOME APPLICAZIONE
        JPanel nomeAppPanel = new JPanel();
        nomeAppPanel.setLayout(new FlowLayout());
        nomeAppPanel.setOpaque(false);
        JLabel nomeAppLabel = new JLabel("BitSoccerSimulator");
        nomeAppLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        nomeAppLabel.setForeground(Color.GREEN);
        nomeAppPanel.add(nomeAppLabel);

        ///////////////////////////////////////////////CENTRO///////////////////////////////////////////////////////////////////
        //PANELLO TABELLA LISTA
        TabellaSquadreInCorso tabellaSquadre = new TabellaSquadreInCorso();
        String[] colonne = {"Nome squadra","Capitano","Numero partecipanti", "Numero massimo partecipanti"};
        DefaultTableModel modelTabellaSquadre = new DefaultTableModel(colonne,0){

            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Disabilita la modifica di tutte le celle
            }
        };
        tabellaSquadre.setModel(modelTabellaSquadre);
        tabellaSquadre.setCustomRenderer();

        JScrollPane pannelloListaSquadreEsterno = new JScrollPane(tabellaSquadre);
        pannelloListaSquadreEsterno.setOpaque(false);
        pannelloListaSquadreEsterno.getViewport().setOpaque(false);
        pannelloListaSquadreEsterno.setBorder(titoloBordoListaSquadre);

        //CREAZIONE THREAD RESPONSABILE DELL'AGGIORNAMENTO DELLA TABELLA SQUADRE
        Thread threadSquadre = new Thread( ()-> {
            try {
                server.updateTabellaSquadre(modelTabellaSquadre);
            }catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        });
        threadSquadre.start();

        ////////////////////////////////////////////////////EST/////////////////////////////////////////////////////////////////

        //PANNELLO CRAEZIONE SQUADRA BORDO
        JPanel panelloCreazioneSquadraEsterno = new JPanel();
        panelloCreazioneSquadraEsterno.setLayout(new BoxLayout(panelloCreazioneSquadraEsterno,BoxLayout.PAGE_AXIS));
        panelloCreazioneSquadraEsterno.setBorder(titoloBordoCreaSquadre);
        panelloCreazioneSquadraEsterno.setOpaque(false);

        //PRIMA RIGA ALL'INTERNO DEL PANNELLO CREAZIONE SQUADRA
        JPanel rigaLabelSquadra = new JPanel();
        rigaLabelSquadra.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        rigaLabelSquadra.setOpaque(false);
        JLabel nomeSquadraLabel = new JLabel(" INSERISCI NOME SQUADRA:");
        nomeSquadraLabel.setForeground(Color.WHITE);
        nomeSquadraLabel.setFont(new Font("Verdana", Font.PLAIN, 20));
        rigaLabelSquadra.add(nomeSquadraLabel);

        ////SECONDA RIGA ALL'INTERNO DEL PANNELLO CREAZIONE SQUADRA
        JPanel rigaInputSquadra = new JPanel(new FlowLayout());
        rigaInputSquadra.setOpaque(false);
        JTextField nomeSquadraInput = new JTextField(15);
        nomeSquadraInput.setFont(new Font("Verdana", Font.PLAIN, 18));
        nomeSquadraInput.setForeground(Color.WHITE);
        nomeSquadraInput.setOpaque(false);
        rigaInputSquadra.add(nomeSquadraInput);


        //TERZA RIGA ALL'INTERNO DEL PANNELLO CREAZIONE SQUADRA
        JPanel rigaBtnCreazione = new JPanel();
        rigaBtnCreazione.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        rigaBtnCreazione.setOpaque(false);
        JButton btnCreaSquadra = new JButton("CREA SQUADRA");
        rigaBtnCreazione.add(btnCreaSquadra);
        //EVENTO CLICK BOTTONE
        btnCreaSquadra.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String response;
                response = server.sendNewSquadra(nomeSquadraInput.getText().toUpperCase(),player);
                if(response.equals("ok")) {
                    new CreazioneSquadra(server, player, nomeSquadraInput.getText().toUpperCase(), true);
                    dispose();
                } else if(response.equals("busy")) JOptionPane.showMessageDialog(null,"Nome squadra già in uso, cambiare nome!");
                else JOptionPane.showMessageDialog(null,"Limite massimo di creazione squadre raggiunto, riprovare più tardi!");
            }
        });

        //AGGIUNGI RIGHE AL PANNELLO DI CREAZIONE SQUADRA
        panelloCreazioneSquadraEsterno.add(Box.createVerticalStrut(100));
        panelloCreazioneSquadraEsterno.add(rigaLabelSquadra);
        panelloCreazioneSquadraEsterno.add(rigaInputSquadra);
        panelloCreazioneSquadraEsterno.add(rigaBtnCreazione);;
        panelloCreazioneSquadraEsterno.add(Box.createVerticalStrut(100));

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////SUD////////////////////////////////////////////////////////////////////////////
        JPanel panelParteciazioneSquadra = new JPanel();
        panelParteciazioneSquadra.setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
        panelParteciazioneSquadra.setOpaque(false);
        JButton bottoneDiPartecipazione = new JButton("PARTECIPA ALLA SQUADRA SELEZIONATA");

        bottoneDiPartecipazione.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String decisioneCapitano;

                if(tabellaSquadre.getSelectedRow() >= 0){

                    int squadraSelezionata = tabellaSquadre.getSelectedRow();
                    String squadraSelezionataString = (String) modelTabellaSquadre.getValueAt(squadraSelezionata,0);

                    decisioneCapitano = server.sendPartecipazione(squadraSelezionataString,player);
                    if(decisioneCapitano.equals("ok")){

                        new CreazioneSquadra(server,player,squadraSelezionataString,false);
                        questoFframe.dispose();

                    }else JOptionPane.showMessageDialog(null,"Errore: richiesta partecipazione non riuscita, riprova.");

                }else{

                    JOptionPane.showMessageDialog(null,"Seleziona una squadra a cui unirsi");
                }
            }
        });

        panelParteciazioneSquadra.add(bottoneDiPartecipazione);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////COSTRUZIONE HOME/////////////////////////////////////////////////////////////
        backgroundPanel.add(nomeAppPanel, BorderLayout.NORTH);
        backgroundPanel.add(pannelloListaSquadreEsterno, BorderLayout.CENTER);
        backgroundPanel.add(panelloCreazioneSquadraEsterno, BorderLayout.EAST);
        backgroundPanel.add(panelParteciazioneSquadra, BorderLayout.SOUTH);

        add(backgroundPanel, BorderLayout.CENTER);

        //RENDI VISIBILE LA HOME
        setVisible(true);

    }
}
