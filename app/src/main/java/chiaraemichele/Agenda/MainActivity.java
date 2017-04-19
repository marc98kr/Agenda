package chiaraemichele.Agenda;

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
import android.widget.Toast;

import java.util.Calendar;

/**
 * @author Chiara De Caria, Michele Scarpelli
 * Activity nella quale viene mostrato il calendario e gli eventi del giorno corrente.
 * */
public class MainActivity extends AppCompatActivity {
    private DBManager dbManager;
    private CursorAdapter listaEventi;
    private ListView listViewEventi;
    private String data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbManager = new DBManager(this);
        listViewEventi = (ListView) findViewById(R.id.listViewEventiOggi);
        //Ottengo la data odierna...
        Calendar calendar = Calendar.getInstance();
        //... e la trasformo in stringa
        data = "" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH) + 1) + "/" + calendar.get(Calendar.YEAR);
        Log.i("MainActivity", "Data di oggi: " + data);
        Cursor cursor = dbManager.getEventi(data);
        listaEventi = new CursorAdapter(this, cursor, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup parent) {
                Log.i("Adapter", "newView");
                View view = getLayoutInflater().inflate(R.layout.layout_lista_eventi, null);
                return view;
            }

            @Override
            public void bindView(final View view, Context context, Cursor cursor) {
                String titolo = cursor.getString(cursor.getColumnIndex(DBStrings.TITOLO_EVENTO));
                String luogo = cursor.getString(cursor.getColumnIndex(DBStrings.LUOGO));
                final String data = cursor.getString(cursor.getColumnIndex(DBStrings.DATA));
                String oraInizio = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_INIZIO));
                String oraFine = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_FINE));
                Log.i("MainActivity", data);
                ((TextView) view.findViewById(R.id.txtViewTitolo)).setText(titolo);
                ((TextView) view.findViewById(R.id.txtViewLuogo)).setText(luogo);
                ((TextView) view.findViewById(R.id.txtViewOraInizio)).setText(oraInizio);
                ((TextView) view.findViewById(R.id.txtViewOraFine)).setText(oraFine);
                view.findViewById(R.id.btnEliminaEvento).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        view.findViewById(R.id.btnEliminaEvento).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int posizione = listViewEventi.getPositionForView(v);
                                long id = listaEventi.getItemId(posizione);
                                if(dbManager.eliminaEvento(id)) {
                                    listaEventi.changeCursor(dbManager.getEventi(data));
                                    Toast.makeText(getApplicationContext(), "Evento eliminato!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public long getItemId(int position) {
                Cursor crs = listaEventi.getCursor();
                crs.moveToPosition(position);
                return crs.getLong(crs.getColumnIndex(DBStrings.ID));
            }
        };
        listViewEventi.setAdapter(listaEventi);
        listViewEventi.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, ActivityVisualizzaEvento.class);
                intent.putExtra("id_evento", id);
                startActivity(intent);
            }
        });
    }
    public void salva(){
        /*String titolo, luogo, data, oraInizio, oraFine;
        titolo = ((EditText) findViewById(R.id.txtViewTitolo)).getText().toString();
        luogo = ((EditText) findViewById(R.id.txtViewLuogo)).getText().toString();
        data = ((EditText) findViewById(R.id.txtData)).getText().toString();
        oraInizio = ((EditText) findViewById(R.id.txtOraInizio)).getText().toString();
        oraFine = ((EditText) findViewById(R.id.txtOraFine)).getText().toString();
        dbManager.aggiungiEvento(titolo, data, luogo, oraInizio, oraFine);
        listaEventi.changeCursor(dbManager.getEventi());*/
    }
    private void aggiornaListaEventi(){
        listaEventi.changeCursor(dbManager.getEventi(data));
    }

    public void btnSalvaOnClick(View view) {
        salva();
    }

}
