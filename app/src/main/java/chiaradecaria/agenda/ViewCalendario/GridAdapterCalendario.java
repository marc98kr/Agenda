package chiaradecaria.agenda.ViewCalendario;

/**
 *  @author Chiara De Caria
 */
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chiaradecaria.agenda.DBManager;
import chiaradecaria.agenda.Evento;
import chiaradecaria.agenda.R;

public class GridAdapterCalendario extends ArrayAdapter{
    private LayoutInflater inflater;
    private List<Date> giorni;
    private Calendar dataCorrente;
    private String[] giorniFestivi = {"1/1", "6/1", "25/4", "1/5", "2/6", "15/8", "1/11", "8/12", "25/12", "26/12"};
    DBManager dbManager;
    public GridAdapterCalendario(Context context, List<Date> giorni, Calendar dataCorrente){
        super(context, R.layout.layout_cella_calendario);
        this.giorni = giorni;
        this.dataCorrente = dataCorrente;
        inflater = LayoutInflater.from(context);
        dbManager = new DBManager(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
       // Log.i("GridAdapterView", "______________________________________________________________");
        //Giorno da visualizzare
        Date data = giorni.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        int giorno = cal.get(Calendar.DAY_OF_MONTH); //Giorno da visualizzare
        int giornoSettimana = cal.get(Calendar.DAY_OF_WEEK);
        //Mese e anno del giorno da visualizzare
        int mese = cal.get(Calendar.MONTH) + 1;
        //Log.i("GridAdapterCalendario", "Mese: " + mese);
        int anno = cal.get(Calendar.YEAR);
        //Mese e anno visualizzati
        int meseCorrente = dataCorrente.get(Calendar.MONTH);
        int annoCorrente = dataCorrente.get(Calendar.YEAR);
        //Log.i("GridAdapterCalendario", "MeseCorrente: " + meseCorrente + " Mese: " + mese);
        View view = convertView;
        if(view == null)
            view = inflater.inflate(R.layout.layout_cella_calendario, parent, false);
        //Se il giorno è festivo lo coloro di rosso
        if(festivo(giorno + "/" + mese))
            view.setBackgroundColor(Color.parseColor("#cc0000"));
        //Se il mese e l'anno sono uguali a mese e anno visualizzati lo coloro di blu
        else if(mese == meseCorrente && anno == annoCorrente){
            //Log.i("GridAdapterCalendario", "Coloro di blu");
            view.setBackgroundColor(Color.parseColor("#00aaff"));
        }
        //Se il giorno non appartiene al mese e all'anno visualizzato lo coloro di bianco
        else
            view.setBackgroundColor(Color.parseColor("#ffffff"));
        TextView giornoCalendario = (TextView) view.findViewById(R.id.giorno_calendario);
        List<Evento> eventi = dbManager.getEventi();
        TextView segnoEvento = (TextView) view.findViewById(R.id.segna_evento);
        Calendar dataEvento = Calendar.getInstance();
        if(eventi != null) {
            /**Se ci sono eventi per questo giorno, colora una text view per segnalare un evento segnato*/
            for (int i = 0; i < eventi.size(); i++) {
                dataEvento.setTime(eventi.get(i).getData());
                if (dataEvento.get(Calendar.DAY_OF_MONTH) == giorno && (dataEvento.get(Calendar.MONTH) + 1) == mese && dataEvento.get(Calendar.YEAR) == anno)
                    segnoEvento.setBackgroundColor(Color.parseColor("#FF4081"));
            }
        }
        giornoCalendario.setText("" + giorno);
        return view;
    }

    @Override
    public int getCount(){
        return giorni.size();
    }

    @Nullable
    @Override
    public Object getItem(int position){
        return giorni.get(position);
    }

    @Override
    public int getPosition(Object item){
        return giorni.indexOf(item);
    }

    /**Metodo che ritorna true se la data(mm/gg) passata come parametro è fesitiva*/
    private boolean festivo(String data){
        if(Arrays.asList(giorniFestivi).contains(data))
            return true;
        return false;
    }
}
