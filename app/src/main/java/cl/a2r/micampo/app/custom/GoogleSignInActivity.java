package cl.a2r.micampo.app.custom;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import cl.a2r.common.AppException;
import cl.a2r.micampo.R;
import cl.a2r.micampo.app.SignInActivity;
import cl.a2r.micampo.app.service.AppService;
import cl.a2r.sip.model.Aplicacion;
import cl.a2r.sip.wsservice.WSAutorizacionCliente;

/**
 * Created by Miguelon on 04-09-2016.
 */
public class GoogleSignInActivity extends AppCompatActivity
                                    implements GoogleApiClient.OnConnectionFailedListener {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private final String TAG = "AUTH";

    private GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    loginChilterra(user);
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    startActivity(new Intent(getApplication(), SignInActivity.class));
                    finish();
                }
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void loginChilterra(FirebaseUser user) {
        new AsyncTask<String, Void, Boolean>() {
            String title, msg;

            protected void onPreExecute() {
                title = "";
                msg = "";
            }

            protected Boolean doInBackground(String... args) {
                try {

                    Thread.sleep(100);

                    //Buscar usuario en bbdd de Cilterra
                    Integer userID = WSAutorizacionCliente.traeUsuario(args[0]);
                    if (userID == null || userID == 0) {
                        return false;
                    }

                    Log.d(TAG, "userId: " + userID.toString());
                    Log.d(TAG, "userEmail: " + args[0]);
                    Log.d(TAG, "userName: " + args[1]);

                    //Verificar si ya esta cargada la configuracion en AppService
                    AppService.setContext(getApplicationContext());
                    if (AppService.getId() == 0) {
                        //Si no esta cargada cargar de AppSQLite
                        AppService.load();

                        //Verificar si esta llena en AppSQLite
                        if (AppService.getId() == 0) {
                            //Si vacia en AppSQLite guardar configuracion por defecto
                            AppService.setDefaults();
                            AppService.insert();
                        }
                    }

                    //Guardar APP en AppSQLite
                    AppService.setUserId(userID);
                    AppService.setUserEmail(args[0]);
                    AppService.setUserName(args[1]);
                    AppService.update();

                } catch (AppException e) {

                    title = "Error";
                    msg = e.getMessage();
                    return false;

                } catch (InterruptedException e) {

                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return false;

                }

                return true;
            }

            protected void onPostExecute(Boolean result){
                //loading.setVisibility(View.INVISIBLE);
                if (!result){
                    Toast.makeText(getApplicationContext(), "Usuario no autorizado.", Toast.LENGTH_SHORT).show();
                    signOut();
                }
            }
        }.execute(user.getEmail(), user.getDisplayName());
    }

    protected void signOut() {
        FirebaseAuth.getInstance().signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        startActivity(new Intent(getApplication(), SignInActivity.class));
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
