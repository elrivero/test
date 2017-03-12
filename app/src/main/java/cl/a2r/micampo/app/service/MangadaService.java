package cl.a2r.micampo.app.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cl.a2r.micampo.app.provider.AppContentProvider;
import cl.a2r.micampo.app.model.Mangada;
import cl.a2r.micampo.app.model.MangadaDetalle;
import cl.a2r.micampo.app.provider.MangadaDetalleSQLite;
import cl.a2r.micampo.app.provider.MangadaSQLite;


/**
 * Created by fmartin on 11-08-2016.
 */
public class MangadaService {

    public static final Integer ESTADO_TODOS = -1;
    public static final Integer ESTADO_CERRADA = 0;
    public static final Integer ESTADO_ABIERTA = 1;


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
     * Buscar todos
     * @return
     */
    public static List<Mangada> getAll() {
        Uri uri = AppContentProvider.CONTENT_URI_MANGADA;
        String[] projection = getColumnsArray();
        String orderBy = MangadaSQLite.COLUMN_NUMERO;

        List<Mangada> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                Mangada item = getMangada(cursor);
                list.add(item);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Buscar mangadas por Predio, Actividad y Estado
     * @param predioid pasar -1 para omitir este filtro
     * @param actividadid pasar -1 para omitir este filtro
     * @param estadoid pasar -1 para omitir este filtro
     * @return
     */
    public static List<Mangada> getByPredioActividadEstado(Integer predioid, Integer actividadid, Integer estadoid) {
        Uri uri = AppContentProvider.CONTENT_URI_MANGADA;
        String[] projection = getColumnsArray();
        String selection = "(%1=-1 OR " + MangadaSQLite.COLUMN_PREDIOID + "=%1) AND (%2=-1 OR " + MangadaSQLite.COLUMN_ACTIVIDADID + "=%2) AND (%3=-1 OR " + MangadaSQLite.COLUMN_ESTADOID + "=%3) ";
        selection = selection.replace("%1", predioid.toString());
        selection = selection.replace("%2", actividadid.toString());
        selection = selection.replace("%3", estadoid.toString());
        //String[] selectionArgs = { predioid.toString(), predioid.toString(), actividadid.toString(), actividadid.toString(), estadoid.toString(), estadoid.toString() };
        String orderBy = MangadaSQLite.COLUMN_PREDIOID + ", " + MangadaSQLite.COLUMN_ACTIVIDADID + ", " + MangadaSQLite.COLUMN_ID;

        List<Mangada> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, null, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                Mangada item = getMangada(cursor);
                if (item.getId() != null) {
                    item.setDetalles(getDetalles(item.getId()));
                }
                list.add(item);
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Busca la ultima mangada abierta de un Predio y Actividad
     * si ninguna o todas cerradas abre una nueva
     * @param predioid
     * @param actividadid
     * @return
     */
    public static Mangada getAbiertaByPredioActividad(Integer predioid, Integer actividadid) {
        Mangada mangada;
        List<Mangada> list = MangadaService.getByPredioActividadEstado(predioid, actividadid, ESTADO_ABIERTA);
        if (list != null && list.size() > 0) {
            mangada = list.get(list.size()-1);
        }
        else {
            List<Mangada> cerradas = MangadaService.getByPredioActividadEstado(predioid, actividadid, ESTADO_CERRADA);

            //Crear nueva mangada
            mangada = new Mangada();
            mangada.setPredioId(predioid);
            mangada.setActividadId(actividadid);
            mangada.setEstadoId(ESTADO_ABIERTA);
            //Tomar como numero siguiente al ultimo generado o 1
            mangada.setNumero(cerradas != null && cerradas.size() > 0 ? cerradas.get(cerradas.size()-1).getNumero() + 1 : 1);
            mangada.setFecha(new Date());
            MangadaService.insert(mangada);
        }

        if (mangada.getId() != null) {
            mangada.setDetalles(getDetalles(mangada.getId()));
        }
        return mangada;
    }

    /**
     * Buscar uno
     * @param id
     * @return
     */
    public static Mangada get(Integer id)  {
        try {

            Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_MANGADA + "/" + id.toString());
            String[] projection = getColumnsArray();

            Mangada item = null;

            Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                item = getMangada(cursor);

                if (item.getId() != null) {
                    item.setDetalles(getDetalles(item.getId()));
                }
            }

            if (cursor != null) {
                cursor.close();
            }

            return item;
        }
        catch (Exception e) {
            return  null;
        }
    }



    /**
     * Insertar nuevo
     * @param item
     */
    public static void insert(Mangada item) {
        ContentValues values = getContentValues(item);
        getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_MANGADA, values);
    }

    /**
     * Insertar nuevos
     * @param list
     */
    public static void insert(List<Mangada> list) {
        ContentValues[] values = getContentValues(list);
        getContext().getContentResolver().bulkInsert(AppContentProvider.CONTENT_URI_MANGADA, values);
    }

    /**
     * Actualizar valores
     * @param item
     */
    public static void update(Mangada item) {
        ContentValues values = getContentValues(item);
        Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_MANGADA + "/" + item.getId().toString());
        getContext().getContentResolver().update(uri, values, null, null);
    }

    /**
     * Insertar o Actualizar los valores de uno
     * Actualiza si existe e Inserta si no
     */
    public static void updateOrInsert(Mangada item) {
        ContentValues values = getContentValues(item);
        if (item.getId() == 0 || values.get("id") == null) {
            //Nuevo sin guardar, Insertar
            getContext().getContentResolver().insert(AppContentProvider.CONTENT_URI_MANGADA, values);
        }
        else {
            //Actualizar
            Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_MANGADA + "/" + item.getId().toString());
            getContext().getContentResolver().update(uri, values, null, null);
        }
    }

    /**
     * Elimina todos
     */
    public static void delete() {
        Uri uri = AppContentProvider.CONTENT_URI_MANGADA;
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno
     * @param id
     */
    public static void delete(Integer id) {
        Uri uri = Uri.parse(AppContentProvider.CONTENT_URI_MANGADA + "/" + id.toString());
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno
     * @param item
     */
    public static void delete(Mangada item) {

        delete(item.getId());
    }


    // DETALLES
    //----------------------------------------------------------------------------------------------

    /**
     * Obtener registros MangadaDetalle hijos de una Mangada
     * @param mangadaid
     * @return
     */
    public static List<MangadaDetalle> getDetalles(Integer mangadaid)  {
        Uri uri = AppContentProvider.CONTENT_URI_MANGADADETALLE;
        String[] projection = getDetallesColumnsArray();
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



    // TILES
    //----------------------------------------------------------------------------------------------

    /**
     * Toma un objeto del cursor
     * @param cursor
     * @return
     */
    public static Mangada getMangada(Cursor cursor) {
        Mangada item = new Mangada();
        item.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaSQLite.COLUMN_ID)));
        item.setNumero(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaSQLite.COLUMN_NUMERO)));
        item.setPredioId(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaSQLite.COLUMN_PREDIOID)));
        item.setActividadId(cursor.getInt(cursor.getColumnIndexOrThrow(MangadaSQLite.COLUMN_ACTIVIDADID)));
        return item;
    }

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
    public static ContentValues getContentValues(Mangada item) {
        ContentValues values = new ContentValues();
        values.put(MangadaSQLite.COLUMN_ID, item.getId());
        values.put(MangadaSQLite.COLUMN_NUMERO, item.getNumero());
        values.put(MangadaSQLite.COLUMN_PREDIOID, item.getPredioId());
        values.put(MangadaSQLite.COLUMN_ACTIVIDADID, item.getActividadId());
        values.put(MangadaSQLite.COLUMN_FECHA, item.getFecha().getTime());
        values.put(MangadaSQLite.COLUMN_ESTADOID, item.getEstadoId());
        return values;
    }

    /**
     * Pone una lista en estructura de tipo ContentValues
     * util para usarse en bulkInsert de AppContentrProvider
     * @return ContentValues[]
     */
    public static ContentValues[] getContentValues(List<Mangada> list) {
        ContentValues[] values = new ContentValues[list.size()];

        if (list != null && list.size() > 0) {
            int i = 0;
            for (Mangada item : list) {
                ContentValues _values =getContentValues(item);
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
            MangadaSQLite.COLUMN_ID,
            MangadaSQLite.COLUMN_NUMERO,
            MangadaSQLite.COLUMN_PREDIOID,
            MangadaSQLite.COLUMN_ACTIVIDADID,
            MangadaSQLite.COLUMN_FECHA,
            MangadaSQLite.COLUMN_ESTADOID
        };
    }

    public static final String[] getDetallesColumnsArray() {
        return new String [] {
            MangadaDetalleSQLite.COLUMN_ID,
            MangadaDetalleSQLite.COLUMN_MANGADAID,
            MangadaDetalleSQLite.COLUMN_GANADOID,
        };
    }

}
