package chiaradecaria.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author Chiara De Caria
 */

public class DBManager {
    private DBHelper dbHelper;
    public DBManager(Context applicationContext){
        dbHelper = new DBHelper(applicationContext);
    }
    /**Metodo che si occupa dell'aggiunta di un evento*/
    public boolean aggiungiEvento(String titoloEvento, String data, String luogo, String oraInizio, String oraFine){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues nuovaRiga = new ContentValues();
        nuovaRiga.put(DBStrings.TITOLO_EVENTO, titoloEvento);
        nuovaRiga.put(DBStrings.DATA, data);
        nuovaRiga.put(DBStrings.LUOGO, luogo);
        nuovaRiga.put(DBStrings.ORA_INIZIO, oraInizio);
        nuovaRiga.put(DBStrings.ORA_FINE, oraFine);
        try{
            db.insert(DBStrings.NOME_TABELLA, null, nuovaRiga);
        }catch (SQLiteException ex){
            Log.e("DBManager", ex.getLocalizedMessage());
            return false;
        }
        Log.i("DBManager", "Evento: " + titoloEvento + " " + data + " salvato!");
        return true;
    }
    /**Metodo che si occupa dell'eliminazione di un evento*/
    public boolean eliminaEvento(long id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try{
            if(db.delete(DBStrings.NOME_TABELLA, DBStrings.ID + "=?", new String[]{Long.toString(id)}) > 0)
              return true;
            return false;
        }catch(SQLiteException ex){
            Log.i("DBManager", "Errore eliminaEvento(long): " + ex.getLocalizedMessage());
            return false;
        }
    }
    /**Metodo che restituisce tutti gli eventi dal database*/
    /*public Cursor getEventi(){
        Cursor cursor;
        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(DBStrings.NOME_TABELLA, null, null, null, null, null, null);
        }catch(SQLiteException ex){
            Log.e("DBManager", "Errore getEventi(): " + ex.getLocalizedMessage());
            return null;
        }
        return cursor;
    }*/
    /**Metodo che restituisce gli eventi di una certa data*/
    public Cursor getEventi(String data){
        Cursor cursor;
        try{
            Log.i("DBManager", "Cerco gli eventi di data " + data);
            // Creo un oggetto di classe SQLiteDatabase per leggere il DB
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            //Creo la query di selezione
            String query = "SELECT * FROM " + DBStrings.NOME_TABELLA + " WHERE " + DBStrings.NOME_TABELLA + "." + DBStrings.DATA + " = '" + data + "';";
            Log.i("DBManager", query);
            //Eseguo la query
            cursor = db.rawQuery(query, null);
        }catch(SQLiteException ex){
            Log.e("DBManager", "Errore getEventi(String): " + ex.getLocalizedMessage());
            return null;
        }
        return cursor;
    }
    /**Metodo che ritorna una lista contenenti tutti gli eventi*/
    public List<Evento> getEventi(){
        Cursor cursor;
        List<Evento> eventi = new ArrayList<Evento>();
        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(DBStrings.NOME_TABELLA, null, null, null, null, null, null);
            if(cursor.moveToFirst()){
                Date dataOggi = new Date();
                do{
                    try {
                        long id = cursor.getLong(cursor.getColumnIndex(DBStrings.ID));
                        String titolo = cursor.getString(cursor.getColumnIndex(DBStrings.TITOLO_EVENTO));
                        String oraInizio = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_INIZIO));
                        String oraFine = cursor.getString(cursor.getColumnIndex(DBStrings.ORA_FINE));
                        String data = cursor.getString(cursor.getColumnIndex(DBStrings.DATA));
                        String luogo = cursor.getString(cursor.getColumnIndex(DBStrings.LUOGO));
                        Evento e = new Evento(id, titolo, oraInizio, oraFine, data, luogo);
                        //if(e.getData().after(dataOggi) || e.getData().equals(dataOggi))
                            eventi.add(e);
                    } catch (ParseException e) {
                        Log.i("DBManager", "Errore: " + e.getMessage());
                        return null;
                    }
                }while(cursor.moveToNext());
                cursor.close();
                return eventi;
            }
        }catch(SQLiteException ex){
            Log.e("DBManager", "Errore getEventi(): " + ex.getLocalizedMessage());
            return null;
        }
        return eventi;
    }
    /**Metodo che ritorna un evento dato un ID*/
    public Cursor getEvento(long id){
        Cursor cursor = null;
        try{
            Log.i("DBManager", "Cerco l'evento con id " + id);
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM " + DBStrings.NOME_TABELLA + " WHERE " + DBStrings.NOME_TABELLA + "." + DBStrings.ID + " = " + id + ";";
            Log.i("DBManager", query);
            cursor = db.rawQuery(query, null);
        }catch(SQLiteException ex){
            Log.e("DBManager", "Errore getEvento(long): " + ex.getLocalizedMessage());
        }
        return cursor;
    }
    public String[] getColumnNames(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBStrings.NOME_TABELLA, null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        return columnNames;
    }
    /**Metodo che aggiorna un evento*/
    public void aggiornaEvento(long id, String titolo, String luogo, String data, String oraFine, String oraInizio){
        try{
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            //Creo un oggetto di classe ContentValues che conterrà i dati.
            ContentValues cv = new ContentValues();
            cv.put(DBStrings.TITOLO_EVENTO, titolo);
            cv.put(DBStrings.DATA, data);
            cv.put(DBStrings.LUOGO, luogo);
            cv.put(DBStrings.ORA_INIZIO, oraInizio);
            cv.put(DBStrings.ORA_FINE, oraFine);
            //Aggiorno il db
            db.update(DBStrings.NOME_TABELLA, cv, DBStrings.ID + " = ?", new String[]{"" + id});
        }catch(SQLiteException ex){
            Log.i("DBManager", "Errore aggiornaEvento(long, String, String, String, String, String): " + ex.getLocalizedMessage());
        }
    }
}
