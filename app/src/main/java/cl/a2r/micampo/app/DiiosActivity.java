package cl.a2r.micampo.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import java.util.ArrayList;

import cl.a2r.micampo.R;
import cl.a2r.micampo.app.model.Diio;

public class DiiosActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String title = "DIIOS";
    private Integer target;
    private Integer idPredio = 0;
    private ArrayList<Diio> list = new ArrayList<Diio>();

    private ListView lvDiios;
    private DiiosAdapter adapterDiios;
    private ProgressBar loading;

    private Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_diios);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        activity = this;

        final Bundle extras = getIntent().getExtras();
        if (extras != null) {
            title = extras.getString("title");
            list = (ArrayList<Diio>) getIntent().getSerializableExtra("list");

        }

        toolbar = (Toolbar) findViewById(R.id.toolbar); // Attaching the layout to the toolbar object
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loading = (ProgressBar) findViewById(R.id.loading);

        lvDiios = (ListView) findViewById(R.id.lvDiios);
        //Evento click en un DIIO
        lvDiios.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Diio item = (Diio) adapterView.getItemAtPosition(i);
            Bundle data = new Bundle();
            data.putString("diio", item.getDiio().toString());
            Intent intent = new Intent();
            intent.putExtras(data);
            setResult(RESULT_OK, intent);
            finish();
            }
        });
    }

    public void onResume() {
        super.onResume();

        try {

            setTitle(title + " (" + String.valueOf(list.size()) + ")");

            if (list.size() == 0) {
                Toast.makeText(this, "Lista vacia", Toast.LENGTH_LONG);
                return;
            }

            loading.setVisibility(View.VISIBLE);
            adapterDiios = new DiiosAdapter(this, list);
            lvDiios.setAdapter(adapterDiios);
            loading.setVisibility(View.GONE);

        }
        catch (Exception e) {
            loading.setVisibility(View.GONE);
        }
    }

}

