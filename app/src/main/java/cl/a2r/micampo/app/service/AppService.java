package cl.a2r.micampo.app.service;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import android.content.ContentValues;

import cl.a2r.micampo.app.provider.AppContentProvider;
import cl.a2r.micampo.app.provider.AppSQLite;
import cl.a2r.sip.model.Predio;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

//import cl.a2r.micampo.AppSQLite.*;

/**
 * Created by fmartin on 11-08-2016.
 */
public class AppService {

    public static final Integer ACTIVIDAD_INDEFINIDA = 0;
    public static final Integer ACTIVIDAD_POSTPARTO_BUSQUEDA = 1;
    public static final Integer ACTIVIDAD_POSTPARTO_REGISTRO = 2;

    public static final Integer ACTIVIDAD_INDUCCION_BUSQUEDA = 5;
    public static final Integer ACTIVIDAD_INDUCCION_REGISTRO = 6;

    public static final int REQUEST_CODE_FRAGMENT = 98;
    public static final int REQUEST_CODE_DIIOS = 99;
    public static final int RESULT_OK = 1;

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------

    //Contexto de la app para poder acceder a su BBDD en AppSQLite
    public static Context context;

    private static Uri appUri;

    //Id del registro en la tabla app de AppSQLite
    public static Integer _id;

    //Id de la app en Chilterra
    public static Integer id = 0;

    //Datos del user logueado
    public static Integer userId = 0;
    public static String userEmail = "";
    public static String userName = "";
    public static GoogleSignInAccount userAccount;
    public static Date userDate; //ultima fecha de sincronizacion de datos

    //Predio seleccionado
    public static Integer predioId;
    public static List<Predio> predios;

    //Mangada
    public static Integer mangada = 0;


    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public static Context getContext() { return context; }
    public static void setContext(Context _context) { context = _context; }

    public static Integer get_Id() { return _id; }
    public static void set_Id(Integer __id) { _id = __id;  }

    public static Integer getId() { return id; }
    public static void setId(Integer _id) { id = _id;  }

    public static Date getUserDate() { return userDate; }
    public static void setUserDate(Date _userDate) { userDate = _userDate; }

    public static Integer getUserId() { return userId; }
    public static void setUserId(Integer _userId) { userId = _userId; }

    public static String getUserEmail() { return userEmail; }
    public static void setUserEmail(String _userEmail) { userEmail = _userEmail; }

    public static String getUserName() { return userName; }
    public static void setUserName(String _userName) { userName = _userName; }

    public static GoogleSignInAccount getUserAccount() { return userAccount; }
    public static void setUserAccount(GoogleSignInAccount _userAccount) { userAccount = _userAccount; }

    public static void setPredioId(Integer _predioId) { predioId = _predioId; }
    public static Integer getPredioId() { return predioId; }
    public static Predio getPredio() {
        PredioService.setContext(getContext());
        Predio _predio = PredioService.get(getPredioId());
        if (_predio != null) {
            return _predio;
        }
        else {
            return null;
        }
    }
    public static String getPredioNombre() { return getPredio() != null ? getPredio().getNombre() : ""; }
    public static String getPredioCodigo() { return getPredio() != null ? getPredio().getCodigo() : ""; }

    public static List<Predio> getPredios() { return predios; }
    public static void setPredios(List<Predio> _predios) { predios = _predios; }

    public static Integer getMangada() { return mangada; }
    public static void setMangada(Integer _mangada) { mangada = _mangada; }

    /**
     * Cargar en los atributos de la clase AppService los valores por defecto
     */
    public static void setDefaults() {
        setId(19);
        setUserDate(new Date());
        setUserId(60);
        setUserEmail("email@server.com");
        setUserName("User Name");
        setPredioId(0);
        setMangada(0);
    }


    // METODOS
    //----------------------------------------------------------------------------------------------

    /**
     * Cargar en los atributos de la clase AppService los valores guardados en AppSQLite
     */
    public static void load() {
        Uri uri = AppContentProvider.CONTENT_URI_APP;
        String[] projection = getAppColumnsArray();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            setId(cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_ID)));
            setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_USERID)));
            setUserEmail(cursor.getString(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_USEREMAIL)));
            setUserName(cursor.getString(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_USERNAME)));
            setUserDate(new Date(cursor.getLong(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_USERDATE))));
            setPredioId(cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_PREDIOID)));
            setMangada(cursor.getInt(cursor.getColumnIndexOrThrow(AppSQLite.COLUMN_MANGADA)));
        }

        if (cursor != null) {
            cursor.close();
        }
    }

    /**
     * Inserta AppService en AppSQLite
     */
    public static void insert() {
        ContentValues values = getContentValues();
        getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_APP, values);
    }

    /**
     * Actualiza AppService en AppSQLite
     */
    public static void update() {
        ContentValues values = getContentValues();
        Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_APP + "/" + "19");
        getContext().getContentResolver().update(uri, values, null, null);
    }

    /**
     * Actualiza o crea APP en AppSQLite
     */
    public static void updateOrInsert() {
        ContentValues values = getContentValues();
        if (getId() == null || getId().intValue() == 0) {
            //Nuevo sin guardar, Insertar
            getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_APP, values);
        }
        else {
            //Actualizar
            Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_APP + "/" + "19");
            getContext().getContentResolver().update(uri, values, null, null);
        }
    }

    /**
     * Elimina AppService de AppSQLite
     */
    public static void delete(Integer id) {
        Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_APP + "/" + id);
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Pone los atribuos de AppService en una estructura de tipo ContentValues pa usarse en Insert/Update de AppContentrProvider
     * @return ContentValues
     */
    public static ContentValues getContentValues() {
        ContentValues values = new ContentValues();
        values.put(AppSQLite.COLUMN_ID, getId());
        values.put(AppSQLite.COLUMN_USERID, getUserId());
        values.put(AppSQLite.COLUMN_USEREMAIL, getUserEmail());
        values.put(AppSQLite.COLUMN_USERNAME, getUserName());
        if (getUserDate() != null) {
            values.put(AppSQLite.COLUMN_USERDATE, getUserDate().getTime());
        }
        values.put(AppSQLite.COLUMN_PREDIOID, getPredioId());
        values.put(AppSQLite.COLUMN_MANGADA, getMangada());

        return values;
    }

    public static final String[] getAppColumnsArray() {
        return new String [] {
            AppSQLite.COLUMN_ID,
            AppSQLite.COLUMN_USERID,
            AppSQLite.COLUMN_USEREMAIL,
            AppSQLite.COLUMN_USERNAME,
            AppSQLite.COLUMN_USERDATE,
            AppSQLite.COLUMN_PREDIOID,
            AppSQLite.COLUMN_MANGADA,
        };
    }

    /* FORMAT STRING
    ********************************************************************************************** */
    public static String dateToString(Date date) {
        return dateToString(date, "dd MMM");
    }

    public static String dateToString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * Verifica si hay conexión a Internet
     * @return
     */
    public static Boolean hayInternet() {
        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");
            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Buscar version actual de la APP para mostrar al inicio
     * @return
     */
    public static String getAppVersion() {
        try {
            PackageInfo pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
            return pInfo.versionName;
        }
        catch (Exception e) {
            return "";
        }
    }

    /**
     * Obtener la cantidad de dias trancurridos desde una fecha hasta la actual
     * @param date
     * @return
     */
    public static long getDaysAgo(Date date) {
        if (date == null) {
            return 0;
        }

        long time1 = date.getTime()/(1000*60*60*24);
        long time2 = new Date().getTime()/(1000*60*60*24);
        return time2 - time1;
    }

}
