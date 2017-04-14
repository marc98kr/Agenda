package chiaradecaria.agenda.ViewCalendario;

/**
 * @author Chiara De Caria
 */
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import chiaradecaria.agenda.ActivityEventi;
import chiaradecaria.agenda.R;

public class ViewCalendario extends LinearLayout {
    private ImageView mesePrecedente, meseSuccessivo;
    private TextView dataCorrente;
    private GridView gridViewCalendario;
    private static final int MAX_CALENDAR_COLUMN = 42;
    private SimpleDateFormat formatoData = new SimpleDateFormat("MMMM yyyy", Locale.ITALY);
    private Calendar calendario = Calendar.getInstance(Locale.ITALY);
    private Context context;
    private GridAdapterCalendario gridAdapterCalendario;

    public ViewCalendario(Context context, AttributeSet attrs){
        super(context, attrs);
        this.context = context;
        inizializzaUI();
        setListenerTasti();
        setOnClickListenerGiorno();
        impostaAdapterCalendario();
    }
    private void setOnClickListenerGiorno(){
        gridViewCalendario.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Date giorno = (Date) parent.getItemAtPosition(position);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(giorno);
                String data = calendar.get(Calendar.DAY_OF_MONTH) + "/" + (calendar.get(Calendar.MONTH)+1) + "/" + calendar.get(Calendar.YEAR);
                mostraEventi(data);
                Log.i("ViewCalendario", "Giorno cliccato " + giorno.toString());
                Log.i("ViewCalendario", "È stato cliccato " + position);
            }
        });
    }

    public void mostraEventi(String data){
        Intent intent = new Intent(context, ActivityEventi.class);
        intent.putExtra("data", data);
        context.startActivity(intent);
    }

    public ViewCalendario(Context context){
        super(context);
        inizializzaUI();
        setListenerTasti();
        setOnClickListenerGiorno();
        impostaAdapterCalendario();
    }

    public ViewCalendario(Context context, AttributeSet attrs, int defStyleAttr){
        super(context, attrs, defStyleAttr);
        inizializzaUI();
        setListenerTasti();
        setOnClickListenerGiorno();
        impostaAdapterCalendario();
    }

    private void inizializzaUI(){
        Log.i("ViewAclendario", "Inizializzo la UI");
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.layout_calendario, this);
        mesePrecedente = (ImageView) view.findViewById(R.id.mese_precedente);
        meseSuccessivo = (ImageView) view.findViewById(R.id.mese_successivo);
        dataCorrente = (TextView) view.findViewById(R.id.txtDataSelezionata);
        gridViewCalendario = (GridView) view.findViewById(R.id.grid_view_calendario);
    }

    private void impostaAdapterCalendario(){
        List<Date> d = new ArrayList<Date>();
        Log.i("ViewCalendario", "" + calendario.get(Calendar.MONTH));
        Calendar cal =(Calendar) calendario.clone();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        int primoGiornoDelMese = cal.get(Calendar.DAY_OF_WEEK) - 1;
        cal.add(Calendar.DAY_OF_MONTH, -primoGiornoDelMese);
        while(d.size() < MAX_CALENDAR_COLUMN){
            d.add(cal.getTime());
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }
        String data = formatoData.format(calendario.getTime());
        dataCorrente.setText(data);
        gridAdapterCalendario = new GridAdapterCalendario(context, d, cal);
        gridViewCalendario.setAdapter(gridAdapterCalendario);
    }

    private void setListenerTasti(){
        mesePrecedente.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendario.add(Calendar.MONTH, -1);
                impostaAdapterCalendario();
            }
        });
        meseSuccessivo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                calendario.add(Calendar.MONTH, 1);
                impostaAdapterCalendario();
            }
        });
    }

}
