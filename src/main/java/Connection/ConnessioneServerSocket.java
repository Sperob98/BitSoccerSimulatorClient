package Connection;

import Entity.InfoFineMatch;
import Entity.InfoMatch;
import Entity.LogPlayer;
import Entity.SimulazioneMatch;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONString;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ConnessioneServerSocket {
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;

    private boolean breakUpdateSquadre;

    private boolean breakUpdateSpogliatoglio;

    public void setBreakUpdateSquadre(boolean breakUpdateSquadre) {
        this.breakUpdateSquadre = breakUpdateSquadre;
    }

    public void setBreakUpdateSpogliatoglio(boolean breakUpdateSpogliatoglio) {
        this.breakUpdateSpogliatoglio = breakUpdateSpogliatoglio;
    }

    public boolean isBreakUpdateSquadre() {
        return breakUpdateSquadre;
    }

    public boolean isBreakUpdateSpogliatoglio() {
        return breakUpdateSpogliatoglio;
    }

    public ConnessioneServerSocket(){

        breakUpdateSquadre = false;
        breakUpdateSpogliatoglio = false;
    }

    public void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new PrintWriter(clientSocket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
    }

    public String sendUsernamePlayer(String user) throws IOException{

        //Creazione oggetto richiesta JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","nuovoUtente\0");
        json.put("utente",user + "\0");

        out.println(json.toString());

        String validita = in.readLine();

        return validita;
    }

    public String sendNewSquadra(String squadra, String capitano){

        String response;

        // Creazione di un oggetto JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","newSquadra\0");
        json.put("squadra", squadra+"\0");
        json.put("capitano",capitano+"\0");

        out.println(json.toString());

        breakUpdateSquadre = true;
        synchronized (in) {
            try {
                response = in.readLine();
                System.out.println("riposta: " + response);
            } catch (IOException ex) {
                response = "ko";
                breakUpdateSquadre = false;
                in.notifyAll();
            }
        }

        return response;
    }

    public void updateTabellaSquadre(DefaultTableModel modelTabella) throws IOException {

        //Creazione oggetto richiesta JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","getSquadreInCostruzione\0");

        //Invia richiesta
        out.println(json.toString());

        String tipoDiMessaggio;

        //Aggiorna tabella
        while(true) {

            synchronized (in) {

                while(breakUpdateSquadre){

                    try{
                        in.wait();
                    }catch (InterruptedException ex){
                        System.out.println("Eccezione InterruptedException");
                    }
                }

                tipoDiMessaggio = in.readLine();

                if (tipoDiMessaggio.equals("AggiornamentoSquadra")) {

                    StringBuilder jsonBuilder = new StringBuilder();
                    int ch;
                    while ((ch = in.read()) != -1) {
                        if (ch == '\n') { // Considera '\n' come delimitatore di fine
                            break;
                        }
                        jsonBuilder.append((char) ch);
                    }

                    String jsonResponse = jsonBuilder.toString();

                    if (!jsonResponse.startsWith("[")) {
                        System.out.println("Il messaggio ricevuto non è un array JSON valido: " + jsonResponse);
                    }
                    JSONArray jsonArray = new JSONArray(jsonResponse);

                    modelTabella.setRowCount(0);//Resetta model
                    for (int i = 0; i < jsonArray.length(); i++) {

                        String[] datiSquadra = new String[4];
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        datiSquadra[0] = jsonObject.getString("nomeSquadra");
                        datiSquadra[1] = jsonObject.getString("capitano");
                        datiSquadra[2] = Integer.toString(jsonObject.getInt("numeroPlayers"));
                        datiSquadra[3] = "5";
                        modelTabella.addRow(datiSquadra);
                    }
                }
            }
        }
    }

    public String sendPartecipazione(String squadraSelezionata, String player){

        //Creazione oggetto richiesta JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","partecipazioneSquadra\0");
        json.put("squadra",squadraSelezionata + "\0");
        json.put("player",player+"\0");

        //Invia richiesta
        out.println(json.toString());

        //Attendi risposta
        String response;
        breakUpdateSquadre = true;
        synchronized (in) {
            try {
                response = in.readLine();
                System.out.println("riposta: " + response);
            } catch (IOException ex) {
                response = "ko";
                breakUpdateSquadre = false;
                in.notifyAll();
            }
        }

        return response;
    }

    public String sendDecisioneCapitano(String squadra, String player, String decisione){

        //Creazione oggetto richiesta JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","decisioneCapitano\0");
        json.put("squadra",squadra + "\0");
        json.put("nomePlayer",player + "\0");
        json.put("decisione",decisione +"\0");

        //Invia richiesta
        out.println(json.toString());

        //Attendi risposta
        String response;
        breakUpdateSpogliatoglio = true;
        synchronized (in){

            try{
                response = in.readLine();
            }catch (IOException ex){
                response = "ko";
            }

            breakUpdateSpogliatoglio = false;
            in.notifyAll();
        }

        return response;
    }

    public int updateSpogliatoio(DefaultListModel<String> listaPlayers, DefaultListModel<String> listaRichieste, InfoMatch infoMatch,JButton btnMatch,boolean isCapitano) throws IOException{

        String tipoMessaggio;
        String line;

        while(true){

            synchronized (in) {

                while (breakUpdateSpogliatoglio){
                    try{
                        in.wait();
                    }catch (InterruptedException ex){

                    }
                }

                tipoMessaggio = in.readLine();
                System.out.println(tipoMessaggio);
                if (tipoMessaggio.equals("AggiornamentoComposizioneSquadra")) {

                    StringBuilder jsonBuilder = new StringBuilder();
                    line = in.readLine();
                    jsonBuilder.append(line);
                    String jsonResponse = jsonBuilder.toString();
                    System.out.println("oggetto ricevuto: " + jsonResponse);

                    //Deserializzazione della stringa JSON in un oggetto JSONObject
                    JSONObject jsonObject = new JSONObject(jsonResponse.toString());

                    //Estrazione dell'array JSON di richieste
                    JSONArray richiesteArray = jsonObject.getJSONArray("richieste");

                    //Aggiorna lista richieste
                    listaRichieste.clear();
                    for (int i = 0; i < richiesteArray.length(); i++) {
                        String richiestaPlayer = richiesteArray.getString(i);
                        listaRichieste.addElement(richiestaPlayer);
                    }

                    //Estrazione dell'array JSON di accettati
                    JSONArray playersArray = jsonObject.getJSONArray("accettati");

                    //Aggiorna lista players accettati
                    listaPlayers.clear();
                    for (int i = 0; i < playersArray.length(); i++) {
                        String playerAccettato = playersArray.getString(i);
                        listaPlayers.addElement(playerAccettato);
                    }
                }else if(tipoMessaggio.equals("rimozione")){

                    breakUpdateSquadre = false;
                    return 1;

                }else if(tipoMessaggio.equals("abbandonoCapitano")){

                    breakUpdateSquadre = false;
                    return 2;

                }else if(tipoMessaggio.equals("rispostaMatch")){

                    String response = in.readLine();

                    if(response != null){

                        if(response.equals("avvioMatch")){

                            //Recupera informazioni match
                            StringBuilder jsonBuilder = new StringBuilder();
                            line = in.readLine();
                            jsonBuilder.append(line);
                            String jsonResponse = jsonBuilder.toString();
                            System.out.println("oggetto ricevuto: " + jsonResponse);

                            //Deserializzazione della stringa JSON in un oggetto JSONObject
                            JSONObject jsonObject = new JSONObject(jsonResponse);

                            //Estrazione dell'array JSON di playersA e players B
                            JSONArray playersA = jsonObject.getJSONArray("playersA");
                            JSONArray playersB = jsonObject.getJSONArray("playersB");
                            String[] arrayPlayersA = new String[4];
                            String[] arrayPlayersB = new String[4];
                            for(int i=0;i<4;i++){

                                arrayPlayersA[i] = playersA.getString(i);
                                arrayPlayersB[i] = playersB.getString(i);
                            }

                            //Estrazione capitanoA e capitanoB
                            String capitanoA = jsonObject.getString("capitanoA");
                            String capitanoB = jsonObject.getString("capitanoB");

                            //Estrazione nome squadraA e squadraB
                            String squadraA = jsonObject.getString("squadraA");
                            String squadraB = jsonObject.getString("squadraB");

                            //Estrazione indice partita nel server
                            int indexPartita = jsonObject.getInt("indexPartita");

                            //Inizializza infoMAtch
                            infoMatch.setPlayersA(arrayPlayersA);
                            infoMatch.setPlayersB(arrayPlayersB);
                            infoMatch.setSquadraA(squadraA);
                            infoMatch.setSquadraB(squadraB);
                            infoMatch.setCapitanoA(capitanoA);
                            infoMatch.setCapitanoB(capitanoB);
                            infoMatch.setIndexPartita(indexPartita);

                            breakUpdateSquadre = false;
                            return 3;

                        }else if(response.equals("attesaMatch")){

                            infoMatch.setStatoMatch("Match avviato, in attesa di trovare una squadra avversaria");

                        }
                    }
                }else if(tipoMessaggio.equals("squadraNonPronta")){

                    infoMatch.setStatoMatch("Formazione squadra in corso");

                    if(isCapitano){

                        infoMatch.getStatoMatch().setVisible(false);
                        btnMatch.setVisible(true);
                    }
                }
            }
        }
    }

    public String cercaMatch(String squadra){

        //Creazione oggetto richiesta JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","cercaMatch\0");
        json.put("squadra",squadra + "\0");

        //Invia richiesta
        out.println(json.toString());

        //Attendi risposta
        String response;
        breakUpdateSpogliatoglio = true;
        synchronized (in){

            try{
                response = in.readLine();
            }catch (IOException ex){
                response = "ko";
            }

            breakUpdateSpogliatoglio = false;
            in.notifyAll();
        }

        return response;
    }

    public void sendRichiestaInizioTurno(InfoMatch infoMatch,String player, String squadra){

        //Creazione oggetto richiesta JSON
        JSONObject json = new JSONObject();
        json.put("tipoRichiesta","inizioTurno\0");
        json.put("squadra",squadra + "\0");
        json.put("player",player + "\0");
        json.put("indexPartita",infoMatch.getIndexPartita());

        //Invia richiesta
        out.println(json.toString());
    }

    public InfoFineMatch threadAscoltoPartita(SimulazioneMatch match, InfoMatch infoMatch) throws IOException{

        int turno = 2;
        int indiceListPlayer = -1;
        LogPlayer[] logPlayersA = new LogPlayer[5];
        LogPlayer[] logPlayersB = new LogPlayer[5];
        List<String> ritorniInfortunio = new ArrayList<>();
        List<String> ritorniPenalizzazione = new ArrayList<>();
        //Inizializzazioni array
        for(int i=0; i<5; i++){

            logPlayersA[i] = new LogPlayer(match.getModelListPlayerA().getElementAt(i));
            logPlayersB[i] = new LogPlayer(match.getModelListPlayerB().getElementAt(i));
        }
        boolean isSquadraA = false;

        String logMatch = new String("");

        InfoFineMatch infoFineMatch = null;

        while(true){

            String oggettoEvento;

            synchronized (in){

                JTextPane paneCronaca = match.getCronaca();

                //Deserializzazione oggettoJSON
                StringBuilder jsonBuilder = new StringBuilder();
                oggettoEvento = in.readLine();
                jsonBuilder.append(oggettoEvento);
                String jsonResponse = jsonBuilder.toString();
                System.out.println("oggetto ricevuto: " + jsonResponse);
                JSONObject jsonObject;
                String tipoEvento;
                try{
                     jsonObject = new JSONObject(jsonResponse);
                }catch (JSONException ex){

                    jsonObject = null;
                }

                if(jsonObject != null)
                    tipoEvento = jsonObject.getString("tipoEvento");
                else tipoEvento = "null";

                String player;
                String msg;
                Font font;

                switch (tipoEvento){

                    case "annullamentoMatch":

                        return new InfoFineMatch(null,null);

                    case "inizioMatch":

                        match.setAvvioPartita(true);

                        String inizioTurno = jsonObject.getString("playerInizioTurno");

                        System.out.println(inizioTurno);

                        logMatch = logMatch + "Match avviato, 5 minuti alla fine\n\n";

                        msg = "Match avviato, 5 minuti alla fine\n\n";
                        font = new Font("Verdana",Font.PLAIN,30);
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.getTime().setText("5 min");
                        try {
                            Thread.sleep(4000); // Pausa per 4 secondi
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //Aggiornamento log Match
                        logMatch = logMatch + "Il primo turno è assegnato al player " + inizioTurno + "\n\n";

                        msg = "Il primo turno è assegnato al player ";
                        font = new Font("Verdana",Font.PLAIN,25);
                        match.appendToPane(paneCronaca,msg, Color.BLACK, StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,inizioTurno+"\n\n",Color.GREEN, StyleConstants.ALIGN_CENTER,font);

                        //Aggiorna graficamente il player corrente
                        match.setPlayerAttualeRender(inizioTurno);
                        match.getListPlayersA().repaint();
                        match.getListPlayersA().revalidate();
                        match.getListPlayersB().repaint();
                        match.getListPlayersB().revalidate();

                        //Indice per aggiornare i log del player
                        indiceListPlayer = LogPlayer.getIndiceListPlayer(match.getModelListPlayerA(),inizioTurno);
                        isSquadraA = true;
                        if(indiceListPlayer == -1){

                            isSquadraA = false;
                            indiceListPlayer = LogPlayer.getIndiceListPlayer(match.getModelListPlayerB(),inizioTurno);
                            logPlayersB[indiceListPlayer].incrementaNumeroPossesso();
                        }
                        if(isSquadraA)
                            logPlayersA[indiceListPlayer].incrementaNumeroPossesso();

                        break;

                    case "assegnazioneTurno":

                        String playerTurno = jsonObject.getString("turnoPlayer");

                        System.out.println(playerTurno);

                        //Aggiornamento log match
                        logMatch = logMatch + "\nTurno " + turno  + " assegnato al player "  + playerTurno + "\n";

                        match.getCronaca().setText(""); //pulisci pane

                        for(String playerRitornato : ritorniInfortunio){

                            costruisciMessaggioDiRitorno(match,1,paneCronaca,playerRitornato);
                        }
                        ritorniInfortunio.clear();

                        for(String playerRitornato : ritorniPenalizzazione){

                            costruisciMessaggioDiRitorno(match,2,paneCronaca,playerRitornato);
                        }
                        ritorniPenalizzazione.clear();

                        match.getCronaca().setText(""); //pulisci pane

                        msg = "Turno " + turno  + " assegnato al player ";
                        font = new Font("Verdana",Font.PLAIN,25);
                        match.appendToPane(paneCronaca,msg, Color.BLACK, StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,playerTurno+"\n\n",Color.GREEN,StyleConstants.ALIGN_CENTER,font);

                        turno++;

                        //Aggiorna graficamente il player corrente
                        match.setPlayerAttualeRender(playerTurno);
                        match.getListPlayersA().repaint();
                        match.getListPlayersA().revalidate();
                        match.getListPlayersB().repaint();
                        match.getListPlayersB().revalidate();

                        //Indice per aggiornare i log del player
                        indiceListPlayer = LogPlayer.getIndiceListPlayer(match.getModelListPlayerA(),playerTurno);
                        isSquadraA = true;
                        if(indiceListPlayer == -1){

                            isSquadraA = false;
                            indiceListPlayer = LogPlayer.getIndiceListPlayer(match.getModelListPlayerB(),playerTurno);
                            logPlayersB[indiceListPlayer].incrementaNumeroPossesso();
                        }
                        if(isSquadraA)
                            logPlayersA[indiceListPlayer].incrementaNumeroPossesso();

                        break;

                    case "tiro":

                        String esitoTiro = jsonObject.getString("esitoTiro");
                        int turnoSquadra = jsonObject.getInt("turnoSquadra");

                        //Aggiornamento log match
                        logMatch = logMatch + "Tenta il tiro\n";

                        msg = "Tenta il tiro\n\n";
                        font = new Font("Verdana",Font.PLAIN,20);
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        try {
                            Thread.sleep(3000); // Pausa per 3 secondi
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //Aggiorna Log Tiri player
                        if(isSquadraA)
                            logPlayersA[indiceListPlayer].incrementaNumeroTiri();
                        else
                            logPlayersB[indiceListPlayer].incrementaNumeroTiri();

                        if(!esitoTiro.equals("goal")){

                            //Aggiornamento log match
                            logMatch = logMatch + "Il tiro non ha avuto successo\n";

                            msg = "Il tiro non ha avuto successo\n";
                            font = new Font("Verdana",Font.PLAIN,20);
                            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                            try {
                                Thread.sleep(2000); // Pausa per 2 secondi
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //Aggiorna Log Tiri Falliti player
                            if(isSquadraA)
                                logPlayersA[indiceListPlayer].incrementaNumeroTiriFalliti();
                            else
                                logPlayersB[indiceListPlayer].incrementaNumeroTiriFalliti();

                        }else {

                           //aggiornamento log match
                            logMatch = logMatch + "Ed è GOOOOOOAL!\n";

                            msg = "GOOOOOOOOOOOOOOOOOAL\n";
                            font = new Font("Verdana",Font.PLAIN,35);
                            match.appendToPane(paneCronaca,msg,Color.RED,StyleConstants.ALIGN_CENTER,font);
                            try {
                                Thread.sleep(2000); // Pausa per 2 secondi
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(turnoSquadra == 0) match.incrementaScoreA();
                            else match.incrementaScoreB();

                            //Aggiorna Log Goal player
                            if(isSquadraA)
                                logPlayersA[indiceListPlayer].incrementaNumeroGoal();
                            else
                                logPlayersB[indiceListPlayer].incrementaNumeroGoal();
                        }

                        break;

                    case "dribbling":

                        String esitoDribbling = jsonObject.getString("esitoDribbling");

                        //aggiormento log match
                        logMatch = logMatch + "Tenta un dribbling\n";

                        msg = "Tenta il dribbling\n\n";
                        font = new Font("Verdana",Font.PLAIN,20);
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        try {
                            Thread.sleep(3000); // Pausa per 3 secondi
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        //Aggiorna Log Dribbling player
                        if(isSquadraA)
                            logPlayersA[indiceListPlayer].incrementaNumeroDribbling();
                        else
                            logPlayersB[indiceListPlayer].incrementaNumeroDribbling();

                        if(!esitoDribbling.equals("ok")){

                            //aggironamento log match
                            logMatch = logMatch + "Dribbling fallito, possesso palla perso\n";

                            msg = "Dribbling fallito, possesso palla perso\n";
                            font = new Font("Verdana",Font.PLAIN,25);
                            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                            try {
                                Thread.sleep(2000); // Pausa per 2 secondi
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //Aggiorna Log Dribbling Falliti player
                            if(isSquadraA)
                                logPlayersA[indiceListPlayer].incrementaNumeroDribblingFallito();
                            else
                                logPlayersB[indiceListPlayer].incrementaNumeroDribblingFallito();

                        }else{

                            //aggiornamento log match
                            logMatch = logMatch + "Dribbling fatanstiscoo, ora ha spazio per tentare il tiro\n";

                            msg = "Dribbling fatanstiscoo, ora ha spazio per tentare il tiro\n\n";
                            font = new Font("Verdana",Font.PLAIN,25);
                            match.appendToPane(paneCronaca,msg,Color.YELLOW,StyleConstants.ALIGN_CENTER,font);
                            try {
                                Thread.sleep(2000); // Pausa per 2 secondi
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            //Aggiorna Log Dribbling Successo player
                            if(isSquadraA)
                                logPlayersA[indiceListPlayer].incrementaNumeroDribblingSuccesso();
                            else
                                logPlayersB[indiceListPlayer].incrementaNumeroDribblingSuccesso();
                        }

                        break;

                    case "infortunio":

                        int minutiInfortunio = jsonObject.getInt("minuti");
                        int minutiPlayerPenalizzato = jsonObject.getInt("minutiP");
                        String playerPenalizzato = jsonObject.getString("playerPenalizzato");
                        player = jsonObject.getString("turnoPlayer");

                        //aggiornamento logMatch
                        logMatch = logMatch + "Fallo da parte del player " + playerPenalizzato + "\n";
                        logMatch = logMatch + "Attenzione, il player ha subito un infortunio. Sarà indisponibile per " + minutiInfortunio + " minuti\n";
                        logMatch = logMatch + "il player  " + playerPenalizzato + "è stato penalizzato per " + minutiPlayerPenalizzato + "\n";

                        msg = "Attenzione, il player ha subito un ";
                        font = new Font("Verdana",Font.PLAIN,20);
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,"infortunio ",Color.RED,StyleConstants.ALIGN_CENTER,font);
                        msg = "per un fallo commesso da ";
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,playerPenalizzato,Color.RED,StyleConstants.ALIGN_CENTER,font);
                        msg = " Sarà indisponibile per ";
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        msg = minutiInfortunio + " minuti\n\n";
                        match.appendToPane(paneCronaca,msg,Color.RED,StyleConstants.ALIGN_CENTER,font);
                        font = new Font("Verdana",Font.PLAIN,20);
                        match.appendToPane(paneCronaca,"L'arbitro dà ",Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,String.valueOf(minutiPlayerPenalizzato),Color.RED,StyleConstants.ALIGN_CENTER,font);
                        msg = " minuti di penalizzazione al player ";
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,playerPenalizzato,Color.RED,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca," per il fallo commesso\n",Color.BLACK,StyleConstants.ALIGN_CENTER,font);

                        try {
                            Thread.sleep(6000); // Pausa per 6 secondi
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        match.addInfortunio(player);
                        match.addPenalizzazione(playerPenalizzato);
                        match.getListPlayersA().repaint();
                        match.getListPlayersA().revalidate();
                        match.getListPlayersB().repaint();
                        match.getListPlayersB().revalidate();

                        //Aggiorna Log minuti infortunio player
                        if(isSquadraA)
                            logPlayersA[indiceListPlayer].setMinutiInfortunati(minutiInfortunio);
                        else
                            logPlayersB[indiceListPlayer].setMinutiInfortunati(minutiInfortunio);

                        break;

                    case "ritornoInfortunio":

                        player = jsonObject.getString("turnoPlayer");
                        //aggiornamento log match
                        logMatch = logMatch + "Il player " + player + " torna dall'infortunio\n";

                        /*font = new Font("Verdana",Font.PLAIN,20);
                        match.appendToPane(paneCronaca,"\n Il player ",Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,player,Color.RED,StyleConstants.ALIGN_CENTER,font);
                        msg = " torna ad essere disponibile\n";
                        match.appendToPane(paneCronaca,msg,Color.RED,StyleConstants.ALIGN_CENTER,font);

                        try {
                            Thread.sleep(2000); // Pausa per 2 secondi
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        ritorniInfortunio.add(player);

                        match.removeInfortunio(player);
                        match.getListPlayersA().repaint();
                        match.getListPlayersA().revalidate();
                        match.getListPlayersB().repaint();
                        match.getListPlayersB().revalidate();

                        break;

                    case "ritornoPenalizzazione":

                        player = jsonObject.getString("playerRitornato");

                        //aggiornamento log match
                        logMatch = logMatch + "Il player " + player + " torna ad essere disponibile\n";

                        /*font = new Font("Verdana",Font.PLAIN,20);
                        match.appendToPane(paneCronaca,"\n Il player ",Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                        match.appendToPane(paneCronaca,player,Color.RED,StyleConstants.ALIGN_CENTER,font);
                        msg = " ha scontato la penalizzazione\n";
                        match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);

                        try {
                            Thread.sleep(2000); // Pausa per 2 secondi
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }*/

                        ritorniPenalizzazione.add(player);

                        match.removePenalizzazione(player);
                        match.getListPlayersA().repaint();
                        match.getListPlayersA().revalidate();
                        match.getListPlayersB().repaint();
                        match.getListPlayersB().revalidate();

                        break;

                    case "refreshTime":

                        String nuovoTime = jsonObject.getString("countDown");
                        System.out.println(nuovoTime);

                        if(nuovoTime.equals("4m")){

                            match.getTime().setText("4 min");
                            match.getTime().setForeground(Color.red);
                            try {
                                Thread.sleep(3000); // Pausa per 3 secondi (2000 millisecondi)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            match.getTime().setForeground(Color.WHITE);

                        }else if(nuovoTime.equals("3m")){

                            match.getTime().setText("3 min");
                            match.getTime().setForeground(Color.red);
                            try {
                                Thread.sleep(3000); // Pausa per 3 secondi (2000 millisecondi)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            match.getTime().setForeground(Color.WHITE);

                        }else if(nuovoTime.equals("2m")){

                            match.getTime().setText("2 min");
                            match.getTime().setForeground(Color.red);
                            try {
                                Thread.sleep(3000); // Pausa per 3 secondi (2000 millisecondi)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            match.getTime().setForeground(Color.WHITE);
                        }else {

                            match.getTime().setText("1 min");
                            match.getTime().setForeground(Color.red);
                            try {
                                Thread.sleep(3000); // Pausa per 3 secondi (3000 millisecondi)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }

                        break;

                    case "fineMatch":

                        match.getCronaca().setText(""); //pulisci pane
                        msg = "MATCH CONCLUSO\n";
                        font = new Font("Verdana",Font.PLAIN,30);
                        match.appendToPane(paneCronaca,msg, Color.BLACK, StyleConstants.ALIGN_CENTER,font);

                        String logStatistiche = new String("");

                        logStatistiche = logStatistiche + "--------------------" + infoMatch.getSquadraA() + "--------------------\n";

                        for(LogPlayer logPlayer : logPlayersA){

                            logStatistiche = logStatistiche + "PLAYER: " + logPlayer.getPlayer() + "\n";
                            logStatistiche = logStatistiche + "NUMERO DI TURNI GIOCATI: " + logPlayer.getNumeroDiPossesso() + "\n";
                            logStatistiche = logStatistiche + "NUMERO TIRI: " + logPlayer.getNumeroTiri() + "\n";
                            logStatistiche = logStatistiche + "TIRI FALLITI: " + logPlayer.getNumeroTiriFalliti() + "\n";
                            logStatistiche = logStatistiche + "GOAL: " + logPlayer.getNumeroGoal() + "\n";
                            logStatistiche = logStatistiche + "TENTATIVI DRIBBLING: " + logPlayer.getNumeroDribbling() + "\n";
                            logStatistiche = logStatistiche + "DRIBBLING RIUSCITI: " + logPlayer.getNumeroDribblingSuccesso() + "\n";
                            logStatistiche = logStatistiche + "DRIBBLING FALLITI: " + logPlayer.getNumeroDribblingFalliti() + "\n";
                            logStatistiche = logStatistiche + "MINUTI INFORTUNATI: " + logPlayer.getMinutiInfortunati() + "\n";

                            logStatistiche = logStatistiche + "--------------------\n";
                        }

                        logStatistiche = logStatistiche + "--------------------" + infoMatch.getSquadraB() + "--------------------\n";

                        for(LogPlayer logPlayer : logPlayersB){

                            logStatistiche = logStatistiche + "PLAYER: " + logPlayer.getPlayer() + "\n";
                            logStatistiche = logStatistiche + "NUMERO DI TURNI GIOCATI: " + logPlayer.getNumeroDiPossesso() + "\n";
                            logStatistiche = logStatistiche + "NUMERO TIRI: " + logPlayer.getNumeroTiri() + "\n";
                            logStatistiche = logStatistiche + "TIRI FALLITI: " + logPlayer.getNumeroTiriFalliti() + "\n";
                            logStatistiche = logStatistiche + "GOAL: " + logPlayer.getNumeroGoal() + "\n";
                            logStatistiche = logStatistiche + "TENTATIVI DRIBBLING: " + logPlayer.getNumeroDribbling() + "\n";
                            logStatistiche = logStatistiche + "DRIBBLING RIUSCITI: " + logPlayer.getNumeroDribblingSuccesso() + "\n";
                            logStatistiche = logStatistiche + "DRIBBLING FALLITI: " + logPlayer.getNumeroDribblingFalliti() + "\n";
                            logStatistiche = logStatistiche + "MINUTI INFORTUNATI: " + logPlayer.getMinutiInfortunati() + "\n";

                            logStatistiche = logStatistiche + "--------------------\n";

                            infoFineMatch = new InfoFineMatch(logMatch,logStatistiche);

                        }

                        return infoFineMatch;
                }
            }
        }
    }

    private void costruisciMessaggioDiRitorno(SimulazioneMatch match, int tipoRitorno, JTextPane paneCronaca, String player){

        if(tipoRitorno == 1){

            Font font = new Font("Verdana",Font.PLAIN,20);
            match.appendToPane(paneCronaca,"\n Il player ",Color.BLACK,StyleConstants.ALIGN_CENTER,font);
            match.appendToPane(paneCronaca,player,Color.RED,StyleConstants.ALIGN_CENTER,font);
            String msg = " torna ad essere disponibile\n";
            match.appendToPane(paneCronaca,msg,Color.RED,StyleConstants.ALIGN_CENTER,font);

            try {
                Thread.sleep(3000); // Pausa per 3 secondi
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }else{

            Font font = new Font("Verdana",Font.PLAIN,20);
            match.appendToPane(paneCronaca,"\n Il player ",Color.BLACK,StyleConstants.ALIGN_CENTER,font);
            match.appendToPane(paneCronaca,player,Color.RED,StyleConstants.ALIGN_CENTER,font);
            String msg = " ha scontato la penalizzazione\n";
            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);

            try {
                Thread.sleep(3000); // Pausa per 3 secondi
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}
