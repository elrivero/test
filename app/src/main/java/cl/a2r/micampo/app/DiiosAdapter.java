package cl.a2r.micampo.app;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import cl.a2r.micampo.R;
import cl.a2r.micampo.app.model.Diio;

public class DiiosAdapter extends ArrayAdapter<Diio> {

    private Context context;

    public DiiosAdapter(Context context, ArrayList<Diio> list) {
        super(context, 0, list);

        this.context = context;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Diio item = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_activity_diios_listview_item, parent, false);
        }
        // Lookup view for data population
        TextView tvDIIO = (TextView) convertView.findViewById(R.id.tvDIIO);

        LinearLayout llMangada = (LinearLayout) convertView.findViewById(R.id.llMangada);
        TextView tvMangada = (TextView) convertView.findViewById(R.id.tvMangada);

        TextView tvFecha = (TextView) convertView.findViewById(R.id.tvFecha);
        TextView tvDescripcion = (TextView) convertView.findViewById(R.id.tvDescripcion);

        // Populate the data into the template view using the data object
        tvDIIO.setText(item.getDiioString());

        //Mangada
        Integer mangada = item.getMangada();
        llMangada.setVisibility(item.getMangada() > 0 ? View.VISIBLE : View.GONE);
        if (mangada > 0) {
            try {
                tvMangada.setText(mangada.toString());
                //String[] colors = new String[] {"#FF33B5E5", "#FFAA66CC", "#FF99CC00", "#FFFFBB33", "#FFFF4444", "#FF0099CC", "#FF9933CC", "#FF669900", "#FFFF8800", "#FFCC0000"};
                switch (mangada) {
                    case 1:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner1));
                        break;
                    case 2:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner2));
                        break;
                    case 3:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner3));
                        break;
                    case 4:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner4));
                        break;
                    case 5:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner5));
                        break;
                    case 6:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner6));
                        break;
                    case 7:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner7));
                        break;
                    case 8:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner8));
                        break;
                    case 9:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner9));
                        break;
                    case 10:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner10));
                        break;
                    default:
                        llMangada.setBackground(context.getResources().getDrawable(R.drawable.rounded_corner));
                        break;
                }
            } catch (Exception e) { }
        }

        //Fecha
        tvFecha.setVisibility(item.getFecha() != null ? View.VISIBLE : View.GONE);
        if (item.getFecha() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM");
            tvFecha.setText(dateFormat.format(item.getFecha()));
        }

        //Descripcion
        tvDescripcion.setVisibility(item.getDescripcion() != null && item.getDescripcion() != "" ? View.VISIBLE : View.GONE);
        if (item.getDescripcion() != null && item.getDescripcion() != "") {
            tvDescripcion.setText(item.getDescripcion());
        }

        // Return the completed view to render on screen
        return convertView;
    }
}
