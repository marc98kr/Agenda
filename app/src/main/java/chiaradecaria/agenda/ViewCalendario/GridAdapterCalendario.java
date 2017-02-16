package chiaradecaria.agenda.ViewCalendario;

/**
 * Created by Marco on 16/02/2017.
 */
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import chiaradecaria.agenda.R;

public class GridAdapterCalendario extends ArrayAdapter{
    private LayoutInflater inflater;
    private List<Date> dateMesi;
    private Calendar dataCorrente;
    public GridAdapterCalendario(Context context, List<Date> dateMesi, Calendar dataCorrente){
        super(context, R.layout.layout_cella_calendario);
        this.dateMesi = dateMesi;
        this.dataCorrente = dataCorrente;
    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Date data = dateMesi.get(position);
        Calendar cal = Calendar.getInstance();
        cal.setTime(data);
        int giorno = cal.get(Calendar.DAY_OF_MONTH);
        int mese = cal.get(Calendar.MONTH) + 1;
        int anno =cal.get(Calendar.YEAR);
        int meseCorrente = dataCorrente.get(Calendar.MONTH);
        int annoCorrente = dataCorrente.get(Calendar.YEAR);
        View view = convertView;
        if(view == null){
            view = inflater.inflate(R.layout.layout_cella_calendario, parent, false);
        }
        if(mese == meseCorrente && anno == annoCorrente){
            view.setBackgroundColor(Color.parseColor("#FF5733"));
        }
        else{
            view.setBackgroundColor(Color.parseColor("CCCCCC"));
        }
        return view;
    }
    @Override
    public int getCount(){
        return dateMesi.size();
    }
    @Nullable
    @Override
    public Object getItem(int position){
        return dateMesi.get(position);
    }
    @Override
    public int getPosition(Object item){
        return dateMesi.indexOf(item);
    }
}
