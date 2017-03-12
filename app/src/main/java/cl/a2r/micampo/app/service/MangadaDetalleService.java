package cl.a2r.micampo.app.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cl.a2r.micampo.app.model.MangadaDetalle;
import cl.a2r.micampo.app.provider.AppContentProvider;
import cl.a2r.micampo.app.provider.MangadaDetalleSQLite;


/**
 * Created by fmartin on 11-08-2016.
 */
public class MangadaDetalleService {

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
     * Obtener registros MangadaDetalle hijos de una Mangada
     * @param mangadaid
     * @return
     */
    public static List<MangadaDetalle> getDetalles(Integer mangadaid)  {
        Uri uri = AppContentProvider.CONTENT_URI_MANGADADETALLE;
        String[] projection = getColumnsArray();
        String selection = MangadaDetalleSQLite.COLUMN_MANGADAID + "=? ";
        String[] selectionArgs = { mangadaid.toString() };
        String orderBy = MangadaDetalleSQLite.COLUMN_ID;

        List<MangadaDetalle> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                list.add(getMangadaDetalle(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Obtener cantidad de registros en mangadas del Predio y Actividad
     * @param predioid
     * @param actividadid
     * @return
     */
    public static Integer countByPredioActividad(Integer predioid, Integer actividadid) {
        List<MangadaDetalle> list = getByPredioActividad(predioid, actividadid);
        if (list != null && list.size() > 0) {
            return list.size();
        }

        return 0;
    }

    /**
     * Obtener registros MangadaDetalle hijos de una Mangada
     * @param predioid
     * @param actividadid
     * @return
     */
    public static List<MangadaDetalle> getByPredioActividad(Integer predioid, Integer actividadid)  {
        Uri uri = AppContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        String query =
            "SELECT d.* " +
            "FROM mangada m " +
            "INNER JOIN mangadadetalle d " +
            "ON m._id = d.mangadaid " +
            "WHERE m.predioid=%1 AND actividadid=%2 ";
        query = query.replace("%1", predioid.toString());
        query = query.replace("%2", actividadid.toString());

        List<MangadaDetalle> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                list.add(getMangadaDetalle(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Verifica si un ganadoid esta procesado en una Mangada de un Predio y Activida
     * @param predioid
     * @param actividadid
     * @param ganadoid
     * @return
     */
    public static boolean getProcesado(Integer predioid, Integer actividadid, Integer ganadoid)  {
        Uri uri = AppContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        String query =
            "SELECT d.* " +
            "FROM mangada m " +
            "INNER JOIN mangadadetalle d " +
            "ON m._id = d.mangadaid " +
            "WHERE m.predioid=%1 AND actividadid=%2 AND d.ganadoid=%3 ";
        query = query.replace("%1", predioid.toString());
        query = query.replace("%2", actividadid.toString());
        query = query.replace("%3", ganadoid.toString());

        List<MangadaDetalle> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            cursor.close();
            return true;
        }

        if (cursor != null) {
            cursor.close();
        }

        return false;
    }


    /**
     * Insertar nuevo
     * @param item
     */
    public static void insert(MangadaDetalle item) {
        ContentValues values = getContentValues(item);
        getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_MANGADADETALLE, values);
    }

    /**
     * Insertar nuevos
     * @param list
     */
    public static void insert(List<MangadaDetalle> list) {
        ContentValues[] values = getContentValues(list);
        getContext().getContentResolver().bulkInsert(AppContentProvider.CONTENT_URI_MANGADADETALLE, values);
    }

    /**
     * Actualizar valores
     * @param item
     */
    public static void update(MangadaDetalle item) {
        ContentValues values = getContentValues(item);
        getContext().getContentResolver().update(AppContentProvider.CONTENT_URI_MANGADADETALLE, values, null, null);
    }

    /**
     * Insertar o Actualizar los valores de uno
     * Actualiza si existe e Inserta si no
     */
    public static void updateOrInsert(MangadaDetalle item) {
        ContentValues values = getContentValues(item);
        if (item.getId() == 0 || values.get("id") == null) {
            //Nuevo sin guardar, Insertar
            getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_MANGADADETALLE, values);
        }
        else {
            //Actualizar
            getContext().getContentResolver().update(AppContentProvider.CONTENT_URI_MANGADADETALLE, values, null, null);
        }
    }

    /**
     * Elimina todos
     */
    public static void delete() {
        Uri uri = AppContentProvider.CONTENT_URI_MANGADADETALLE;
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno
     * @param id
     */
    public static void delete(Integer id) {
        Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_MANGADADETALLE + "/" + id);
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno
     * @param item
     */
    public static void delete(MangadaDetalle item) {

        delete(item.getId());
    }


     // TILES
    //----------------------------------------------------------------------------------------------

    /**
     * Toma un objeto del cursor
     * @param cursor
     * @return
     */
    public static MangadaDetalle getMangadaDetalle(Cursor cursor) {
        MangadaDetalle item = new MangadaDetalle();
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaDetalleSQLite.COLUMN_ID)));
        item.setMangadaId(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaDetalleSQLite.COLUMN_MANGADAID)));
        item.setGanadoId(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaDetalleSQLite.COLUMN_GANADOID)));
        return item;
    }

    /**
     * Pone los atribuos de un registro en estructura de tipo ContentValues
     * util para usarse en Insert/Update de AppContentrProvider
     * @return ContentValues
     */
    public static ContentValues getContentValues(MangadaDetalle item) {
        ContentValues values = new ContentValues();
        values.put(MangadaDetalleSQLite.COLUMN_ID, item.getId());
        values.put(MangadaDetalleSQLite.COLUMN_MANGADAID, item.getMangadaId());
        values.put(MangadaDetalleSQLite.COLUMN_GANADOID, item.getGanadoId());
        return values;
    }

    /**
     * Pone una lista en estructura de tipo ContentValues
     * util para usarse en bulkInsert de AppContentrProvider
     * @return ContentValues[]
     */
    public static ContentValues[] getContentValues(List<MangadaDetalle> list) {
        ContentValues[] values = new ContentValues[list.size()];

        if (list != null && list.size() > 0) {
            int i = 0;
            for (MangadaDetalle item : list) {
                ContentValues _values = getContentValues(item);
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
            MangadaDetalleSQLite.COLUMN_ID,
            MangadaDetalleSQLite.COLUMN_MANGADAID,
            MangadaDetalleSQLite.COLUMN_GANADOID,
        };
    }

}
