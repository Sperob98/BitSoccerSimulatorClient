package Connection;

import Entity.InfoMatch;
import Entity.LogPlayer;
import Entity.SimulazioneMatch;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONString;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.io.*;
import java.net.Socket;

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

    public int updateSpogliatoio(DefaultListModel<String> listaPlayers, DefaultListModel<String> listaRichieste, InfoMatch infoMatch) throws IOException{

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

    public void threadAscoltoPartita(SimulazioneMatch match) throws IOException{

        int turno = 2;

        while(true){

            String tipoMessaggio;
            String oggettoEvento;

            int indiceListPlayer;
            LogPlayer[] logPlayersA = new LogPlayer[5];
            LogPlayer[] logPlayersB = new LogPlayer[5];
            //Inizializzazioni array
            for(int i=0; i<5; i++){

                logPlayersA[i] = new LogPlayer(match.getModelListPlayerA().getElementAt(i));
                logPlayersB[i] = new LogPlayer(match.getModelListPlayerB().getElementAt(i));
            }

            synchronized (in){

                tipoMessaggio = in.readLine();
                System.out.println(tipoMessaggio);

                JTextPane paneCronaca = match.getCronaca();

                if(tipoMessaggio.equals("inizioMatch")){

                    match.setAvvioPartita(true);

                    String inizioTurno = in.readLine();

                    System.out.println(inizioTurno);

                    //match.getAreaCronaca().append("Match avviato, 5 minuti alla fine\n");
                    String msg = "Match avviato, 5 minuti alla fine\n\n";
                    Font font = new Font("Verdana",Font.PLAIN,30);
                    match.appendToPane(paneCronaca,msg,Color.GREEN,StyleConstants.ALIGN_CENTER,font);
                    try {
                        Thread.sleep(2000); // Pausa per 2 secondi (2000 millisecondi)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //match.getAreaCronaca().append("Il primo turno è assegnato al player " + inizioTurno + "\n\n" );
                    msg = "Il primo turno è assegnato al player " + inizioTurno + "\n";
                    font = new Font("Verdana",Font.PLAIN,30);
                    match.appendToPane(paneCronaca,msg, Color.BLACK, StyleConstants.ALIGN_CENTER,font);
                    try {
                        Thread.sleep(4000); // Pausa per 4 secondi (2000 millisecondi)
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    //Aggiorna graficamente il player corrente
                    match.setPlayerAttualeRender(inizioTurno);
                    match.getListPlayersA().repaint();
                    match.getListPlayersA().revalidate();
                    match.getListPlayersB().repaint();
                    match.getListPlayersB().revalidate();

                    //Indice per aggiornare i log del player
                    indiceListPlayer = LogPlayer.getIndiceListPlayer(match.getModelListPlayerA(),inizioTurno);
                }

                if(tipoMessaggio.equals("assegnazioneTurno")){

                    String playerTurno = in.readLine();

                    System.out.println(playerTurno);

                    //match.getAreaCronaca().append("\nTurno " + turno  + " assegnato al player "  + playerTurno + "\n");
                    match.getCronaca().setText(""); //pulisci pane
                    String msg = "Turno " + turno  + " assegnato al player "  + playerTurno + "\n\n";
                    Font font = new Font("Verdana",Font.PLAIN,30);
                    match.appendToPane(paneCronaca,msg, Color.GREEN, StyleConstants.ALIGN_CENTER,font);

                    turno++;

                    //Aggiorna graficamente il player corrente
                    match.setPlayerAttualeRender(playerTurno);
                    match.getListPlayersA().repaint();
                    match.getListPlayersA().revalidate();
                    match.getListPlayersB().repaint();
                    match.getListPlayersB().revalidate();

                }

                if(tipoMessaggio.equals("evento")){

                    //Deserializzazione oggettoJSON
                    StringBuilder jsonBuilder = new StringBuilder();
                    oggettoEvento = in.readLine();
                    jsonBuilder.append(oggettoEvento);
                    String jsonResponse = jsonBuilder.toString();
                    System.out.println("oggetto ricevuto: " + jsonResponse);

                    JSONObject jsonObject = new JSONObject(jsonResponse.toString());

                    String tipoEvento = jsonObject.getString("tipoEvento");
                    String player = jsonObject.getString("turnoPlayer");

                    switch (tipoEvento){

                        case "tiro":

                            String esitoTiro = jsonObject.getString("esitoTiro");
                            int turnoSquadra = jsonObject.getInt("turnoSquadra");

                            //match.getAreaCronaca().append("Tenta il tiro\n");
                            String msg = "Tenta il tiro\n\n";
                            Font font = new Font("Verdana",Font.PLAIN,35);
                            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                            try {
                                Thread.sleep(4000); // Pausa per 2 secondi (2000 millisecondi)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(!esitoTiro.equals("goal")){

                                //match.getAreaCronaca().append("Il tiro non ha avuto successo\n");
                                msg = "Il tiro non ha avuto successo\n";
                                font = new Font("Verdana",Font.PLAIN,35);
                                match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);

                            }else {

                               //match.getAreaCronaca().append("Ed è GOOOOOOAL!\n");
                                msg = "Ed è GOOOOOOOOOAL\n";
                                font = new Font("Verdana",Font.PLAIN,40);
                                match.appendToPane(paneCronaca,msg,Color.RED,StyleConstants.ALIGN_CENTER,font);

                                if(turnoSquadra == 0) match.incrementaScoreA();
                                else match.incrementaScoreB();
                            }

                            break;

                        case "dribbling":

                            String esitoDribbling = jsonObject.getString("esitoDribbling");

                            //match.getAreaCronaca().append("Tenta un dribbling\n");
                            msg = "Tenta il dribbling\n\n";
                            font = new Font("Verdana",Font.PLAIN,30);
                            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                            try {
                                Thread.sleep(4000); // Pausa per 2 secondi (2000 millisecondi)
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                            if(!esitoDribbling.equals("ok")){

                                //match.getAreaCronaca().append("Dribbling fallito, possesso palla perso\n");
                                msg = "Dribbling fallito, possesso palla perso\n";
                                font = new Font("Verdana",Font.PLAIN,30);
                                match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);

                            }else{

                                //match.getAreaCronaca().append("Dribbling fatanstiscoo, ora ha spazio per tentare il tiro\n");
                                msg = "Dribbling fatanstiscoo, ora ha spazio per tentare il tiro\n\n";
                                font = new Font("Verdana",Font.PLAIN,30);
                                match.appendToPane(paneCronaca,msg,Color.YELLOW,StyleConstants.ALIGN_CENTER,font);
                            }

                            break;

                        case "infortunio":

                            int minutiInfortunio = jsonObject.getInt("minuti");

                            //match.getAreaCronaca().append("Turno di " + player + "\n");
                            //match.getAreaCronaca().append("Attenzione, il player ha subito un infortunio. Sarà indisponibile per " + minutiInfortunio + " minuti\n");
                            msg = "Attenzione, il player ha subito un infortunio. Sarà indisponibile per " + minutiInfortunio + " minuti\n";
                            font = new Font("Verdana",Font.PLAIN,30);
                            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);

                            match.setPlayerInfortunato(player);
                            match.getListPlayersA().repaint();
                            match.getListPlayersA().revalidate();
                            match.getListPlayersB().repaint();
                            match.getListPlayersB().revalidate();

                            break;

                        case "ritornoInfortunio":

                            //match.getAreaCronaca().append("Il player " + player + " torna ad essere disponibile\n");
                            msg = "Il player" +  player + " torna ad essere disponibile\n";
                            font = new Font("Verdana",Font.PLAIN,20);
                            match.appendToPane(paneCronaca,msg,Color.BLACK,StyleConstants.ALIGN_CENTER,font);
                            match.setPlayerInfortunato("null");
                            match.getListPlayersA().repaint();
                            match.getListPlayersA().revalidate();
                            match.getListPlayersB().repaint();
                            match.getListPlayersB().revalidate();

                            break;
                    }
                }
            }
        }
    }

    public void stopConnection() throws IOException {
        in.close();
        out.close();
        clientSocket.close();
    }
}