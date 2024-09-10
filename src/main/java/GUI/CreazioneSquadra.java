package GUI;

import Connection.ConnessioneServerSocket;
import Entity.InfoMatch;
import org.json.JSONObject;

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

        //Inizializzazioni variabili di classe
        this.player = player;
        this.isCapitano = isCapitano;
        this.nomeSquadra = nomeSquadra;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /////////////////////////////////////////////////////NORD///////////////////////////////////////////////////////////////
        JLabel nomeSquadraLabel = new JLabel();
        nomeSquadraLabel.setText(this.nomeSquadra);
        nomeSquadraLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        JPanel panelNomeSquadra = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelNomeSquadra.add(nomeSquadraLabel);

        ////////////////////////////////////////CENTRO/////////////////////////////////////////////////////////////////////////

        //PANNELLO LISTA PLAYER ACCETTATI
        Border bordoListaPlayerAccettati = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloPlayerAccettati = new TitledBorder(bordoListaPlayerAccettati,"PLAYER PARTECIPANTI");
        //JPanel panelListaPlayer = new JPanel();
        //panelListaPlayer.setBorder(titoloPlayerAccettati);
        //panelListaPlayer.setLayout(new BoxLayout(panelListaPlayer,BoxLayout.Y_AXIS));
        JList<String> listaPlayer = new JList<>();
        JScrollPane scrollPlayerAccettati = new JScrollPane(listaPlayer);
        scrollPlayerAccettati.setBorder(titoloPlayerAccettati);
        //panelListaPlayer.add(scrollPlayerAccettati);

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
        Border bordoListaRichieste = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloRichiestePlayer = new TitledBorder(bordoListaRichieste,"RICHIESTE PLAYER");
        JPanel panelRichiestePlayer = new JPanel();
        panelRichiestePlayer.setLayout(new BoxLayout(panelRichiestePlayer,BoxLayout.Y_AXIS));
        panelRichiestePlayer.setBorder(titoloRichiestePlayer);

        //PANNELLO LISTA RICHIESTE
        JList<String> listaRichieste = new JList<>();
        JScrollPane scrollRichiestePlayer = new JScrollPane(listaRichieste);
        DefaultListModel<String> modelListaRichieste = new DefaultListModel<>();
        listaRichieste.setModel(modelListaRichieste);

        //PANNELLO BOTTONI GESTIONE RICHIESTE
        JPanel panelBottoni = new JPanel();
        panelBottoni.setLayout(new FlowLayout(FlowLayout.CENTER,0,0));
        JButton acceptButton = new JButton("ACCETTA PLAYER");
        JButton rejectButton = new JButton("RIFIUTA PLAYER");

        if(!isCapitano){

            acceptButton.setEnabled(false);
            rejectButton.setEnabled(false);
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

        JButton btnMatch = new JButton("Cerca una squadra avversaria");
        if(!isCapitano) btnMatch.setEnabled(false);

        InfoMatch infoMatch = new InfoMatch();

        panelSud.add(btnMatch);
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
        add(panelNomeSquadra,BorderLayout.NORTH);
        add(scrollPlayerAccettati, BorderLayout.CENTER);
        add(panelRichiestePlayer,BorderLayout.EAST);
        add(panelSud,BorderLayout.SOUTH);

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
