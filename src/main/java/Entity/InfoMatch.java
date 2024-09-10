package Entity;

import javax.swing.*;

public class InfoMatch {

    private JLabel statoMatch = new JLabel("Formazione squadra in corso");

    private String squadraA;

    private String squadraB;

    private String capitanoA;

    private String capitanoB;

    private String[] playersA = new String[4];

    private String[] playersB = new String[4];

    private int indexPartita;

    public String getCapitanoA() {
        return capitanoA;
    }

    public String getCapitanoB() {
        return capitanoB;
    }

    public String[] getPlayersA() {
        return playersA;
    }

    public String[] getPlayersB() {
        return playersB;
    }

    public JLabel getStatoMatch() {
        return statoMatch;
    }

    public String getSquadraA() {
        return squadraA;
    }

    public String getSquadraB() {
        return squadraB;
    }

    public int getIndexPartita() {
        return indexPartita;
    }

    public void setStatoMatch(String statoMatch) {
        this.statoMatch.setText(statoMatch);
    }

    public void setSquadraA(String squadraA) {
        this.squadraA = squadraA;
    }

    public void setSquadraB(String squadraB) {
        this.squadraB = squadraB;
    }

    public void setStatoMatch(JLabel statoMatch) {
        this.statoMatch = statoMatch;
    }

    public void setCapitanoA(String capitanoA) {
        this.capitanoA = capitanoA;
    }

    public void setCapitanoB(String capitanoB) {
        this.capitanoB = capitanoB;
    }

    public void setPlayersA(String[] playersA) {
        this.playersA = playersA;
    }

    public void setPlayersB(String[] playersB) {
        this.playersB = playersB;
    }

    public void setIndexPartita(int indexPartita) {
        this.indexPartita = indexPartita;
    }
}
