package cl.a2r.micampo.app;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import cl.a2r.micampo.R;
import cl.a2r.micampo.app.custom.GoogleSignInActivity;
import cl.a2r.micampo.app.service.AppService;
import cl.a2r.micampo.app.service.MangadaDetalleService;
import cl.a2r.micampo.app.service.MangadaService;
import cl.a2r.micampo.postparto.PostpartoDashboardFragment;
import cl.a2r.micampo.postparto.service.PostpartoService;
import de.hdodenhof.circleimageview.CircleImageView;

public class DashboardActivity extends GoogleSignInActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView tvUserName;
    private TextView tvUserEmail;
    private CircleImageView ivUserPicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.app_activity_dashboard);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        AppService.setContext(getApplicationContext());
        if (AppService.getPredio() != null) {
            setTitle(AppService.getPredioNombre());
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View v = navigationView.getHeaderView(0);
        tvUserName = (TextView) v.findViewById(R.id.tvUserName);
        tvUserName.setText(AppService.getUserName());
        tvUserEmail = (TextView) v.findViewById(R.id.tvUserEmail);
        tvUserEmail.setText(AppService.getUserEmail());
        ivUserPicture = (CircleImageView) v.findViewById(R.id.ivUserPicture);
        ivUserPicture.setColorFilter(Color.WHITE);

        //FRAGMENT
        if (findViewById(R.id.fragment_container) != null) {
            if (savedInstanceState != null) {
                return;
            }

            DashboardFragment dashboardFragment = new DashboardFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, dashboardFragment).commit();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {

            setDashboardFragment();

        } else if (id == R.id.nav_postparto) {

            setPostpartoFragment();

        } else if (id == R.id.nav_mangada) {

            //Intent intent = new Intent(DashboardActivity.this, MangadasActivity.class);
            //startActivity(intent);

//            item.setActionView(new ProgressBar(this));
//            item.getActionView().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    item.setActionView(null);
//                }
//            }, 1000);



            Toast.makeText(getApplicationContext(), "Limpiado magadas...", Toast.LENGTH_LONG).show();

            //Poner mangada en 0 para todos los registros de Postparto
            PostpartoService.limpiarMagadas();

            //Borarr todos los registros en MagadaDetalle
            MangadaDetalleService.delete();

            //Borarr todos los registros en Magada
            MangadaService.delete();

//            item.setActionView(null);
            Toast.makeText(getApplicationContext(), "Limpieza de mangadas teminada...", Toast.LENGTH_LONG).show();
            //ShowAlert.showAlert("Aviso", "Finalizó la limpieza de mangadas", this);

        }  else if (id == R.id.nav_baston) {

            Intent intent = new Intent(DashboardActivity.this, BastonActivity.class);
            startActivity(intent);

        }  else if (id == R.id.nav_predio) {

            AppService.setUserId(0);
            AppService.setUserName("");
            AppService.setUserDate(new Date());
            AppService.update();

            Intent intent = new Intent(DashboardActivity.this, PredioActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout) {

            signOut();
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void setDashboardFragment() {
        DashboardFragment fragment = new DashboardFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    protected void setPostpartoFragment() {
        PostpartoDashboardFragment fragment = new PostpartoDashboardFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        transaction.replace(R.id.fragment_container, fragment);
        transaction.addToBackStack(null);

        transaction.commit();
    }

    public void onClick (View v){
        if(!v.isShown()){
            return;
        }

        if (v.getId() == R.id.cvPostparto) {
            setDashboardFragment();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            return false;
        }

        return super.onOptionsItemSelected(item);
    }
}
