package chiaradecaria.agenda;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.util.Log;

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
            return false;
        }
    }
    /**Metodo che prende tutti gli eventi dal database*/
    public Cursor getEventi(){
        Cursor cursor;
        try{
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            cursor = db.query(DBStrings.NOME_TABELLA, null, null, null, null, null, null);
        }catch(SQLiteException ex){
            Log.e("DBManager", ex.getLocalizedMessage());
            return null;
        }
        return cursor;
    }
    /**Metodo che prende gli eventi di una certa data*/
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
            Log.e("DBManager", ex.getLocalizedMessage());
            return null;
        }
        return cursor;
    }
    public String[] getColumnNames(){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DBStrings.NOME_TABELLA, null, null, null, null, null, null);
        String[] columnNames = cursor.getColumnNames();
        return columnNames;
    }
}
