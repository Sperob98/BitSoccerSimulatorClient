package Entity;

import javax.swing.*;

public class LogPlayer {

    private String player;

    int numeroDiPossesso = 0;

    private int numeroTiri = 0;

    private int numeroGoal = 0;

    private int numeroTiriFalliti = 0;

    private int numeroDribbling = 0;

    private int numeroDribblingSuccesso = 0;

    private int numeroDribblingFalliti = 0;

    private int minutiInfortunati = 0;

    public LogPlayer(String player){

        this.player = player;
    }

    public String getPlayer() {
        return player;
    }

    public int getNumeroTiri() {
        return numeroTiri;
    }

    public int getNumeroGoal() {
        return numeroGoal;
    }

    public int getNumeroTiriFalliti() {
        return numeroTiriFalliti;
    }

    public int getNumeroDribbling() {
        return numeroDribbling;
    }

    public int getNumeroDribblingSuccesso() {
        return numeroDribblingSuccesso;
    }

    public int getNumeroDribblingFalliti() {
        return numeroDribblingFalliti;
    }

    public int getMinutiInfortunati() {
        return minutiInfortunati;
    }

    public int getNumeroDiPossesso() {
        return numeroDiPossesso;
    }

    public void setMinutiInfortunati(int minutiInfortunati) {
        this.minutiInfortunati = minutiInfortunati;
    }

    public void incrementaNumeroPossesso(){

        numeroDiPossesso++;
    }

    public void incrementaNumeroTiri(){

        numeroTiri++;
    }

    public void incrementaNumeroGoal(){

        numeroGoal++;
    }

    public void incrementaNumeroTiriFalliti(){

        numeroTiriFalliti++;
    }

    public void incrementaNumeroDribbling(){

        numeroDribbling++;
    }

    public void incrementaNumeroDribblingSuccesso(){

        numeroDribblingSuccesso++;
    }

    public void incrementaNumeroDribblingFallito(){

        numeroDribblingFalliti++;
    }

    public static int getIndiceListPlayer(DefaultListModel<String> modelListPlayer, String turnoPlayer){

        int i = 0;

        String[] listPlayers = new String[5];
        for(int j=0; j<5; j++){
            listPlayers[j] = modelListPlayer.getElementAt(j);
        }

        for(String player : listPlayers){

            if(player.equals(turnoPlayer) || player.equals(turnoPlayer + " (capitano)")){

                return i;
            }

            i++;
        }

        return -1;
    }
}
