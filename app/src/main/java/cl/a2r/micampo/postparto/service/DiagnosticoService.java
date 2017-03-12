package cl.a2r.micampo.postparto.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

import cl.a2r.micampo.postparto.provider.DiagnosticoSQLite;
import cl.a2r.micampo.app.provider.AppContentProvider;
import cl.a2r.micampo.model.revpostparto.Diagnostico;
import cl.a2r.micampo.postparto.provider.PostpartoContentProvider;


/**
 * Created by fmartin on 11-08-2016.
 */
public class DiagnosticoService   {

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
    public static List<Diagnostico> getAll() {
        Uri uri = PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO;
        String[] projection = getColumnsArray();

        List<Diagnostico> diagnosticos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                diagnosticos.add(getDiagnostico(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return diagnosticos;
    }

    /**
     * Busca uno SQLite
     * @param id
     * @return
     */
    public static Diagnostico get(Integer id) {
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO + "/" + id);
        String[] projection = getColumnsArray();

        Diagnostico diagnostico = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            diagnostico = getDiagnostico(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }

        return diagnostico;
    }

    /**
     * Insertar nuevo en SQLite
     * @param diagnostico
     */
    public static void insert(Diagnostico diagnostico) {
        ContentValues values = getContentValues(diagnostico);
        getContext().getContentResolver().insert(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO, values);
    }

    /**
     * Insertar nuevos en SQLite
     * @param diagnosticos
     */
    public static void insert(List<Diagnostico> diagnosticos) {
        ContentValues[] values = getContentValues(diagnosticos);
        getContext().getContentResolver().bulkInsert(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO, values);
    }

    /**
     * Actualizar valores de uno en SQLite
     * @param diagnostico
     */
    public static void update(Diagnostico diagnostico) {
        ContentValues values = getContentValues(diagnostico);
        getContext().getContentResolver().update(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO, values, null, null);
    }

    /**
     * Insertar o Actualizar en SQLite
     * Actualiza si existe e Inserta si no
     */
    public static void updateOrInsert(Diagnostico diagnostico) {
        ContentValues values = getContentValues(diagnostico);
        if (diagnostico.getId() == 0 || values.get("id") == null) {
            //Nuevo sin guardar, Insertar
            getContext().getContentResolver().insert(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO, values);
        }
        else {
            //Actualizar
            getContext().getContentResolver().update(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO, values, null, null);
        }
    }

    public static void delete() {
        getContext().getContentResolver().delete(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO, null, null);
    }

    /**
     * Elimina uno de SQLite
     * @param id
     */
    public static void delete(Integer id) {
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_DIAGNOSTICO + "/" + id);
        getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno de SQLite
     * @param diagnostico
     */
    public static void delete(Diagnostico diagnostico) {
        delete(diagnostico.getId());
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
                DiagnosticoSQLite.COLUMN_ID,
                DiagnosticoSQLite.COLUMN_CODIGO,
                DiagnosticoSQLite.COLUMN_NOMBRE
        };
    }

    /**
     * Extrae un objeto del cursor
     * @param c
     * @return
     */
    protected static Diagnostico getDiagnostico(Cursor c) {
        Diagnostico diagnostico = new Diagnostico();
        diagnostico.setId(c.getInt(c.getColumnIndexOrThrow(DiagnosticoSQLite.COLUMN_ID)));
        diagnostico.setCodigo(c.getString(c.getColumnIndexOrThrow(DiagnosticoSQLite.COLUMN_CODIGO)));
        diagnostico.setNombre(c.getString(c.getColumnIndexOrThrow(DiagnosticoSQLite.COLUMN_NOMBRE)));
        return diagnostico;
    }

    /**
     * Pone los atribuos en estructura de tipo ContentValues
     * util para usarse en Insert/Update de AppContentrProvider
     * @return ContentValues
     */
    protected static ContentValues getContentValues(Diagnostico diagnostico) {
        ContentValues values = new ContentValues();
        values.put(DiagnosticoSQLite.COLUMN_ID, diagnostico.getId());
        values.put(DiagnosticoSQLite.COLUMN_CODIGO, diagnostico.getCodigo());
        values.put(DiagnosticoSQLite.COLUMN_NOMBRE, diagnostico.getNombre());
        return values;
    }

    /**
     * Pone una lista en estructura de tipo ContentValues
     * para usarse en bulkInsert de AppContentrProvider
     * @return ContentValues[]
     */
    protected static ContentValues[] getContentValues(List<Diagnostico> diagnosticos) {
        ContentValues[] result = new ContentValues[diagnosticos.size()];

        if (diagnosticos != null && diagnosticos.size() > 0) {
            int i = 0;
            for (Diagnostico iagnostico : diagnosticos) {
                ContentValues values = getContentValues(iagnostico);
                result[i] = values;
                i++;
            }
        }

        return result;
    }

}
