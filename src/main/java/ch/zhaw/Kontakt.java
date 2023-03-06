package ch.zhaw;

public class Kontakt {
    private String name;
    private int jahrgang;
    private String stadt;

    public Kontakt(String name, int jahrgang, String stadt) {
        this.name = name;
        this.jahrgang = jahrgang;
        this.stadt = stadt;
    }

    public String getName() {
        return name;
    }

    public int getJahrgang() {
        return jahrgang;
    }

    public String getStadt() {
        return stadt;
    }
}
