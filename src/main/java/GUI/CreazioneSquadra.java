package GUI;

import Connection.ConnessioneServerSocket;
import Entity.InfoMatch;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class CreazioneSquadra extends JFrame {

    String player;

    String nomeSquadra;
    boolean isCapitano;

    public CreazioneSquadra(ConnessioneServerSocket server,String player, String nomeSquadra, boolean isCapitano){

        /////////////////////////////////////////IMPOSTAZIONE FINESTRA HOME////////////////////////////////////////////////////////////
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800,500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());
        setTitle("SPOGLIATOIO");

        //Inizializzazioni variabili di classe
        this.player = player;
        this.isCapitano = isCapitano;
        this.nomeSquadra = nomeSquadra;

        ImageIcon sfondoIcon = new ImageIcon(getClass().getResource("/spogliatoio2.jpg"));
        Image sfondoImage = sfondoIcon.getImage();

        ImageIcon icona = new ImageIcon(getClass().getResource("/erbaCalcio.jpg"));
        Image iconaImage = icona.getImage();
        setIconImage(iconaImage);

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

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////NORD///////////////////////////////////////////////////////////////
        JLabel nomeSquadraLabel = new JLabel();
        nomeSquadraLabel.setText(this.nomeSquadra);
        nomeSquadraLabel.setFont(new Font("Verdana", Font.BOLD, 36));
        nomeSquadraLabel.setForeground(Color.BLACK);
        JPanel panelNomeSquadra = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNomeSquadra.add(nomeSquadraLabel);
        panelNomeSquadra.setBackground(new Color(255,255,255,50));

        ////////////////////////////////////////CENTRO/////////////////////////////////////////////////////////////////////////

        //PANNELLO LISTA PLAYER ACCETTATI
        Border bordoListaPlayerAccettati = BorderFactory.createLineBorder(Color.BLACK,2);
        TitledBorder titoloPlayerAccettati = new TitledBorder(bordoListaPlayerAccettati,"PLAYER PARTECIPANTI");
        titoloPlayerAccettati.setTitleFont(new Font("Verdana", Font.BOLD, 16));
        titoloPlayerAccettati.setTitleColor(Color.WHITE);
        JList<String> listaPlayer = new JList<>();
        listaPlayer.setCellRenderer(new RenderListPlayersAccettati());
        listaPlayer.setOpaque(false);
        JScrollPane scrollPlayerAccettati = new JScrollPane(listaPlayer);
        scrollPlayerAccettati.setBorder(titoloPlayerAccettati);
        scrollPlayerAccettati.setOpaque(false);
        scrollPlayerAccettati.getViewport().setOpaque(false);

        //GESTIONE LISTA
        DefaultListModel<String> modelListaPlayer = new DefaultListModel<>();
        if(isCapitano){

            String nomeCapitano;
            nomeCapitano = this.player + " (capitano)";
            modelListaPlayer.addElement(nomeCapitano);
        }
        listaPlayer.setModel(modelListaPlayer);


        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////EST////////////////////////////////////////////////////////////////////////

        //PANNELLO RICHIESTE PALYER
        Border bordoListaRichieste = BorderFactory.createLineBorder(Color.BLACK,2);
        TitledBorder titoloRichiestePlayer = new TitledBorder(bordoListaRichieste,"RICHIESTE PLAYER");
        titoloRichiestePlayer.setTitleFont(new Font("Verdana", Font.BOLD, 16));
        titoloRichiestePlayer.setTitleColor(Color.WHITE);
        JPanel panelRichiestePlayer = new JPanel();
        panelRichiestePlayer.setLayout(new BoxLayout(panelRichiestePlayer,BoxLayout.Y_AXIS));
        panelRichiestePlayer.setBorder(titoloRichiestePlayer);
        panelRichiestePlayer.setOpaque(false);
        panelRichiestePlayer.setMinimumSize(new Dimension(330,300));
        panelRichiestePlayer.setPreferredSize(new Dimension(330,300));

        //PANNELLO LISTA RICHIESTE
        JList<String> listaRichieste = new JList<>();
        listaRichieste.setCellRenderer(new RenderListRichieste());
        listaRichieste.setOpaque(false);
        JScrollPane scrollRichiestePlayer = new JScrollPane(listaRichieste);
        DefaultListModel<String> modelListaRichieste = new DefaultListModel<>();
        listaRichieste.setModel(modelListaRichieste);
        scrollRichiestePlayer.setOpaque(false);
        scrollRichiestePlayer.getViewport().setOpaque(false);

        //PANNELLO BOTTONI GESTIONE RICHIESTE
        JPanel panelBottoni = new JPanel();
        panelBottoni.setLayout(new FlowLayout(FlowLayout.CENTER,10,20));
        JButton acceptButton = new JButton("ACCETTA PLAYER");
        JButton rejectButton = new JButton("RIFIUTA PLAYER");
        panelBottoni.setOpaque(false);

        if(!isCapitano){

            acceptButton.setVisible(false);
            rejectButton.setVisible(false);
        }

        acceptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int indexSelezionato = listaRichieste.getSelectedIndex();
                if(indexSelezionato >= 0) {

                    String playerSelezionato = modelListaRichieste.getElementAt(indexSelezionato);
                    String response = server.sendDecisioneCapitano(nomeSquadra,playerSelezionato, "accettato");

                    if(response.equals("ok")) JOptionPane.showMessageDialog(null,"Operazione eseguita");
                    else if(response.equals("full")) JOptionPane.showMessageDialog(null,"Attenzione: squadra al completo");
                    else JOptionPane.showMessageDialog(null,"Operazione non riuscita");
                }
                else JOptionPane.showMessageDialog(null,"Seleziona un player");
            }
        });

        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int indexSelezionato = listaRichieste.getSelectedIndex();
                if(indexSelezionato >= 0) {

                    String playerSelezionato = modelListaRichieste.getElementAt(indexSelezionato);
                    String response = server.sendDecisioneCapitano(nomeSquadra, playerSelezionato, "rifiutato");

                    if(response.equals("ok")) JOptionPane.showMessageDialog(null,"Operazione eseguita");
                    else if(response.equals("full")) JOptionPane.showMessageDialog(null,"Attenzione: squadra al completo");
                    else JOptionPane.showMessageDialog(null,"Operazione non riuscita");
                }
                else JOptionPane.showMessageDialog(null,"Seleziona un player");
            }
        });

        panelBottoni.add(acceptButton);
        panelBottoni.add(rejectButton);


        panelRichiestePlayer.add(scrollRichiestePlayer);
        panelRichiestePlayer.add(panelBottoni);

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////////////SUD////////////////////////////////////////////////////////////////////////
        JPanel panelSud = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelSud.setOpaque(false);

        JButton btnMatch = new JButton("CERCA UNA SQUADRA AVVERSARIA");
        if(!isCapitano) btnMatch.setEnabled(false);

        InfoMatch infoMatch = new InfoMatch();

        panelSud.add(btnMatch);
        JLabel labelInfoMatch = infoMatch.getStatoMatch();
        labelInfoMatch.setFont(new Font("Arial", Font.BOLD, 16));
        labelInfoMatch.setForeground(Color.BLACK);
        panelSud.add(infoMatch.getStatoMatch());

        if(isCapitano) infoMatch.getStatoMatch().setVisible(false);
        else btnMatch.setVisible(false);

        btnMatch.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                if(modelListaPlayer.getSize() == 5 && modelListaRichieste.getSize() == 0) {

                    String response = server.cercaMatch(nomeSquadra);
                    System.out.println(response);

                    if (!(response.equals("ok"))) {

                        JOptionPane.showMessageDialog(null, "Operazione non riuscita, riprovare!", "Errore", JOptionPane.ERROR_MESSAGE);

                    }else{

                        JOptionPane.showMessageDialog(null, "Operazione riuscita!");

                        btnMatch.setVisible(false);
                        infoMatch.getStatoMatch().setVisible(true);
                    }

                }else  JOptionPane.showMessageDialog(null,"Attenzione: assicurarsi di rimuovere ogni richiesta e di completare la squadra");
            }
        });

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        //////////////////////////////////////COSTRUZIONE FRAME///////////////////////////////////////////////////////////////////
        backgroundPanel.add(panelNomeSquadra,BorderLayout.NORTH);
        backgroundPanel.add(scrollPlayerAccettati, BorderLayout.CENTER);
        backgroundPanel.add(panelRichiestePlayer,BorderLayout.EAST);
        backgroundPanel.add(panelSud,BorderLayout.SOUTH);

        add(backgroundPanel, BorderLayout.CENTER);

        setVisible(true);

        //////////////////////////////////////////////GESTIONE AGGIORNAMENTO SQUADRA////////////////////////////////////////////////////////////////////////////

        Thread threadListaRichieste = new Thread( () -> {

            server.setBreakUpdateSquadre(true);
            try {

                int stato = server.updateSpogliatoio(modelListaPlayer,modelListaRichieste,infoMatch);

                switch (stato){

                    case 1:

                        JOptionPane.showMessageDialog(null,"Non sei stato accettato in squadra");
                        new Home(server, player);
                        dispose();
                        break;

                    case 2:

                        JOptionPane.showMessageDialog(null, "Il capitano ha abbadonato la squadra, squadra sciolta");
                        new Home(server, player);
                        dispose();
                        break;

                    case 3:

                        dispose();
                        new Partita(server,player,nomeSquadra,infoMatch);
                        break;
                }

            }catch (IOException ex){
                System.out.println(ex.getMessage());
            }
        });
        threadListaRichieste.start();
    }
}
