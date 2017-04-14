package chiaradecaria.agenda;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;
/**
 * @author Chiara De Caria
 * Activity che mostra gli eventi di una determinata data, consente l'aggiunta di un nuovo evento e la modifica di un evento selezionato.
 * */
public class ActivityEventi extends AppCompatActivity {
    private DBManager dbManager;
    private CursorAdapter adapterEventi;
    private ListView listViewEventi;
    String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eventi);
        data = getIntent().getStringExtra("data");
        ((TextView) findViewById(R.id.txtViewData)).setText("Eventi del\n" + data);
        Log.i("ActivityEventi", "Data ricevuta: " + data);
        dbManager = new DBManager(this);
        listViewEventi = (ListView) findViewById(R.id.listViewEventi);
        Cursor cursor = dbManager.getEventi(data);
        adapterEventi = new CursorAdapter(this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                View view = getLayoutInflater().inflate(R.layout.layout_lista_eventi, null);
                return view;
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                String id = cursor.getString(cursor.getColumnIndex(DBStrings.ID));
                Log.i("ActivityEventi", "ID Evento " + id);
                String titolo = cursor.getString(cursor.getColumnIndex(DBStrings.TITOLO_EVENTO));
                String luogo = cursor.getString(cursor.getColumnIndex(DBStrings.LUOGO));
                String data = cursor.getString(cursor.getColumnIndex(DBStrings.DATA));
                String oraInizio = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_INIZIO));
                String oraFine = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_FINE));
                ((TextView) view.findViewById(R.id.txtViewTitolo)).setText(titolo);
                ((TextView) view.findViewById(R.id.txtViewLuogo)).setText(luogo);
                ((TextView) view.findViewById(R.id.txtViewOraInizio)).setText(oraInizio);
                ((TextView) view.findViewById(R.id.txtViewOraFine)).setText(oraFine);
            }
            @Override
            public long getItemId(int position) {
                Cursor crs = adapterEventi.getCursor();
                crs.moveToPosition(position);
                return crs.getLong(crs.getColumnIndex(DBStrings.ID));
            }
        };
        listViewEventi.setAdapter(adapterEventi);
        listViewEventi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                visualizzaEvento(id);
            }
        });
    }
    private void visualizzaEvento(long id){
        Intent intent = new Intent(ActivityEventi.this, ActivityVisualizzaEvento.class);
        intent.putExtra("id_evento", id);
        startActivity(intent);
        this.finish();
    }
    public void btnAggiungiEventoOnClick(View view) {
        Intent intent = new Intent(this, ActivityAggiungiEvento.class);
        intent.putExtra("data", data);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Aggiorno la lista soltanto se è stato aggiunto un evento
        if(requestCode == 1){
            Log.i("ActivityEventi", "Aggiunta terminata");
            setResult(resultCode);
            if(resultCode == 1){
                Log.i("ActivityEventi", "Evento aggiunto!");
                adapterEventi.changeCursor(dbManager.getEventi(this.data));
            }
        }
    }
}
