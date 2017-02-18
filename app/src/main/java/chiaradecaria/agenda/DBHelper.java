package chiaradecaria.agenda;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * @author Chiara De Caria
 */

public class DBHelper extends SQLiteOpenHelper{
    public static final String NOME_DB="DBEventi";

    public DBHelper(Context applicationContext){
        super(applicationContext, NOME_DB, null, 3);
    }
    /**Gestione della creazione del db*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("DBHelper", "Creo il db...");
        //Creo una stringa contenente la query per la creazione del DB
        String queryCreazione = "CREATE TABLE " + DBStrings.NOME_TABELLA + "( " +
                                "_id INTEGER PRIMARY KEY AUTOINCREMENT," +
                                DBStrings.TITOLO_EVENTO + " TEXT," +
                                DBStrings.DATA + " TEXT," +
                                DBStrings.LUOGO + " TEXT," +
                                DBStrings.ORA_INIZIO + " TEXT," +
                                DBStrings.ORA_FINE + " TEXT );";
        db.execSQL(queryCreazione);
    }
    /**Gestione dell'aggiornamento del db*/
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DBStrings.NOME_TABELLA);
        onCreate(db);
    }
}
