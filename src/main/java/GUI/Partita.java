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

        ImageIcon sfondoIcon = new ImageIcon(getClass().getResource("/sfondoCampo2.jpg"));
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

        //Classe che permette l'aggironamento delle informazioni del match
        SimulazioneMatch match = new SimulazioneMatch();

        //Colora di grigio il player che corrisponde al client in uso
        match.setPlayerClient(player);
        match.getListPlayersA().repaint();
        match.getListPlayersA().revalidate();
        match.getListPlayersB().repaint();
        match.getListPlayersB().revalidate();

        /////////////////////////////////////////NORD////////////////////////////////////////////////////////////////////////////
        JPanel panelNord = new JPanel(new FlowLayout(FlowLayout.CENTER,35,0));
        Border bordoPanelNord = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloBordoNord = new TitledBorder(bordoPanelNord, "INFO PARTITA");
        titoloBordoNord.setTitleFont(new Font("Verdana", Font.BOLD, 16));
        titoloBordoNord.setTitleColor(Color.WHITE);
        panelNord.setBorder(titoloBordoNord);
        panelNord.setOpaque(false);

        //Nomi squadre
        JPanel panelNomiSquadre = new JPanel();
        panelNomiSquadre.setOpaque(false);
        JLabel squadrePartiteLabel = new JLabel(infoMatch.getSquadraA() + " vs " + infoMatch.getSquadraB());
        squadrePartiteLabel.setFont(new Font("Verdana", Font.PLAIN, 24));
        squadrePartiteLabel.setForeground(Color.WHITE);
        panelNomiSquadre.add(squadrePartiteLabel);

        //Scores
        JPanel panelScores = new JPanel(new FlowLayout());
        panelScores.setOpaque(false);
        //Font scores
        match.getScoreA().setFont(new Font("Verdana",Font.PLAIN,20));
        match.getScoreA().setForeground(Color.WHITE);
        match.getScoreB().setFont(new Font("Verdana",Font.PLAIN,20));
        match.getScoreB().setForeground(Color.WHITE);
        JLabel trattinoLabel = new JLabel("-");
        trattinoLabel.setFont(new Font("Verdana",Font.PLAIN,20));
        trattinoLabel.setForeground(Color.WHITE);
        //Componi scores
        panelScores.add(match.getScoreA());
        panelScores.add(trattinoLabel);
        panelScores.add(match.getScoreB());
        //Aggiuiungi scores e nomi squadra al panel
        panelNord.add(panelScores,FlowLayout.LEFT);
        panelNord.add(panelNomiSquadre,FlowLayout.CENTER);

        //Time
        JPanel panelTime = new JPanel(new FlowLayout());
        panelTime.setOpaque(false);
        //Font
        match.getTime().setFont(new Font("Verdana",Font.PLAIN,20));
        match.getTime().setForeground(Color.white);
        //aggiungi time
        panelTime.add(match.getTime());
        panelNord.add(panelTime,FlowLayout.RIGHT);

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
        titoloBordoCenter.setTitleFont(new Font("Verdana", Font.BOLD, 16));
        titoloBordoCenter.setTitleColor(Color.BLACK);
        panelCenter.setBorder(titoloBordoCenter);
        panelCenter.setOpaque(false);

        //Istruzione su cosa fare
        JLabel labelIstruzione = new JLabel("Clicca sulla palla per ottenere il turno");
        labelIstruzione.setFont(new Font("Verdana", Font.BOLD, 18));
        labelIstruzione.setForeground(Color.green);
        panelCenter.add(labelIstruzione);

        //Panel alto contenente il pallone
        JPanel panelBallTop = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelBallTop.setOpaque(false);
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
        panelBallCenter.setOpaque(false);
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
        panelBallDown.setOpaque(false);
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
        panelOvest.setOpaque(false);
        Border bordoOvest = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloOvest = new TitledBorder(bordoOvest,infoMatch.getSquadraA());
        titoloOvest.setTitleColor(Color.white);
        panelOvest.setBorder(titoloOvest);

        DefaultListModel<String> modelListA = match.getModelListPlayerA();
        String[] arrayPayersA = infoMatch.getPlayersA();

        modelListA.addElement(infoMatch.getCapitanoA() + " (capitano)");
        for(int i=0; i<4; i++) modelListA.addElement(arrayPayersA[i]);

        panelOvest.add(match.getListPlayersA());

        //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////EST////////////////////////////////////////////////////////////////////////////////////////

        JPanel panelEst = new JPanel();
        panelEst.setOpaque(false);
        Border bordoEst = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloEst = new TitledBorder(bordoEst, infoMatch.getSquadraB());
        titoloEst.setTitleColor(Color.white);
        panelEst.setBorder(titoloEst);

        DefaultListModel<String> modelListB = match.getModelListPlayerB();
        String[] arrayPlayersB = infoMatch.getPlayersB();

        modelListB.addElement(infoMatch.getCapitanoB() + " (capitano)");
        for(int i=0; i<4; i++) modelListB.addElement(arrayPlayersB[i]);

        panelEst.add(match.getListPlayersB());

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

        ////////////////////////////////////////////////SUD////////////////////////////////////////////////////////////////////////////////////////

        JPanel panelSud = new JPanel(new FlowLayout(FlowLayout.LEFT,10,0));
        panelSud.setOpaque(false);
        Border bordoSud = BorderFactory.createLineBorder(Color.BLACK,1);
        TitledBorder titoloSud = new TitledBorder(bordoSud, "LOGS MATCH");
        titoloSud.setTitleFont(new Font("Verdana", Font.BOLD, 16));
        titoloSud.setTitleColor(Color.white);
        panelSud.setBorder(titoloSud);

        JButton buttonFileLogPlayer = new JButton("Ottieni file log delle statiche dei players");
        buttonFileLogPlayer.setEnabled(false);
        JButton buttonFileLogMatch = new JButton("Ottieni file log di tutta la descrizione della partita");
        buttonFileLogMatch.setEnabled(false);

        JPanel panelMappaColori = new JPanel();
        panelMappaColori.setLayout(new BoxLayout(panelMappaColori,BoxLayout.Y_AXIS));
        panelMappaColori.setOpaque(false);

        JPanel rigaColore1 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        rigaColore1.setOpaque(false);
        JPanel rigaColore2 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,0));
        rigaColore2.setOpaque(false);
        JPanel rigaColore3 = new JPanel(new FlowLayout(FlowLayout.LEFT,5,5));
        rigaColore3.setOpaque(false);

        panelMappaColori.add(rigaColore1);
        panelMappaColori.add(rigaColore2);
        panelMappaColori.add(rigaColore3);

        JLabel colore1 = new JLabel();
        colore1.setBackground(Color.DARK_GRAY);
        colore1.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY,2));
        colore1.setMinimumSize(new Dimension(10,10));
        colore1.setMaximumSize(new Dimension(10,10));
        colore1.setPreferredSize(new Dimension(10,10));
        JLabel ioLabel = new JLabel("io");
        ioLabel.setFont(new Font("Arial", Font.BOLD, 14));
        ioLabel.setForeground(Color.darkGray);
        colore1.setOpaque(true);
        rigaColore1.add(colore1);
        rigaColore1.add(ioLabel);

        JLabel colore2 = new JLabel();
        colore2.setBackground(Color.GREEN);
        colore2.setBorder(BorderFactory.createLineBorder(Color.GREEN,2));
        colore2.setMinimumSize(new Dimension(10,10));
        colore2.setMaximumSize(new Dimension(10,10));
        colore2.setPreferredSize(new Dimension(10,10));
        JLabel turnoLabel = new JLabel("Player di turno");
        turnoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        turnoLabel.setForeground(Color.GREEN);
        colore2.setOpaque(true);
        rigaColore2.add(colore2);
        rigaColore2.add(turnoLabel);

        JLabel colore3 = new JLabel();
        colore3.setBackground(Color.RED);
        colore3.setBorder(BorderFactory.createLineBorder(Color.RED,2));
        colore3.setMinimumSize(new Dimension(10,10));
        colore3.setMaximumSize(new Dimension(10,10));
        colore3.setPreferredSize(new Dimension(10,10));
        colore3.setOpaque(true);
        JLabel infortunatoPlayer = new JLabel("Player infortunato/penalizzato");
        infortunatoPlayer.setFont(new Font("Arial", Font.PLAIN, 14));
        infortunatoPlayer.setForeground(Color.RED);
        rigaColore3.add(colore3);
        rigaColore3.add(infortunatoPlayer);

        panelSud.add(panelMappaColori);
        panelSud.add(buttonFileLogMatch);
        panelSud.add(buttonFileLogPlayer);

        /////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////


        ///////////////////////////////////////////////////COSTRUZIONE FRAME//////////////////////////////////////////////////////////////////////////////

        backgroundPanel.add(panelNord,BorderLayout.NORTH);
        backgroundPanel.add(panelCenter,BorderLayout.CENTER);
        backgroundPanel.add(panelOvest,BorderLayout.WEST);
        backgroundPanel.add(panelEst,BorderLayout.EAST);
        backgroundPanel.add(panelSud,BorderLayout.SOUTH);

        add(backgroundPanel,BorderLayout.CENTER);

        setVisible(true);

        //Avvia thread di ascolto per la simulaione partita

        Thread threadAscoltoSimulazione = new Thread( () -> {

            try {
                InfoFineMatch infoFineMatch = server.threadAscoltoPartita(match,infoMatch);

                buttonFileLogMatch.setEnabled(true);
                buttonFileLogPlayer.setEnabled(true);

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
                panelCronaca.setOpaque(false);
                panelCronaca.getViewport().setOpaque(false);

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
