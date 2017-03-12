package cl.a2r.micampo.postparto.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cl.a2r.micampo.postparto.provider.MedControlSQLite;
import cl.a2r.micampo.app.provider.AppContentProvider;
import cl.a2r.micampo.postparto.provider.PostpartoContentProvider;
import cl.a2r.sip.model.Medicamento;
import cl.a2r.sip.model.MedicamentoControl;


/**
 * Created by fmartin on 11-08-2016.
 */
public class MedControlService {

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    protected static Context context;


    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public static Context getContext() { return context; }
    public static void setContext(Context _context) { context = _context; }


    // METODOS
    //----------------------------------------------------------------------------------------------
    /**
     * Buscar todos en AppSQLite
     * @return
     */
    public static List<MedicamentoControl> getAll() {
        Uri uri = PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL;
        String[] projection = getColumnsArray();

        List<MedicamentoControl> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                list.add(getMedicamentoControl(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Busca uno SQLite
     * @param id
     * @return
     */
    public static MedicamentoControl get(Integer id) {
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL + "/" + id);
        String[] projection = getColumnsArray();

        MedicamentoControl item = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            item = getMedicamentoControl(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }

        return item;
    }

    /**
     * Insertar nuevo en SQLite
     * @param item
     */
    public static void insert(MedicamentoControl item) {
        ContentValues values = getContentValues(item);
        getContext().getContentResolver().insert(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL, values);
    }

    /**
     * Insertar nuevos en SQLite
     * @param list
     */
    public static void insert(List<MedicamentoControl> list) {
        ContentValues[] values = getContentValues(list);
        getContext().getContentResolver().bulkInsert(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL, values);
    }

    /**
     * Actualizar valores de uno en SQLite
     * @param item
     */
    public static void update(MedicamentoControl item) {
        ContentValues values = getContentValues(item);
        getContext().getContentResolver().update(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL, values, null, null);
    }

    /**
     * Insertar o Actualizar en SQLite
     * Actualiza si existe e Inserta si no
     */
    public static void updateOrInsert(MedicamentoControl item) {
        ContentValues values = getContentValues(item);
        if (item.getId() == 0 || values.get("id") == null) {
            //Nuevo sin guardar, Insertar
            getContext().getContentResolver().insert(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL, values);
        }
        else {
            //Actualizar
            getContext().getContentResolver().update(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL, values, null, null);
        }
    }

    public static void delete() {
        getContext().getContentResolver().delete(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL, null, null);
    }

    /**
     * Elimina uno de SQLite
     * @param id
     */
    public static void delete(Integer id) {
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_MEDICAMENTOCONTROL + "/" + id);
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno de SQLite
     * @param item
     */
    public static void delete(MedicamentoControl item) {
        delete(item.getId());
    }


    // UTILES
    //----------------------------------------------------------------------------------------------

    /**
     * Retorna un arreglo con todos los campos de la tabla,
     * util para pasar como parametro "projection" en query
     * @return
     */
    protected static final String[] getColumnsArray() {
        return new String [] {
                MedControlSQLite.COLUMN_ID,
                MedControlSQLite.COLUMN_NOMBRE,
                MedControlSQLite.COLUMN_SERIE,
                MedControlSQLite.COLUMN_LOTE,
                MedControlSQLite.COLUMN_CANTIDAD
        };
    }

    /**
     * Extrae un objeto del cursor
     * @param c
     * @return
     */
    protected static MedicamentoControl getMedicamentoControl(Cursor c) {
        MedicamentoControl item = new MedicamentoControl();
        item.setId(c.getInt(c.getColumnIndexOrThrow(MedControlSQLite.COLUMN_ID)));
        item.setSerie(c.getInt(c.getColumnIndexOrThrow(MedControlSQLite.COLUMN_SERIE)));
        item.setLote(c.getInt(c.getColumnIndexOrThrow(MedControlSQLite.COLUMN_LOTE)));
        item.setCantidad(c.getInt(c.getColumnIndexOrThrow(MedControlSQLite.COLUMN_CANTIDAD)));
        Medicamento medicamento = new Medicamento();
        medicamento.setNombre(c.getString(c.getColumnIndexOrThrow(MedControlSQLite.COLUMN_NOMBRE)));
        item.setMed(medicamento);
        return item;
    }

    /**
     * Pone los atribuos en estructura de tipo ContentValues
     * util para usarse en Insert/Update de AppContentrProvider
     * @return ContentValues
     */
    protected static ContentValues getContentValues(MedicamentoControl item) {
        ContentValues values = new ContentValues();
        values.put(MedControlSQLite.COLUMN_ID, item.getId());
        values.put(MedControlSQLite.COLUMN_NOMBRE, item.getMed().getNombre());
        values.put(MedControlSQLite.COLUMN_SERIE, item.getSerie());
        values.put(MedControlSQLite.COLUMN_LOTE, item.getLote());
        values.put(MedControlSQLite.COLUMN_CANTIDAD, item.getCantidad());
        return values;
    }

    /**
     * Pone una lista en estructura de tipo ContentValues
     * para usarse en bulkInsert de AppContentrProvider
     * @return ContentValues[]
     */
    protected static ContentValues[] getContentValues(List<MedicamentoControl> list) {
        ContentValues[] result = new ContentValues[list.size()];

        if (list != null && list.size() > 0) {
            int i = 0;
            for (MedicamentoControl item : list) {
                ContentValues values = getContentValues(item);
                result[i] = values;
                i++;
            }
        }

        return result;
    }

}
