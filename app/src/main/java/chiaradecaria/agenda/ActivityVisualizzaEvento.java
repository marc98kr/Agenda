package chiaradecaria.agenda;
/**
 * @author Chiara De Caria, Michele Scarpelli
 */

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.icu.text.RelativeDateTimeFormatter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


public class ActivityVisualizzaEvento extends AppCompatActivity implements LocationListener {
    DBManager db;
    long idEvento;
    private LocationListener locationListener;
    Date inizioEvento;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visualizza_evento);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String luogo = ((EditText) findViewById(R.id.txtLuogo)).getText().toString();
                String partenza = location.getLatitude() + "," + location.getLongitude();
                Log.i("A.VisualizzaEventi", "Posizione: " + partenza);
                new RichiediDati().execute(partenza, luogo.replaceAll("\\s+", ""));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                Log.w("A.VisualizzaEventi", "Nuovo stato: " + status + " (" + provider + ")");
            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Log.w("A.VisualizzaEventi", "Provider disabilitato");
            }
        };
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Controllo se è garantito il permesso della posizione
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            Toast.makeText(this, "Il permesso per l'utilizzo della posizione non è attivo, impossibile determinare la distanza dal luogo dell'evento!", Toast.LENGTH_SHORT).show();
        else
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 1000, locationListener);
        idEvento = getIntent().getExtras().getLong("id_evento");
        Log.i("A.VisualizzaEvento", "id evento: " + idEvento);
        db = new DBManager(this);
        mostraEvento();
    }

    /**Metodo che prende l'evento dal db e lo mostra*/
    private void mostraEvento() {
        try {
            Cursor cursor = db.getEvento(idEvento);
            cursor.moveToFirst();
            Log.i("A.VisualizzaEvento", "" + cursor.getColumnIndex(DBStrings.TITOLO_EVENTO));
            String titolo = cursor.getString(cursor.getColumnIndex(DBStrings.TITOLO_EVENTO));
            String luogo = cursor.getString(cursor.getColumnIndex(DBStrings.LUOGO));
            String data = cursor.getString(cursor.getColumnIndex(DBStrings.DATA));
            String oraInizio = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_INIZIO));
            SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            inizioEvento = dateFormat.parse(oraInizio);
            String oraFine = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_FINE));
            ((EditText) findViewById(R.id.txtTitolo)).setText(titolo);
            ((EditText) findViewById(R.id.txtLuogo)).setText(luogo);
            ((EditText) findViewById(R.id.txtData)).setText(data);
            ((EditText) findViewById(R.id.txtOraInizio)).setText(oraInizio);
            ((EditText) findViewById(R.id.txtOraFine)).setText(oraFine);
        } catch (CursorIndexOutOfBoundsException ex) {
            Log.e("A.VisualizzaEvento", ex.getLocalizedMessage());
        } catch (ParseException e) {

        }
    }

    @Override
    public void onBackPressed() {
        aggiornaEvento();
    }

    /**Metodo che aggiorna i dati dell'evento all'interno del db*/
    private void aggiornaEvento() {
        String titolo = ((EditText) findViewById(R.id.txtTitolo)).getText().toString();
        String luogo = ((EditText) findViewById(R.id.txtLuogo)).getText().toString();
        String data = ((EditText) findViewById(R.id.txtData)).getText().toString();
        String oraInizio = ((EditText) findViewById(R.id.txtOraInizio)).getText().toString();
        String oraFine = ((EditText) findViewById(R.id.txtOraFine)).getText().toString();
        //Se uno dei campi risulta vuoto non salvo le modifiche
        if (titolo.isEmpty() || luogo.isEmpty() || data.isEmpty() || oraInizio.isEmpty() || oraFine.isEmpty())
            Toast.makeText(this, "Uno dei campi risulta vuoto!\nModifiche non salvate", Toast.LENGTH_SHORT).show();
        else {
            try{
                SimpleDateFormat formatoOrario = new SimpleDateFormat("HH:mm"); //Formato HH:MM 24 ore
                formatoOrario.setLenient(false);
                Date orarioInizio = formatoOrario.parse(oraInizio);
                Date orarioFine  = formatoOrario.parse(oraFine);
                if(orarioFine.before(orarioInizio)) {
                    Toast.makeText(this, "L'orario della fine deve essere successivo all'orario di inizio!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch(ParseException ex){
                Toast.makeText(this, "L'orario inserito è errato!", Toast.LENGTH_SHORT).show();
                return;
            }
            db.aggiornaEvento(idEvento, titolo, luogo, data, oraFine, oraInizio);
            Toast.makeText(this, "Evento salvato!", Toast.LENGTH_SHORT).show();
            this.finish();
        }
    }
    /**Metodo che estrae i dati dal documento xml ricevuto da google*/
    @Nullable
    private String[] estraiDati(String risposta) {
        try {
            DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            InputSource source = new InputSource();
            source.setCharacterStream(new StringReader(risposta));
            Document document = documentBuilder.parse(source);
            String durata = document.getElementsByTagName("duration").item(0).getTextContent();
            String distanza = document.getElementsByTagName("distance").item(0).getTextContent();
            durata = estraiDurata(durata);
            distanza = estraiDistanza(distanza);
            Log.i("A.VisualizzaEventi", "Dati: " + distanza + " (" + durata + ")");
            return new String[]{distanza, durata};
        } catch (Exception e) {
            Log.e("A.VisualizzaEvento", "Errore estraiDati(String): " + e.getLocalizedMessage());
            return null;
        }
    }

    private String estraiDistanza(String distanza) {
        distanza = distanza.substring(1, distanza.indexOf("\n", 1)).replaceAll("\\s", "");
        double d = Double.parseDouble(distanza);
        d /= 1000;
        return d + " km";
    }

    private String estraiDurata(String durata) {
        durata = durata.substring(1, durata.indexOf("\n", 1)).replaceAll("\\s", "");
        Log.i("A.VisualizzaEvento", durata);
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        long durataTot = Long.parseLong(durata);
        //La durata inviata dal server di Google Maps è espressa in secondi
        long ore = durataTot / 3600;
        long minuti = (durataTot % 3600) / 60;
        try {
            Date tempoNecessario = format.parse(ore + ":" + minuti);
            Date oraCorrente = new Date();
            Calendar c = Calendar.getInstance();
            c.setTime(oraCorrente);
            long tempoResiduo = oraCorrente.getTime() - inizioEvento.getTime();
            Log.i("A.VisualizzaEvento", "Tempo residuo " + tempoResiduo);
            if(tempoResiduo >= tempoNecessario.getTime()) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Sei in tempo per raggiungere il luogo dell'evento", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Sei in ritardo!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        } catch (ParseException e) {

        }
        if(ore == 0)
            return minuti + " minuti";
        if(ore == 1)
            durata = ore + " ora ";
        else
            durata = ore + " ore ";
        if(minuti == 1)
            durata = durata + " e " + minuti + " minuto";
        else if(minuti > 1)
            durata = durata + " e " + minuti + " minuti";

        return durata;
    }

    @Nullable
    private String richiediDistanza(String urlRichiesta, String urlParameters) {
        //Creo un oggetto di classe HttpURLConnection per stabilire la connessione
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlRichiesta);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "it-IT");
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append("\n");
            }
            Log.i("richiediDistanza", "dati ricevuti!");
            Log.i("richiediDistanza", "Dati: " + response);
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    /**Alla pressione del tasto Google Maps si aprirà l'applicazione Maps per visualizzare il logo dell'incontro direttamente sulla mappa, con la possibilità di ottenere le indicazioni o attivare il navigatore*/
    public void btnGoogleMapsOnClick(View view) {
        String luogo = ((EditText) findViewById(R.id.txtLuogo)).getText().toString();
        Uri uri = Uri.parse("geo:0,0?q=" + luogo);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage("com.google.android.apps.maps");
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(this, "Google Maps non installato su questo dispositivo!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onLocationChanged(Location location) {
        String luogo = ((EditText) findViewById(R.id.txtLuogo)).getText().toString();
        String partenza = location.getLatitude() + "," + location.getLongitude();
        new RichiediDati().execute(partenza, luogo.replaceAll("\\s+", ""));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ((LocationManager) getSystemService(LOCATION_SERVICE)).removeUpdates(locationListener);
        }
    }

    /**Necessario per eseguire operazioni tramite Internet*/
    private class RichiediDati extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground(String... params) {
            Log.i("RichiediDati", params[0]);
            try{
                String urlParameters = "fName=" + URLEncoder.encode("???", "UTF-8") + "&lName=" + URLEncoder.encode("???", "UTF-8");
                String url = "http://maps.googleapis.com/maps/api/distancematrix/xml?origins=" + params[0] + "&destinations=" + params[1] + "&sensor=false&mode=car";
                Log.i("RichiediDati", url);
                final String[] dati = estraiDati(richiediDistanza(url, urlParameters));
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dati == null)
                            ((TextView) findViewById(R.id.txtDistanzaTempo)).setText("Si è verificato un errore nella visualizzazione della distanza e del tempo richiesto.\nVerificare che il campo 'luogo' sia corretto.");
                        else
                            ((TextView) findViewById(R.id.txtDistanzaTempo)).setText(dati[0] + " (" + dati[1] + ")");
                    }
                });
            }catch(Exception ex){

            }
            return null;
        }
    }
}
