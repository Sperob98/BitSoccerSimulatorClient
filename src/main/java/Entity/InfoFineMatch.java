package Entity;

public class InfoFineMatch {

    private String logMatch;

    private String logStatistiche;

    public InfoFineMatch(String logMatch, String logStatistiche ){

        this.logMatch = logMatch;

        this.logStatistiche = logStatistiche;
    }

    public String getLogMatch() {
        return logMatch;
    }

    public String getLogStatistiche() {
        return logStatistiche;
    }
}
