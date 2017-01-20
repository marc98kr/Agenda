package chiaradecaria.agenda;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;



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
        if(titolo.isEmpty() || luogo.isEmpty() || oraInizio.isEmpty() || oraFine.isEmpty())
            Toast.makeText(this, "Uno dei campi risulta vuoto!\nImpossibile aggiungere l'evento.", Toast.LENGTH_SHORT).show();
        else{
            dbManager.aggiungiEvento(titolo, data, luogo, oraInizio, oraFine);
            setResult(1);
        }
        this.finish();
    }
    @Override
    public void onBackPressed() {
        setResult(0);
        this.finish();
    }
}
