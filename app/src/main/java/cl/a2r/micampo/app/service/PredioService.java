package cl.a2r.micampo.app.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cl.a2r.micampo.app.provider.AppContentProvider;
import cl.a2r.micampo.app.provider.PredioSQLite;
import cl.a2r.sip.model.Predio;


/**
 * Created by fmartin on 11-08-2016.
 */
public class PredioService {

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    public static Context context;


    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public static Context getContext() { return context; }
    public static void setContext(Context _context) { context = _context; }


    // METODOS
    //----------------------------------------------------------------------------------------------
    /**
     * Buscar todos los predios
     * @return
     */
    public static List<Predio> getAll() {
        Uri uri = AppContentProvider.CONTENT_URI_PREDIO;
        String[] projection = getColumnsArray();
        String orderBy = PredioSQLite.COLUMN_NOMBRE;

        List<Predio> _predios = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                Predio _predio = getPredio(cursor);
                _predios.add(_predio);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return _predios;
    }

    /**
     * Buscar un predio
     * @param id
     * @return
     */
    public static Predio get(Integer id)  {
        try {

            Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_PREDIO + "/" + id);
            String[] projection = getColumnsArray();

            Predio _predio = null;

            Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                _predio = getPredio(cursor);
            }

            if (cursor != null) {
                cursor.close();
            }

            return _predio;
        }
        catch (Exception e) {
            return  null;
        }
    }

    /**
     * Toma un objeto Predio del cursor
     * @param cursor
     * @return
     */
    public static Predio getPredio(Cursor cursor) {
        Predio _predio = new Predio();
        _predio.setId(cursor.getInt(cursor.getColumnIndexOrThrow(PredioSQLite.COLUMN_ID)));
        _predio.setCodigo(cursor.getString(cursor.getColumnIndexOrThrow(PredioSQLite.COLUMN_CODIGO)));
        _predio.setNombre(cursor.getString(cursor.getColumnIndexOrThrow(PredioSQLite.COLUMN_NOMBRE)));
        //_predio.setClientId(cursor.getInt(cursor.getColumnIndexOrThrow(PredioSQLite.COLUMN_CLIENTID)));
        return _predio;
    }

    /**
     * Insertar nuevo Predio
     * @param _predio
     */
    public static void insert(Predio _predio) {
        ContentValues values = getContentValues(_predio);
        getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_PREDIO, values);
    }

    /**
     * Insertar nuevos Predios
     * @param _predios
     */
    public static void insert(List<Predio> _predios) {
        ContentValues[] values = getContentValues(_predios);
        getContext().getContentResolver().bulkInsert(AppContentProvider.CONTENT_URI_PREDIO, values);
    }

    /**
     * Actualizar valores de un Predio
     * @param _predio
     */
    public static void update(Predio _predio) {
        ContentValues values = getContentValues(_predio);
        getContext().getContentResolver().update(AppContentProvider.CONTENT_URI_PREDIO, values, null, null);
    }

    /**
     * Insertar o Actualizar los valores de un Predio
     * Actualiza si existe e Inserta si no
     */
    public static void updateOrInsert(Predio _predio) {
        ContentValues values = getContentValues(_predio);
        if (_predio.getId() == 0 || values.get("id") == null) {
            //Nuevo sin guardar, Insertar
            getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_PREDIO, values);
        }
        else {
            //Actualizar
            getContext().getContentResolver().update(AppContentProvider.CONTENT_URI_PREDIO, values, null, null);
        }
    }

    /**
     * Elimina un Predio
     * @param id
     */
    public static void delete(Integer id) {
        Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_PREDIO + "/" + id);
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina un Predio
     * @param _predio
     */
    public static void delete(Predio _predio) {
        delete(_predio.getId());
    }


    // UTILES
    //----------------------------------------------------------------------------------------------
    /**
     * Pone los atribuos de un Predio en estructura de tipo ContentValues
     * util para usarse en Insert/Update de AppContentrProvider
     * @return ContentValues
     */
    public static ContentValues getContentValues(Predio _predio) {
        ContentValues values = new ContentValues();
        values.put(PredioSQLite.COLUMN_ID, _predio.getId());
        values.put(PredioSQLite.COLUMN_CODIGO, _predio.getCodigo());
        values.put(PredioSQLite.COLUMN_NOMBRE, _predio.getNombre());
        values.put(PredioSQLite.COLUMN_CLIENTID, 0);
        return values;
    }

    /**
     * Pone una lista de Predios en estructura de tipo ContentValues
     * util para usarse en bulkInsert de AppContentrProvider
     * @return ContentValues[]
     */
    public static ContentValues[] getContentValues(List<Predio> _predios) {
        ContentValues[] values = new ContentValues[_predios.size()];

        if (_predios != null && _predios.size() > 0) {
            int i = 0;
            for (Predio _predio : _predios) {
                ContentValues _values =getContentValues(_predio);
                values[i] = _values;
                i++;
            }
        }

        return values;
    }

    /**
     * Retorna un arreglo con todos los campos de la tabla,
     * util para pasar como parametro "projection" en query
     * @return
     */
    public static final String[] getColumnsArray() {
        return new String [] {
            PredioSQLite.COLUMN_ID,
            PredioSQLite.COLUMN_CODIGO,
            PredioSQLite.COLUMN_NOMBRE,
            PredioSQLite.COLUMN_CLIENTID,
        };
    }

}
