package chiaraemichele.Agenda;
/**
 * @author Chiara De Caria, Michele Scarpelli
 * Activity che permette la creazione di un nuovo evento e quindi l'aggiunta dei dati all'interno del db locale.
 */
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class ActivityAggiungiEvento extends AppCompatActivity {
    DBManager dbManager;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aggiungi_evento);
        data = getIntent().getStringExtra("data");
        Log.i("ActivityAggiungiEvento", "Data: " + data);
        this.setTitle("Aggiungi un evento per il " + data);
        dbManager = new DBManager(this);
    }
    public void btnSalvaOnClick(View view) {
        String titolo, luogo, oraInizio, oraFine;
        titolo = ((EditText) findViewById(R.id.txtTitolo)).getText().toString();
        luogo = ((EditText) findViewById(R.id.txtLuogo)).getText().toString();
        oraInizio = ((EditText) findViewById(R.id.txtOrarioInizio)).getText().toString();
        oraFine = ((EditText) findViewById(R.id.txtOrarioFine)).getText().toString();
        //Se un campo risulta vuoto
        if(titolo.isEmpty() || luogo.isEmpty() || oraInizio.isEmpty() || oraFine.isEmpty()) //Visualizza un toast per segnalare un campo vuoto
            Toast.makeText(this, "Uno o più campi risultano vuoti!", Toast.LENGTH_SHORT).show();
        else{
            try{
                //Converto le stringhe degli orari in Date per verificare l'orario
                SimpleDateFormat formatoOrario = new SimpleDateFormat("HH:mm"); //Formato HH:MM 24 ore
                formatoOrario.setLenient(false);
                Date orarioInizio = formatoOrario.parse(oraInizio);
                Date orarioFine  = formatoOrario.parse(oraFine);
                if(orarioFine.before(orarioInizio)){
                    Toast.makeText(this, "L'orario della fine deve essere successivo all'orario di inizio!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch(ParseException ex){
                //Eccezione lanciata in caso di orario non valido
                Toast.makeText(this, "Errore inserimento orario!", Toast.LENGTH_SHORT).show();
                Log.e("ActivityAggiungiEvento", ex.getLocalizedMessage());
                //Interrompo il metodo
                return;
            }
            /**Se gli orari inseriti sono corretti e i campi sono stati riempiti, aggiungo l'evento nel database,
             * imposto il risultato dell'activity ad 1 così da segnalare all'activity precedente l'aggiunta di un nuovo evento*/
            dbManager.aggiungiEvento(titolo, data, luogo, oraInizio, oraFine);
            setResult(1);
            this.finish();
        }
    }
    @Override
    public void onBackPressed() {
        //Nessun evento aggiunto, imposto il risultato a 0
        setResult(0);
        this.finish();
    }
}
