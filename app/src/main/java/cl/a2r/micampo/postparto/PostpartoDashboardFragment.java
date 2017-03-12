package cl.a2r.micampo.postparto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cl.a2r.common.AppException;
import cl.a2r.micampo.R;
import cl.a2r.micampo.app.DiiosActivity;
import cl.a2r.micampo.app.custom.ShowAlert;
import cl.a2r.micampo.app.service.AppService;
import cl.a2r.micampo.app.service.MangadaDetalleService;
import cl.a2r.micampo.app.service.MangadaService;
import cl.a2r.micampo.model.revpostparto.Diagnostico;
import cl.a2r.micampo.model.revpostparto.GanRevPostParto;
import cl.a2r.micampo.postparto.service.DiagnosticoService;
import cl.a2r.micampo.postparto.service.MedControlService;
import cl.a2r.micampo.postparto.model.Postparto;
import cl.a2r.micampo.postparto.service.PostpartoService;
import cl.a2r.micampo.wsservice.WSRevPostPartoCliente;
import cl.a2r.sip.model.Medicamento;
import cl.a2r.sip.model.MedicamentoControl;
import cl.a2r.sip.wsservice.WSRB51Cliente;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
//import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

public class PostpartoDashboardFragment extends Fragment {

    private View parentView;
    private FloatingActionMenu materialDesignFAM;
    private FloatingActionButton btnRegister, btnSearch;

    private OnFragmentInteractionListener mListener;

    private SwipeRefreshLayout swipeRefresh;

    private CardView cvSincronizar;
    private TextView tvSincronizarRegistros;
    private TextView tvSincronizar;

    private CardView cvAtrasadasRevision;
    private TextView tvAtrasadasRevision;
    private CardView cvAtrasadasTratamiento;
    private TextView tvAtrasadasTratamiento;

    private TextView tvActualizado;

    private ProgressBar loading;

    private PieChart pieChart;

    public PostpartoDashboardFragment() {
        // Required empty public constructor
    }

    public static PostpartoDashboardFragment newInstance(String param1, String param2) {
        PostpartoDashboardFragment fragment = new PostpartoDashboardFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parentView = inflater.inflate(R.layout.postparto_activity_dashboard_fragment, container, false);

        swipeRefresh = (SwipeRefreshLayout) parentView.findViewById(R.id.swipeRefresh);
        swipeRefresh.setColorSchemeResources(R.color.google_blue, R.color.google_orange, R.color.google_red);
        swipeRefresh.setOnRefreshListener(
            new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    new loadDataAsync().execute();
                }
            }
        );

        materialDesignFAM = (FloatingActionMenu) parentView.findViewById(R.id.fabMenu);
        btnSearch = (FloatingActionButton) materialDesignFAM.findViewById(R.id.btnSearch);
        btnRegister = (FloatingActionButton) materialDesignFAM.findViewById(R.id.btnRegister);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            materialDesignFAM.close(true);
            Intent intent = new Intent(getActivity(), PostpartoRegistroActivity.class);
            intent.putExtra("actividadid", AppService.ACTIVIDAD_POSTPARTO_BUSQUEDA);
            startActivityForResult(intent, AppService.REQUEST_CODE_FRAGMENT);
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            materialDesignFAM.close(true);
            Intent intent = new Intent(getActivity(), PostpartoRegistroActivity.class);
            startActivityForResult(intent, AppService.REQUEST_CODE_FRAGMENT);
            }
        });

        AppService.setContext(getActivity().getApplicationContext());
        MangadaService.setContext(getActivity().getApplicationContext());
        MangadaDetalleService.setContext(getActivity().getApplicationContext());
        PostpartoService.setContext(getActivity().getApplicationContext());
        DiagnosticoService.setContext(getActivity().getApplicationContext());
        MedControlService.setContext(getActivity().getApplicationContext());

        //GRAFICO
        pieChart = (PieChart) parentView.findViewById(R.id.pieChart);
        pieChart.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                switch (e.getXIndex()) {
                    case 0:
                        List<Postparto> list1 = PostpartoService.getPendienteRevision(AppService.getPredioId(), Long.valueOf(0));
                        if (list1 != null && list1.size() > 0) {
                            Intent intent = new Intent(getActivity(), DiiosActivity.class);
                            intent.putExtra("title", "Pendiente revisión");
                            intent.putExtra("list", PostpartoService.getDiios(list1, AppService.ACTIVIDAD_INDEFINIDA));
                            startActivity(intent);
                            //startActivityForResult(intent, AppService.REQUEST_CODE_DIIOS);
                        }
                        break;

                    case 1:
                        List<Postparto> list2 = PostpartoService.getEnTratamiento(AppService.getPredioId(), Long.valueOf(0));
                        if (list2 != null && list2.size() > 0) {
                            Intent intent = new Intent(getActivity(), DiiosActivity.class);
                            intent.putExtra("title", "En Tratamiento");
                            intent.putExtra("list", PostpartoService.getDiios(list2, AppService.ACTIVIDAD_INDEFINIDA));
                            startActivity(intent);
                            //startActivityForResult(intent, AppService.REQUEST_CODE_DIIOS);
                        }
                        break;

                    case 2:
                        List<Postparto> list3 = PostpartoService.getDeAlta(AppService.getPredioId());
                        if (list3 != null && list3.size() > 0) {
                            Intent intent = new Intent(getActivity(), DiiosActivity.class);
                            intent.putExtra("title", "De Alta");
                            intent.putExtra("list", PostpartoService.getDiios(list3, AppService.ACTIVIDAD_INDEFINIDA));
                            startActivity(intent);
                            //startActivityForResult(intent, AppService.REQUEST_CODE_DIIOS);
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected() {
            }
        });

        cvSincronizar = (CardView) parentView.findViewById(R.id.cvSincronizar);
        tvSincronizarRegistros = (TextView) parentView.findViewById(R.id.tvSincronizarRegistros);
        tvSincronizar = (TextView) parentView.findViewById(R.id.tvSincronizar);
        tvSincronizar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new loadDataAsync().execute();
            }
        });

        cvAtrasadasRevision = (CardView) parentView.findViewById(R.id.cvAtrasadasRevision);
        tvAtrasadasRevision = (TextView) parentView.findViewById(R.id.tvAtrasadasRevision);
        tvAtrasadasRevision.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Buscar lunes
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                List<Postparto> list1 = PostpartoService.getPendienteRevision(AppService.getPredioId(), calendar.getTime().getTime());
                if (list1 != null && list1.size() > 0) {
                    Intent intent = new Intent(getActivity(), DiiosActivity.class);
                    intent.putExtra("title", "Pendiente atrasadas");
                    intent.putExtra("list", PostpartoService.getDiios(list1, AppService.ACTIVIDAD_INDEFINIDA));
                    startActivity(intent);
                }
            }
        });

        cvAtrasadasTratamiento = (CardView) parentView.findViewById(R.id.cvAtrasadasTratamiento);
        tvAtrasadasTratamiento = (TextView) parentView.findViewById(R.id.tvAtrasadasTratamiento);
        tvAtrasadasTratamiento.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //Buscar lunes
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                List<Postparto> list1 = PostpartoService.getEnTratamiento(AppService.getPredioId(), calendar.getTime().getTime());
                if (list1 != null && list1.size() > 0) {
                    Intent intent = new Intent(getActivity(), DiiosActivity.class);
                    intent.putExtra("title", "En tratamiento atrasadas");
                    intent.putExtra("list", PostpartoService.getDiios(list1, AppService.ACTIVIDAD_INDEFINIDA));
                    startActivity(intent);
                }
            }
        });

        tvActualizado = (TextView) parentView.findViewById(R.id.tvActualizado);

        loading = (ProgressBar) parentView.findViewById(R.id.loading);

        //Cargar datos via WebService
        new loadDataAsync().execute();

        return parentView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Buscar datos via WebService
     */
    protected class loadDataAsync extends AsyncTask<Void, Void, Void> {
        String title, msg;
        List<Postparto> listPostparto;
        List<GanRevPostParto> listGanRevPostparto;
        List<Diagnostico> listDiagnostico;
        List<MedicamentoControl> listMedicamentoControl;

        protected void onPreExecute() {
            title = "";
            msg = "";

            if (pieChart.getData() == null) {
                loading.setVisibility(View.VISIBLE);
                if (pieChart.getData() == null && PostpartoService.countCandidatos(AppService.getPredioId(), AppService.ACTIVIDAD_POSTPARTO_REGISTRO) > 0) {
                    refreshControls();
                }
            }
        }

        protected Void doInBackground(Void... arg0) {
            try {

                Thread.sleep(1500);

                if (!AppService.hayInternet()) {
                    title = "Error";
                    msg = "No hay conexión a internet, trabaja con datos locales.";
                    return null;
                }

                //Buscar todos los pendientes de sincronizacion y mandar a guardar en PostgreSQL
                List<Postparto> noSincronizados = PostpartoService.getNoSincronizados();
                if (noSincronizados != null && noSincronizados.size() > 0) {
                    //Preparar lista de GanRevPostParto a guardar
                    List<GanRevPostParto> list = new ArrayList<GanRevPostParto>();
                    for (Postparto item: noSincronizados) {
                        if (item.getGanPostparto() != null) {
                            if (item.getGanPostparto().getIdMedControl() == 0) {
                                item.getGanPostparto().setIdMedControl(null);
                                item.getGanPostparto().setMedicamento("");
                            }
                            list.add(item.getGanPostparto());
                        }
                    }

                    //Verificar si lista llena
                    if (list != null && list.size() > 0) {
                        //Guardar lista en postgresql
                        WSRevPostPartoCliente.insertaRevPostParto(list);

                        //Si guardo bien en PostgreSQL, actualizar atributo Sincronizado
                        for (Postparto item: noSincronizados) {
                            //Poner como sincronizado
                            item.setSincronizado(1);
                            //Actualizar en SQLite
                            PostpartoService.update(item);
                        }
                    }
                }

                //BUSCAR NUEVOS REGISTROS DE LOS 3 TIPOS
                //DIAGNOSTICOS
                listDiagnostico = new ArrayList<>();
                {
                    //Insertar prompt
                    Diagnostico item = new Diagnostico();
                    item.setId(-1);
                    item.setCodigo("");
                    item.setNombre("Seleccione Diagnóstico");
                    listDiagnostico.add(item);

                    //Buscar Diagnosticos
                    List<Diagnostico> _list = WSRevPostPartoCliente.traeDiagnosticos();
                    if (_list != null && _list.size() > 0) {
                        listDiagnostico.addAll(_list);
                    }
                }

                //MEDICAMENTOS
                listMedicamentoControl = new ArrayList<>();
                {
                    //Insertar prompt
                    MedicamentoControl item = new MedicamentoControl();
                    item.setId(-1);
                    item.setSerie(0);
                    item.setLote(0);
                    item.setCantidad(0);

                    Medicamento med = new Medicamento();
                    med.setId(-1);
                    med.setCodigo("");
                    med.setNombre("Seleccione Tratamiento");
                    item.setMed(med);
                    listMedicamentoControl.add(item);

                    //Inseratar al inicio Medicamento ficticio "Sin tratamineto"
                    item = new MedicamentoControl();
                    item.setId(0);
                    item.setSerie(0);
                    item.setLote(0);
                    item.setCantidad(0);

                    med = new Medicamento();
                    med.setId(0);
                    med.setCodigo("");
                    med.setNombre("Sin tratamiento");
                    item.setMed(med);
                    listMedicamentoControl.add(item);

                    //Buscar Medicamentos
                    List<MedicamentoControl> _list = WSRB51Cliente.traeMedicamentos(AppService.getId());

                    if (_list != null && _list.size() > 0) {
                        listMedicamentoControl.addAll(_list);
                    }
                }

                //POSTPARTOS
                listGanRevPostparto = new ArrayList<>();
                listGanRevPostparto = WSRevPostPartoCliente.traeGanRevPostParto();


            } catch (AppException e) {

                title = "Error";
                msg = e.getMessage();

            } catch (InterruptedException e) {

                title = "Error";
                msg = e.getMessage();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            if (title.equals("Error")) {
                loading.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                ShowAlert.showAlert("Revise", "Buscando datos en servidor... " + msg, getActivity());
            }

            //Guardar en SQLite
            //Diagnosticos
            if (listDiagnostico != null && listDiagnostico.size() > 0) {
                //Primero borrar datos anteriores en SQLite
                DiagnosticoService.delete();
                //Guardar nuevos
                DiagnosticoService.insert(listDiagnostico);
            }

            //Medicamentos
            if (listMedicamentoControl != null && listMedicamentoControl.size() > 0) {
                //Primero borrar datos anteriores en SQLite
                MedControlService.delete();

                //Guardar nuevos
                MedControlService.insert(listMedicamentoControl);
            }

            //Postpartos
            if (listGanRevPostparto != null && listGanRevPostparto.size() > 0) {

                //Buscar procesados
                List<Postparto> procesados = null;
                if (AppService.getDaysAgo(AppService.getUserDate()) == 0) {
                    procesados = PostpartoService.getProcesados(AppService.getPredioId(), AppService.ACTIVIDAD_POSTPARTO_REGISTRO);
                }

                listPostparto = new ArrayList<>();
                //Convertir lista de tipo GanRevPostParto a Postparto
                for (GanRevPostParto ganRevPostParto : listGanRevPostparto) {
                    if (ganRevPostParto.getIdFundo().equals(AppService.getPredioId())) {
                        Postparto postparto = getPostparto(ganRevPostParto);
                        listPostparto.add(postparto);
                    }
                }

                //Guardar en SQLite
                if (listPostparto != null && listPostparto.size() > 0) {
                    //Delete
                    PostpartoService.deletePorPredio(AppService.getPredioId());

                    //Insert
                    PostpartoService.insert(listPostparto);

                    //Actualizar procesados anteriores
                    if (procesados != null && procesados.size() > 0) {
                        for (Postparto procesado : procesados) {
                            Postparto item = PostpartoService.getPorDiioCodRevision(procesado.getDiio(), procesado.getGanPostparto().getCodRevision());
                            if (item != null) {
                                item.setMangada(procesado.getMangada());
                                item.setCandidato(procesado.getCandidato());
                                item.getGanPostparto().setCandidato(procesado.getGanPostparto().isCandidato());
                                PostpartoService.update(item);
                            }
                        }
                    }

                    AppService.setUserDate(new Date());
                    AppService.update();
                }
            }

            //LIMPIAR MANGADA SI FECHA NUEVA
            if (AppService.getDaysAgo(AppService.getUserDate()) > 0) {
                //Borarr todos los registros en MagadaDetalle
                MangadaDetalleService.delete();

                //Borarr todos los registros en Magada
                MangadaService.delete();
            }

            if (AppService.getPredioId() > 0) {
                refreshControls();
            }

            loading.setVisibility(View.GONE);
            swipeRefresh.setRefreshing(false);
        }
    }

    private void refreshControls() {

        List<Postparto> noSincronizados = PostpartoService.getNoSincronizados();
        cvSincronizar.setVisibility(noSincronizados != null && noSincronizados.size() > 0 ? View.VISIBLE : View.GONE);
        tvSincronizarRegistros.setText(String.format("%1$d", noSincronizados != null && noSincronizados.size() > 0 ? noSincronizados.size() : 0));

        Integer countPendiente = PostpartoService.countPendienteRevision(AppService.getPredioId(), 0);
        Integer countDeAlta = PostpartoService.countDeAlta(AppService.getPredioId());
        Integer countTratamiento = PostpartoService.countEnTratamiento(AppService.getPredioId(), 0);
        Integer totalParidas = countPendiente + countDeAlta + countTratamiento;

        if (totalParidas > 0) {

            //Buscar lunes
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            Integer countPendienteAtrasadas = PostpartoService.countPendienteRevision(AppService.getPredioId(), calendar.getTime().getTime());
            Integer countTratamientoAtrasadas = PostpartoService.countEnTratamiento(AppService.getPredioId(), calendar.getTime().getTime());

            //Grafico
            ArrayList<Entry> entries = new ArrayList<>();
            entries.add(new Entry(countPendiente, 0));
            entries.add(new Entry(countTratamiento, 1));
            entries.add(new Entry(countDeAlta, 2));
            PieDataSet dataset = new PieDataSet(entries, "");
            dataset.setColors(new int[]{R.color.google_orange, R.color.google_red, R.color.google_blue}, getActivity());
            dataset.setDrawValues(true);
            //dataset.setSliceSpace(3f);//linea separacion entre slice

            ArrayList<String> labels = new ArrayList<String>();
            labels.add(String.format("%1$d Pendiente", countPendiente));
            labels.add(String.format("%1$d Tratamiento", countTratamiento));
            labels.add(String.format("%1$d Alta", countDeAlta));

            boolean animate = pieChart.getData() == null;

            PieData data = new PieData(labels, dataset);
            pieChart.setData(data);
            pieChart.setNoDataTextDescription("");
            pieChart.setMinimumHeight(pieChart.getWidth());

            data.setValueTextSize(24);
            data.setValueTextColor(Color.WHITE);

            pieChart.setDescription("");
            pieChart.setDrawHoleEnabled(true);

            pieChart.setCenterText(String.format("%1$d\nparidas", totalParidas));
            pieChart.setCenterTextSize(24f);

            if (animate) {
                pieChart.animateY(1400, Easing.EasingOption.EaseInOutQuad);
            }

            pieChart.setDrawingCacheEnabled(false);

            Legend legend = pieChart.getLegend();
            legend.setPosition(LegendPosition.ABOVE_CHART_CENTER);
            legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
            legend.setFormSize(15f); // set the size of the legend forms/shapes
            legend.setTextSize(15f);
            legend.setTextColor(R.color.primary_dark);
            legend.setXEntrySpace(5f); // set the space between the legend entries on the x-axis

            legend.setYEntrySpace(10); // set the space between the legend entries on the y-axis
            legend.setYOffset(10);

            pieChart.getLegend().setWordWrapEnabled(true);
            pieChart.getLegend().setYEntrySpace(40f);

            pieChart.setDrawSliceText(false);
            pieChart.invalidate();
            pieChart.setVisibility(View.VISIBLE);

            cvAtrasadasRevision.setVisibility(countPendienteAtrasadas > 0 ? View.VISIBLE : View.GONE);
            tvAtrasadasRevision.setText(String.format("%1$d", countPendienteAtrasadas));

            cvAtrasadasTratamiento.setVisibility(countTratamientoAtrasadas > 0 ? View.VISIBLE : View.GONE);
            tvAtrasadasTratamiento.setText(String.format("%1$d", countTratamientoAtrasadas));

            tvActualizado.setText(String.format("Actualizado: %1$s", AppService.dateToString(AppService.getUserDate(), "dd MMM hh:mma")));
        }
    }

    /**
     * Crea un objeto Postparto a partir de uno GanRevPostParto
     * @param ganRevPostParto
     * @return
     */
    private Postparto getPostparto(GanRevPostParto ganRevPostParto) {
        Postparto postparto = new Postparto();
        postparto.setMangada(0);
        postparto.setSincronizado(1);
        postparto.setCandidato(ganRevPostParto.isCandidato() ? 1 : 0);
        postparto.setGanPostparto(ganRevPostParto);
        return postparto;
    }

    private void guardarDatosAsync() {
        new AsyncTask<Void, Void, Void>() {
            String title, msg;

            protected void onPreExecute() {
                //loading.setVisibility(View.VISIBLE);
                title = "";
                msg = "";
            }

            protected Void doInBackground(Void... arg0) {
                try {

                    Thread.sleep(100);

                    //buscar todos los pendientes de sincronizacion y mandar a guardar en PostgreSQL
                    List<Postparto> noSincronizados = PostpartoService.getNoSincronizados();
                    if (noSincronizados != null && noSincronizados.size() > 0) {

                        if (!AppService.hayInternet()) {
                            title = "Error";
                            msg = "No se pudo sincronizar, no hay internet.";
                            return null;
                        }

                        //Preparar lista de GanRevPostParto a guardar
                        List<GanRevPostParto> list = new ArrayList<GanRevPostParto>();
                        for (Postparto item: noSincronizados) {
                            if (item.getGanPostparto() != null) {
                                if (item.getGanPostparto().getIdMedControl() == 0) {
                                    item.getGanPostparto().setIdMedControl(null);
                                    item.getGanPostparto().setMedicamento("");
                                }
                                list.add(item.getGanPostparto());
                            }
                        }

                        //Verificar si lista llena
                        if (list != null && list.size() > 0) {
                            //Guardar lista en postgresql
                            WSRevPostPartoCliente.insertaRevPostParto(list);

                            //Si guardo bien en PostgreSQL, actualizar atributo Sincronizado
                            for (Postparto item: noSincronizados) {
                                //Poner como sincronizado
                                item.setSincronizado(1);
                                //Actualizar en SQLite
                                PostpartoService.update(item);
                            }
                        }
                    }
                }
                catch (AppException e) {

                    title = "Error";
                    msg = e.getMessage();

                }
                catch (InterruptedException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                return null;
            }

            protected void onPostExecute(Void result){

                loading.setVisibility(View.INVISIBLE);

                if (title.equals("Error")){
                    //ShowAlert.showAlert(title, msg, getApplicationContext());
                    Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
                    return;
                }

                try {

                    Toast.makeText(getContext(), "Sincronización concluida..", Toast.LENGTH_LONG).show();
                    refreshControls();
                }
                catch (Exception e){
                    Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case AppService.REQUEST_CODE_FRAGMENT:
                new loadDataAsync().execute();
                break;
        }
    }
}
