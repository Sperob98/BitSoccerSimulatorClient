package GUI;

import Connection.ConnessioneServerSocket;
import Entity.InfoFineMatch;
import Entity.InfoMatch;
import Entity.SimulazioneMatch;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Partita extends JFrame {

    public Partita(ConnessioneServerSocket server, String player, String squadra, InfoMatch infoMatch){

        /////////////////////////////////////////IMPOSTAZIONE FINESTRA HOME////////////////////////////////////////////////////////////
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000,500);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(new BorderLayout());

        //Classe che permette l'aggironamento delle informazioni del match
        SimulazioneMatch match = new SimulazioneMatch();

        //Colora di grigio il player che corrisponde al client in uso
        match.setPlayerClient(player);
        match.getListPlayersA().repaint();
        match.getListPlayersA().revalidate();
        match.getListPlayersB().repaint();
        match.getListPlayersB().revalidate();

        /////////////////////////////////////////NORD////////////////////////////////////////////////////////////////////////////
        JPanel panelNord = new JPanel(new FlowLayout());
        Border bordoPanelNord = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloBordoNord = new TitledBorder(bordoPanelNord, "INFO PARTITA");
        panelNord.setBorder(titoloBordoNord);

        //Nomi squadre
        JPanel panelNomiSquadre = new JPanel();
        JLabel squadrePartiteLabel = new JLabel(infoMatch.getSquadraA() + " vs " + infoMatch.getSquadraB());
        squadrePartiteLabel.setFont(new Font("Verdana", Font.PLAIN, 18));
        panelNomiSquadre.add(squadrePartiteLabel);

        //Scores
        JPanel panelScores = new JPanel(new FlowLayout());
        //Font scores
        match.getScoreA().setFont(new Font("Verdana",Font.PLAIN,18));
        match.getScoreB().setFont(new Font("Verdana",Font.PLAIN,18));
        JLabel trattinoLabel = new JLabel("-");
        trattinoLabel.setFont(new Font("Verdana",Font.PLAIN,18));
        //Componi scores
        panelScores.add(match.getScoreA());
        panelScores.add(trattinoLabel);
        panelScores.add(match.getScoreB());
        //Aggiuiungi scores e nomi squadra al panel
        panelNord.add(panelScores,FlowLayout.LEFT);
        panelNord.add(panelNomiSquadre,FlowLayout.CENTER);


        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ///////////////////////////////////////CENTRO///////////////////////////////////////////////////////////////////////////

        /////////////////DECISIONE TURNO

        //Immagine palla
        ImageIcon imagePalla = new ImageIcon(getClass().getResource("/palla.png"));
        JLabel labelPallaTop = new JLabel(imagePalla);
        JLabel labelPallaCenter = new JLabel(imagePalla);
        JLabel labelPallaDown = new JLabel(imagePalla);

        JPanel panelCenter = new JPanel();
        panelCenter.setLayout(new BoxLayout(panelCenter,BoxLayout.Y_AXIS));
        Border bordoPanelCenter = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloBordoCenter = new TitledBorder(bordoPanelCenter,"CRONACA PARTITA");
        panelCenter.setBorder(titoloBordoCenter);

        //Istruzione su cosa fare
        JLabel labelIstruzione = new JLabel("Clicca sulla palla per ottenere il turno");
        panelCenter.add(labelIstruzione);

        //Panel alto contenente il pallone
        JPanel panelBallTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBallTop.setSize(25,25);
        panelBallTop.add(labelPallaTop);

        labelPallaTop.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                server.sendRichiestaInizioTurno(infoMatch,player,squadra);
            }
        });

        //Panel centrale contente il pallone
        JPanel panelBallCenter = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBallCenter.setSize(25,25);
        panelBallCenter.add(labelPallaCenter);
        panelBallCenter.setVisible(false);

        labelPallaCenter.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                server.sendRichiestaInizioTurno(infoMatch,player,squadra);
            }
        });

        //Panel basso contente il pallone
        JPanel panelBallDown = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBallDown.setSize(25,25);
        panelBallDown.add(labelPallaDown);
        panelBallDown.setVisible(false);

        labelPallaDown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                server.sendRichiestaInizioTurno(infoMatch,player,squadra);
            }
        });

        //Riempi panelCenter
        panelCenter.add(panelBallTop);
        panelCenter.add(Box.createVerticalStrut(100));
        panelCenter.add(panelBallCenter);
        panelCenter.add(Box.createVerticalStrut(100));
        panelCenter.add(panelBallDown);

        ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ////////////////////////////////////////////////OVEST////////////////////////////////////////////////////////////////////////////////////////////////////

        JPanel panelOvest = new JPanel();
        Border bordoOvest = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloOvest = new TitledBorder(bordoOvest,infoMatch.getSquadraA());
        panelOvest.setBorder(titoloOvest);

        DefaultListModel<String> modelListA = match.getModelListPlayerA();
        String[] arrayPayersA = infoMatch.getPlayersA();

        modelListA.addElement(infoMatch.getCapitanoA() + " (capitano)");
        for(int i=0; i<4; i++) modelListA.addElement(arrayPayersA[i]);

        panelOvest.add(match.getListPlayersA());

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////EST////////////////////////////////////////////////////////////////////////////////////////

        JPanel panelEst = new JPanel();
        Border bordoEst = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloEst = new TitledBorder(bordoEst, infoMatch.getSquadraB());
        panelEst.setBorder(titoloEst);

        DefaultListModel<String> modelListB = match.getModelListPlayerB();
        String[] arrayPlayersB = infoMatch.getPlayersB();

        modelListB.addElement(infoMatch.getCapitanoB() + " (capitano)");
        for(int i=0; i<4; i++) modelListB.addElement(arrayPlayersB[i]);

        panelEst.add(match.getListPlayersB());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////SUD////////////////////////////////////////////////////////////////////////////////////////

        JPanel panelSud = new JPanel(new FlowLayout());
        Border bordoSud = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloSud = new TitledBorder(bordoSud, "LOGS MATCH");
        panelSud.setBorder(titoloSud);

        panelSud.setVisible(false);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////COSTRUZIONE FRAME//////////////////////////////////////////////////////////////////////////////

        add(panelNord,BorderLayout.NORTH);
        add(panelCenter,BorderLayout.CENTER);
        add(panelOvest,BorderLayout.WEST);
        add(panelEst,BorderLayout.EAST);
        add(panelSud,BorderLayout.SOUTH);
        setVisible(true);

        //Avvia thread di ascolto per la simulaione partita

        Thread threadAscoltoSimulazione = new Thread( () -> {

            try {
                InfoFineMatch infoFineMatch = server.threadAscoltoPartita(match,infoMatch);

                JButton buttonFileLogPlayer = new JButton("Ottieni file log delle statiche dei players");
                JButton buttonFileLogMatch = new JButton("Ottieni file log di tutta la descrizione della partita");

                panelSud.add(buttonFileLogMatch);
                panelSud.add(buttonFileLogPlayer);

                panelSud.setVisible(true);

                buttonFileLogPlayer.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                       costruisciFileStatistiche(infoFineMatch);
                    }
                });

                buttonFileLogMatch.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {

                        costruisciFileMatch(infoFineMatch);
                    }
                });

            }catch (IOException ex){

            }
        });
        threadAscoltoSimulazione.start();

        //////////////////////////////////////LOOP DECISIONE TURNO/////////////////////////////////////////////////////////////////////////

        while(!match.isAvvioPartita()){

            try{

                Thread.sleep(1000);

                panelBallTop.setVisible(false);
                panelBallCenter.setVisible(true);

                Thread.sleep(1000);

                panelBallCenter.setVisible(false);
                panelBallDown.setVisible(true);

                Thread.sleep(1000);

                panelBallDown.setVisible(false);
                panelBallTop.setVisible(true);

            }catch (InterruptedException ex){

            }

            if(match.isAvvioPartita()){

                panelCenter.removeAll();

                JScrollPane panelCronaca = new JScrollPane(match.getCronaca());

                panelCenter.add(panelCronaca);

                //Aggiorna
                panelCenter.repaint();
                panelCenter.revalidate();
            }
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    }

    private void costruisciFileStatistiche(InfoFineMatch infoFineMatch){

        // Crea un JFileChooser per permettere all'utente di scegliere dove salvare il file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Scegli dove salvare il file");

        // Mostra la finestra di dialogo di salvataggio
        int userSelection = fileChooser.showSaveDialog(null);

        // Verifica se l'utente ha selezionato un file
        if(userSelection == JFileChooser.APPROVE_OPTION){

            File fileToSave = fileChooser.getSelectedFile();

            // Aggiungi estensione se non presente
            if (!fileToSave.getAbsolutePath().endsWith(".txt")) {
                fileToSave = new File(fileToSave + ".txt");
            }

            // Scrivi il contenuto nel file selezionato
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(infoFineMatch.getLogStatistiche());
                System.out.println("File salvato: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private void costruisciFileMatch(InfoFineMatch infoFineMatch){

        // Crea un JFileChooser per permettere all'utente di scegliere dove salvare il file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Scegli dove salvare il file");

        // Mostra la finestra di dialogo di salvataggio
        int userSelection = fileChooser.showSaveDialog(null);

        // Verifica se l'utente ha selezionato un file
        if(userSelection == JFileChooser.APPROVE_OPTION){

            File fileToSave = fileChooser.getSelectedFile();

            // Aggiungi estensione se non presente
            if (!fileToSave.getAbsolutePath().endsWith(".txt")) {
                fileToSave = new File(fileToSave + ".txt");
            }

            // Scrivi il contenuto nel file selezionato
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(infoFineMatch.getLogMatch());
                System.out.println("File salvato: " + fileToSave.getAbsolutePath());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
}
