package chiaraemichele.Agenda;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Chiara De Caria, Michele Scarpelli
 * Classe contentene i dati dell'evento.
 */

public class Evento {
    private long idEvento;
    private String titolo, oraInizio, oraFine;
    private Date data;
    private String luogo;

    public Evento(long idEvento, String titolo, String oraInizio, String oraFine, String data, String luogo) throws ParseException{
        this.idEvento = idEvento;
        this.titolo = titolo;
        this.oraInizio = oraInizio;
        this.oraFine = oraFine;
        DateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);
        this.data = formatoData.parse(data);
        this.luogo = luogo;

    }

    public long getIdEvento() {
        return idEvento;
    }

    public void setIdEvento(long idEvento) {
        this.idEvento = idEvento;
    }

    public String getTitolo() {
        return titolo;
    }

    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    public String getOraInizio() {
        return oraInizio;
    }

    public void setOraInizio(String oraInizio) {
        this.oraInizio = oraInizio;
    }

    public String getOraFine() {
        return oraFine;
    }

    public void setOraFine(String oraFine) {
        this.oraFine = oraFine;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }

    public String getLuogo() {
        return luogo;
    }

    public void setLuogo(String luogo) {
        this.luogo = luogo;
    }
}
