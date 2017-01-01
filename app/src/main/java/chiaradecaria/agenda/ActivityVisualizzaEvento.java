package chiaradecaria.agenda;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class ActivityVisualizzaEvento extends AppCompatActivity {
    DBManager db;
    long idEvento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_evento);
        idEvento = getIntent().getExtras().getLong("id_evento");
        Log.i("A.VisualizzaEvento", "id evento: " + idEvento);
        db = new DBManager(this);
        mostraEvento();
    }
    /**Metodo che prende l'evento dal db e lo mostra*/
    private void mostraEvento(){
        Cursor cursor = db.getEvento(idEvento);
        Log.i("A.VisualizzaEvento", "" + cursor.getColumnIndex(DBStrings.TITOLO_EVENTO));
        String titolo = cursor.getString(1/*cursor.getColumnIndex(DBStrings.TITOLO_EVENTO)*/);
        String luogo = cursor.getString(cursor.getColumnIndex(DBStrings.LUOGO));
        String data = cursor.getString(cursor.getColumnIndex(DBStrings.DATA));
        String oraInizio = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_INIZIO));
        String oraFine = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_FINE));
        ((EditText) findViewById(R.id.txtTitolo)).setText(titolo);
        ((EditText) findViewById(R.id.txtLuogo)).setText(luogo);
        ((EditText) findViewById(R.id.txtData)).setText(data);
        ((EditText) findViewById(R.id.txtOraInizio)).setText(oraInizio);
        ((EditText) findViewById(R.id.txtOraFine)).setText(oraFine);
    }
    /**Metodo che aggiorna l'evento*/
    private void aggiornaEvento(){
        String titolo = ((EditText) findViewById(R.id.txtTitolo)).getText().toString();
        String luogo = ((EditText) findViewById(R.id.txtLuogo)).getText().toString();
        String data = ((EditText) findViewById(R.id.txtData)).getText().toString();
        String oraInizio = ((EditText) findViewById(R.id.txtOraInizio)).getText().toString();
        String oraFine = ((EditText) findViewById(R.id.txtOraFine)).getText().toString();
        db.aggiornaEvento(idEvento, titolo, luogo, data, oraInizio, oraFine);
    }
    @Override
    protected void onDestroy() {
        //aggiornaEvento();
        super.onDestroy();
    }
}
