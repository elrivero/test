package cl.a2r.micampo.postparto.service;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cl.a2r.micampo.app.model.Mangada;
import cl.a2r.micampo.app.model.MangadaDetalle;
import cl.a2r.micampo.app.service.AppService;
import cl.a2r.micampo.app.model.Diio;
import cl.a2r.micampo.app.service.MangadaDetalleService;
import cl.a2r.micampo.app.service.MangadaService;
import cl.a2r.micampo.postparto.model.Postparto;
import cl.a2r.micampo.postparto.provider.PostpartoContentProvider;
import cl.a2r.micampo.postparto.provider.PostpartoSQLite;
import cl.a2r.micampo.model.revpostparto.GanRevPostParto;

/* TODO poner tipos a los metodos, insert Uri, el resto int */
/* TODO poner try catch a los metodos y procesar en capa visual */

/**
 * Created by fmartin on 11-08-2016.
 */
public class PostpartoService {

    //public static final int POSTPARTO_MODO_REGISTRO = 0;
    //public static final int POSTPARTO_MODO_BUSQUEDA = 1;

    public static final Integer DIIOS_ACTIVITY_CANDIDATOS = 1;
    public static final Integer DIIOS_ACTIVITY_PROCESADOS = 2;

    public static final Integer POSTPARTO_DIAGNOSTICO_SELECCIONE = -1;
    public static final Integer POSTPARTO_DIAGNOSTICO_NORMAL = 4;

    public static final Integer POSTPARTO_TRATAMIENTO_SELECCIONE = -1;
    public static final Integer POSTPARTO_TRATAMIENTO_NINGUNO = 0;

    // ATRIBUTOS
    //----------------------------------------------------------------------------------------------
    private static Context context;

    // PROPIEDADES
    //----------------------------------------------------------------------------------------------
    public static Context getContext() { return context; }
    public static void setContext(Context _context) { context = _context; }

    public static Integer limpiarMagadas() {
        Integer count = 0;
        List<Postparto> list = getAll();
        if (list != null && list.size() > 0) {
            for (Postparto item : list) {
                item.setMangada(0);
                item.setSincronizado(1);
                limpiarMagada(item);
                count++;
            }
        }

        return count;
    }

    public static Integer limpiarMagada(Postparto item) {
        ContentValues values = getContentValues(item);
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + item.getId());
        return getContext().getContentResolver().update(uri, values, null, null);
    }

    public static Integer countPendienteRevision(Integer predioId, long fechacontrol) {
        List<Postparto> list = getPendienteRevision(predioId, fechacontrol);
        if (list == null) {
            return  0;
        }

        return list.size();
    }

    public static List<Postparto> getPendienteRevision(Integer predioId, long fechacontrol) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        //String[] selectionArgs = { predioId.toString(), String.valueOf(fechacontrol), String.valueOf(fechacontrol) };

        String query =
            "SELECT p.* " +
            "FROM postparto p " +
            "INNER JOIN ( " +
                "SELECT MAX(_id) _id " +
                "FROM postparto " +
                "GROUP BY ganadoid " +
            ") u " +
            "ON p._id = u._id " +
            "WHERE p.fundoid=%1 AND p.codrevision=1 AND p.diagnosticoid IS NULL AND p.medcontrolid IS NULL AND (0=%2 OR p.fechacontrol<=%2) ";
        query = query.replace("%1", predioId.toString());
        query = query.replace("%2", String.valueOf(fechacontrol));

        List<Postparto> postpartos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                postpartos.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return postpartos;
    }

    public static Integer countDeAlta(Integer predioId) {
        List<Postparto> list = getDeAlta(predioId);
        if (list == null) {
            return  0;
        }

        return list.size();
    }

    public static List<Postparto> getDeAlta(Integer predioId) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        String[] selectionArgs = { predioId.toString(), POSTPARTO_DIAGNOSTICO_NORMAL.toString() };

        String query =
            "SELECT p.* " +
            "FROM postparto p " +
            "INNER JOIN ( " +
                "SELECT MAX(_id) _id " +
                "FROM postparto " +
                "GROUP BY ganadoid " +
            ") u " +
            "ON p._id = u._id " +
            "WHERE p.fundoid=? AND p.diagnosticoid=? ";

        List<Postparto> postpartos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, selectionArgs, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                postpartos.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return postpartos;
    }

    public static Integer countEnTratamiento(Integer predioId, long fechacontrol) {
        List<Postparto> list = getEnTratamiento(predioId, fechacontrol);
        if (list == null) {
            return  0;
        }

        return list.size();
    }

    public static List<Postparto> getEnTratamiento(Integer predioId, long fechacontrol) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_QUERY;
        String[] projection = { "*" };
        //String[] selectionArgs = { predioId.toString(), Long.toString(fechacontrol), Long.toString(fechacontrol) };

        String query =
            "SELECT p.* " +
            "FROM postparto p " +
            "WHERE p.fundoid=%1 AND p.codrevision>1 AND (p.diagnosticoid IS NULL OR p.diagnosticoid=0) AND (0=%2 OR p.fechacontrol<=%2) ";
        query = query.replace("%1", predioId.toString());
        query = query.replace("%2", String.valueOf(fechacontrol));

        List<Postparto> postpartos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                postpartos.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return postpartos;
    }

    /**
     * Obtiene una lista de Diio a partir de una lista de Postparto
     * para usar en listado de DIIOs
     * @param postpartos
     * @return
     */
    public static ArrayList<Diio> getDiios(List<Postparto> postpartos, Integer actividadid) {
        ArrayList<Diio> list = new ArrayList<Diio>();
        for (Postparto item: postpartos) {
            Diio diio = new Diio(item.getDiio());
            Mangada mangada = null;
            if (item.getMangada() > 0) {
                mangada = MangadaService.get(item.getMangada());
                if (mangada != null && actividadid == mangada.getActividadId()) {
                    diio.setMangada(mangada.getNumero());
                }
            }

            if (mangada == null) {
                //Buscar lunes
                //Calendar calendar = Calendar.getInstance();
                //calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                //Date lunes = calendar.getTime();
                Date lunes = new Date();

                if (item.getGanPostparto().getIdDiagnostico() == POSTPARTO_DIAGNOSTICO_NORMAL) {
                    diio.setDescripcion("De alta");
                }
                else if (item.getGanPostparto().getCodRevision() == 1) {
                    if (item.getGanPostparto().getFechaControl().before(lunes)) {
                        diio.setDescripcion("Revisión atrasada");

                    }
                    else {
                        diio.setDescripcion("Revisión pendiente");
                    }
                }
                else {
                    if (item.getGanPostparto().getFechaControl().before(lunes)) {
                        diio.setDescripcion("Tratamiento atrasada");
                    }
                    else {
                        diio.setDescripcion("Tratamiento pendiente");
                    }
                }
            }
            else if (mangada != null && actividadid == mangada.getActividadId() && mangada.getActividadId() == AppService.ACTIVIDAD_POSTPARTO_REGISTRO) {
                if (item.getGanPostparto().getDiagnostico() != null && item.getGanPostparto().getDiagnostico().length() > 0
                        && item.getGanPostparto().getMedicamento() != null && item.getGanPostparto().getMedicamento().length() > 0) {
                    diio.setDescripcion(item.getGanPostparto().getDiagnostico() + " / " + item.getGanPostparto().getMedicamento());
                }
                else if (item.getGanPostparto().getDiagnostico() != null && item.getGanPostparto().getDiagnostico().length() > 0) {
                    diio.setDescripcion(item.getGanPostparto().getDiagnostico());
                }
            }

            list.add(diio);
        }
        return list;
    }

    /**
     * Cuenta la cantidad de Candidatos
     * @return
     */
    public static Integer countCandidatos(Integer predioid, Integer actividadid) {
        List<Postparto> list = getCandidatos(predioid, actividadid);
        if (list == null) {
            return  0;
        }

        return list.size();
    }

    /**
     * Busca el listado de Postpartos candidatos que no estan en mangadas
     * @return
     */
    public static List<Postparto> getCandidatos(Integer predioid, Integer actividadid) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        String query =
            "SELECT p.* " +
            "FROM postparto p " +
            "INNER JOIN ( " +
                "SELECT Max(_id) _id " +
                "FROM postparto p " +
                "GROUP BY p.ganadoid " +
            ") u " +
            "ON p._id = u._id " +
            "LEFT JOIN ( " +
                "SELECT d.* " +
                "FROM mangada m " +
                "INNER JOIN mangadadetalle d " +
                "ON m._id = d.mangadaid " +
                "WHERE m.predioid=%1 AND m.actividadid=%2 " +
            ") m " +
            "ON p.ganadoid = m.ganadoid " +
            "WHERE p.fundoid=%1 AND p.candidato > 0 AND m.ganadoid IS NULL " +
            "ORDER BY p.codrevision, p.fechacontrol,  p.diio";
        query = query.replace("%1", predioid.toString());
        query = query.replace("%2", actividadid.toString());

        List<Postparto> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                list.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Cuenta la cantidad de Procesados
     * @return
     */
//    public static Integer countProcesados(Integer predioid, Integer actividadid) {
//        List<Postparto> list = getProcesados(predioid, actividadid);
//        if (list == null) {
//            return  0;
//        }
//
//        return list.size();
//    }

    /**
     * Busca el listado de Procesados
     * @return
     */
    public static List<Postparto> getProcesados(Integer predioid, Integer actividadid) {
        List<Postparto> procesados = new ArrayList<>();

        List<MangadaDetalle> detalles = MangadaDetalleService.getByPredioActividad(predioid, actividadid);
        if (detalles != null && detalles.size() > 0) {
            for (MangadaDetalle detalle : detalles) {
                List<Postparto> list = getByGanadoId(detalle.getGanadoId());
                if (list != null && list.size() > 0) {
                    Postparto item = list.get(0);
                    if (item != null && item.getMangada() == 0) {
                        item = null;
                        if (list.size() > 1) {
                            item = list.get(1);
                        }
                    }
                    if (item != null) {
                        procesados.add(item);
                    }
                }
            }
        }

        return procesados;
    }

    /**
     * Busca el listado en Mangada actual
     * @return
     */
//    public static List<Postparto> getMangadaActual(Integer predioId, Integer mangada) {
//        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
//        String[] projection = getColumnsArray();
//        String selection = PostpartoSQLite.COLUMN_FUNDOID + "=? AND " + PostpartoSQLite.COLUMN_MANGADA + "=?";
//        String[] selectionArgs = { predioId.toString(), mangada.toString() };
//        String orderBy = PostpartoSQLite.COLUMN_MANGADA + ", " + PostpartoSQLite.COLUMN_DIIO;
//
//        List<Postparto> postpartos = new ArrayList<>();
//
//        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
//        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
//            do {
//                postpartos.add(getPostparto(cursor));
//            } while (cursor.moveToNext());
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//
//        return postpartos;
//    }

    /**
     * Cuenta los candidatos en mangada
     * @return
     */
//    public static Integer countMangadaActual(Integer predioId, Integer mangada) {
//        if (mangada == 0) {
//            return 0;
//        }
//
//        return getMangadaActual(predioId, mangada).size();
//    }


    public static String getDiio(Integer diio) {
        if (diio == 0) {
            return "DIIO";
        }

        String _diio = String.format("%010d", diio);
        return _diio.substring(0, 2) + " " + _diio.substring(2, 5) + " " + _diio.substring(5);
    }

    // METODOS
    //----------------------------------------------------------------------------------------------
    /**
     * Buscar historico de un DIIO en AppSQLite
     * @return
     */
    public static List<Postparto> getByGanadoId(Integer ganadoid) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        String selection = PostpartoSQLite.COLUMN_GANADOID + "=? ";
        String[] selectionArgs = { ganadoid.toString() };
        String orderBy = PostpartoSQLite.COLUMN_CODREVISION + " DESC ";

        List<Postparto> list = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                list.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return list;
    }

    /**
     * Buscar candidato de un DIIO en AppSQLite
     * @return
     */
    public static Postparto getCandidatoPorDiio(Integer diio) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        //String selection = PostpartoSQLite.COLUMN_DIIO + "=? AND " + PostpartoSQLite.COLUMN_CANDIDATO + ">0 AND " + PostpartoSQLite.COLUMN_MANGADA + "=0";
        String selection = PostpartoSQLite.COLUMN_DIIO + "=?";
        String[] selectionArgs = { diio.toString() };
        String orderBy = PostpartoSQLite.COLUMN_FECHACONTROL + " DESC ";

        Postparto item = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                item = getPostparto(cursor);
                break;
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return item;
    }

    /**
     * Busca un registro porstparto dado su diio y su codRevision
     * @param diio
     * @param codRevision
     * @return
     */
    public static Postparto getPorDiioCodRevision(Integer diio, Integer codRevision) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        String selection = PostpartoSQLite.COLUMN_DIIO + "=? AND " + PostpartoSQLite.COLUMN_CODREVISION + "=? ";
        String[] selectionArgs = { diio.toString(), codRevision.toString() };
        String orderBy = PostpartoSQLite.COLUMN_DIIO + ", " + PostpartoSQLite.COLUMN_CODREVISION;

        Postparto item = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                item = getPostparto(cursor);
                break;
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return item;
    }

    /**
     * Buscar postpartos de un predio
     * @param predioId
     * @return
     */
    public static List<Postparto> getPorPredio(Integer predioId) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        String selection = PostpartoSQLite.COLUMN_FUNDOID + "=? ";
        String[] selectionArgs = { predioId.toString() };
        String orderBy = PostpartoSQLite.COLUMN_DIIO + ", " + PostpartoSQLite.COLUMN_CODREVISION;

        List<Postparto> postpartos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                postpartos.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return postpartos;
    }

    /**
     * Buscar postparto procesado de un ganado
     * @param ganadoid
     * @return
     */
    public static Postparto getByGanadoIdPenultimo(Integer ganadoid) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        String query =
            "SELECT p.* " +
            "FROM postparto p " +
            "LEFT JOIN ( " +
                "SELECT p.* " +
                "FROM postparto p " +
                "WHERE p.ganadoid=%1 " +
                "ORDER BY p._id DESC " +
                "LIMIT 1" +
            ") u " +
            "ON p._id = u._id " +
            "WHERE p.ganadoid=%1 AND u.ganadoid IS NULL " +
            "ORDER BY p._id DESC " +
            "LIMIT 1 ";
        query = query.replace("%1", ganadoid.toString());

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            Postparto item = getPostparto(cursor);
            cursor.close();
            return item;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

     public static Postparto getByGanadoIdUltimo(Integer ganadoid) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_QUERY;
        String[] projection = {};
        String query =
            "SELECT p.* " +
            "FROM postparto p " +
            "WHERE p.ganadoid=%1 " +
            "ORDER BY p._id DESC " +
            "LIMIT 1 ";
        query = query.replace("%1", ganadoid.toString());

        Cursor cursor = getContext().getContentResolver().query(uri, projection, query, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            Postparto item = getPostparto(cursor);
            cursor.close();
            return item;
        }

        if (cursor != null) {
            cursor.close();
        }

        return null;
    }

    public static Postparto getCandidatoPorDiioPenultimo(Integer diio) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        //String selection = PostpartoSQLite.COLUMN_DIIO + "=? AND " + PostpartoSQLite.COLUMN_CANDIDATO + ">0 AND " + PostpartoSQLite.COLUMN_MANGADA + "=0";
        String selection = PostpartoSQLite.COLUMN_DIIO + "=?";
        String[] selectionArgs = { diio.toString() };
        String orderBy = PostpartoSQLite.COLUMN_FECHACONTROL + " DESC ";

        Postparto item = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            Integer i= 0;
            do {
                if (i == 1) {
                    item = getPostparto(cursor);
                    break;
                }
                i++;
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return item;
    }

    public static Postparto getCandidatoPorEid(String eid) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        //String selection = PostpartoSQLite.COLUMN_DIIO + "=? AND " + PostpartoSQLite.COLUMN_CANDIDATO + ">0 AND " + PostpartoSQLite.COLUMN_MANGADA + "=0";
        String selection = PostpartoSQLite.COLUMN_EID + "=?";
        String[] selectionArgs = { eid };
        String orderBy = PostpartoSQLite.COLUMN_FECHACONTROL + " DESC ";

        Postparto item = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                item = getPostparto(cursor);
                break;
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return item;
    }

    /**
     * Buscar todos en AppSQLite
     * @return
     */
    public static List<Postparto> getAll() {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();

        List<Postparto> postpartos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                postpartos.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return postpartos;
    }

    public static Integer countNoSincronizados() {
        return getNoSincronizados().size();
    }

    public static List<Postparto> getNoSincronizados() {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        String selection = PostpartoSQLite.COLUMN_SINCRONIZADO + "=0";
        String orderBy = PostpartoSQLite.COLUMN_FECHACONTROL;

        List<Postparto> postpartos = new ArrayList<>();

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, null, orderBy);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            do {
                postpartos.add(getPostparto(cursor));
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        return postpartos;
    }

    /**
     * Busca uno SQLite
     * @param id
     * @return
     */
    public static Postparto get(Integer id) {
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + id);
        String[] projection = getColumnsArray();

        Postparto postparto = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            postparto = getPostparto(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }

        return postparto;
    }

    public static Postparto getByGanadoIdAndCodRevision(Integer ganadoid, Integer codRevision) {
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        String[] projection = getColumnsArray();
        String selection = PostpartoSQLite.COLUMN_GANADOID + "=? AND " + PostpartoSQLite.COLUMN_CODREVISION + "=? ";
        String[] selectionArgs = { ganadoid.toString(), codRevision.toString() };

        Postparto postparto = null;

        Cursor cursor = getContext().getContentResolver().query(uri, projection, selection, selectionArgs, null);
        if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
            postparto = getPostparto(cursor);
        }

        if (cursor != null) {
            cursor.close();
        }

        return postparto;
    }

    /**
     * Extrae un objeto del cursor
     * @param c
     * @return
     */
    protected static Postparto getPostparto(Cursor c) {
        Postparto postparto = new Postparto();
        postparto.setId(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_ID)));
        postparto.setSincronizado(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_SINCRONIZADO)));
        postparto.setMangada(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_MANGADA)));
        //postparto.setModo(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_MODO)));
        postparto.setCandidato(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_CANDIDATO)));

        //GanPostparto
        GanRevPostParto ganado = new GanRevPostParto();
        ganado.setIdFundo(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_FUNDOID)));
        ganado.setIdGanado(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_GANADOID)));
        ganado.setDiio(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_DIIO)));
        ganado.setEid(c.getString(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_EID)));

        ganado.setIdGanado(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_GANADOID)));
        ganado.setDiio(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_DIIO)));
        ganado.setEid(c.getString(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_DIIO)));

        ganado.setCandidato(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_CANDIDATO)) == 1 ? true : false);
        ganado.setFechaParto(new Date(c.getLong(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_FECHAPARTO))));
        ganado.setIdTipoParto(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_TIPOPARTOID)));
        ganado.setCodRevision(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_CODREVISION)));
        ganado.setIdDiagnostico(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_DIAGNOSTICOID)));
        ganado.setDiagnostico(c.getString(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_DIAGNOSTICO)));

        ganado.setIdMedControl(c.getInt(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_MEDCONTROLID)));
        ganado.setMedicamento(c.getString(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_MEDICAMENTO)));
        ganado.setFechaControl(new Date(c.getLong(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_FECHACONTROL))));
        ganado.setIdUsuario(AppService.getUserId());
        ganado.setMensaje(c.getString(c.getColumnIndexOrThrow(PostpartoSQLite.COLUMN_MENSAJE)));

        postparto.setGanPostparto(ganado);

        return postparto;
    }

    /**
     * Insertar nuevo en SQLite
     * @param postparto
     */
    public static Uri insert(Postparto postparto) {
        ContentValues values = getContentValues(postparto);
        Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
        return getContext().getContentResolver().insert(uri, values);
    }

    /**
     * Insertar nuevos en SQLite
     * @param postpartos
     */
    public static int insert(List<Postparto> postpartos) {
        ContentValues[] values = getContentValues(postpartos);
        getContext().getContentResolver().bulkInsert(PostpartoContentProvider.CONTENT_URI_POSTPARTO, values);
        //List<Postparto> list = getAll();
        return 0;
    }

    /**
     * Actualizar valores de uno en SQLite
     * @param postparto
     */
    public static int update(Postparto postparto) {
//        List<Postparto> list = getAll();
//        if (list != null && list.size() > 0) {
//            for (Postparto item : list){
//                Log.d("debug", String.format("id: %1$d diio: %2$d", item.getId(), item.getDiio()));
//            }
//        }

        ContentValues values = getContentValues(postparto);
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + postparto.getId());
        return getContext().getContentResolver().update(uri, values, null, null);
    }

    /**
     * Insertar o Actualizar en SQLite
     * Actualiza si existe e Inserta si no
     */
    public static void updateOrInsert(Postparto postparto) {

        //Verificar si ya se habia guardado otro registro con ese ganadoid
        //para resolver el caso de cuando se busca en webservice por eid y no se devuelve el eid
        Postparto item = getByGanadoIdAndCodRevision(postparto.getGanPostparto().getIdGanado(), postparto.getGanPostparto().getCodRevision());
        if (item != null) {
            ContentValues values = getContentValues(item);
            values.put(PostpartoSQLite.COLUMN_ID, item.getId());
            //Actualizar
            Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + item.getId());
            getContext().getContentResolver().update(uri, values, null, null);
        }
        else {

            //Buscar normal por llave id
            ContentValues values = getContentValues(postparto);
            if (postparto.getId() == null || postparto.getId() == 0) {
                //Nuevo sin guardar, Insertar
                Uri uri = PostpartoContentProvider.CONTENT_URI_POSTPARTO;
                getContext().getContentResolver().insert(uri, values);
            }
            else {
                //Actualizar
                Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + postparto.getId());
                getContext().getContentResolver().update(uri, values, null, null);
            }
        }
    }

    public static int delete() {
        return getContext().getContentResolver().delete(PostpartoContentProvider.CONTENT_URI_POSTPARTO, null, null);
    }

    /**
     * Elimina un postparto por diio y codRevision
     * @param diio
     * @param codRevision
     */
    public static int delete(Integer diio, Integer codRevision) {
        Postparto item = getPorDiioCodRevision(diio, codRevision);
        if (item != null && item.getId() != null && item.getId() > 0) {
            Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + item.getId());
            return getContext().getContentResolver().delete(uri, null, null);
        }

        return 0;
    }

    public static int deletePorPredio(Integer predioId) {
        List<Postparto> list = getPorPredio(predioId);
        if (list != null && list.size() > 0) {
            Integer i = 0;
            for (Postparto item: list) {
                Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + item.getId());
                getContext().getContentResolver().delete(uri, null, null);
                i++;
            }
            return i;
        }

        return 0;
    }

    /**
     * Elimina uno de SQLite
     * @param id
     */
    public static int delete(Integer id) {
        Uri uri = Uri.parse(PostpartoContentProvider.CONTENT_URI_POSTPARTO + "/" + id);
        return getContext().getContentResolver().delete(uri, null, null);
    }

    /**
     * Elimina uno de SQLite
     * @param postparto
     */
    public static int delete(Postparto postparto) {
        return delete(postparto.getId());
    }


    // UTILES
    //----------------------------------------------------------------------------------------------
    /**
     * Pone los atribuos en estructura de tipo ContentValues
     * util para usarse en Insert/Update de AppContentrProvider
     * @return ContentValues
     */
    protected static ContentValues getContentValues(Postparto postparto) {
        ContentValues values = new ContentValues();
        values.put(PostpartoSQLite.COLUMN_ID, postparto.getId());
        values.put(PostpartoSQLite.COLUMN_SINCRONIZADO, postparto.getSincronizado());
        values.put(PostpartoSQLite.COLUMN_MANGADA, postparto.getMangada());
        //values.put(PostpartoSQLite.COLUMN_MODO, postparto.getModo());

        GanRevPostParto ganPostparto = postparto.getGanPostparto();
        values.put(PostpartoSQLite.COLUMN_FUNDOID, ganPostparto.getIdFundo());
        values.put(PostpartoSQLite.COLUMN_GANADOID, ganPostparto.getIdGanado());
        values.put(PostpartoSQLite.COLUMN_DIIO, ganPostparto.getDiio());
        if (ganPostparto.getEid() != null) {
            values.put(PostpartoSQLite.COLUMN_EID, ganPostparto.getEid());
        }
        else {
            values.put(PostpartoSQLite.COLUMN_EID, "");
        }

        values.put(PostpartoSQLite.COLUMN_CANDIDATO, postparto.getCandidato() > 0 || ganPostparto.isCandidato() ? 1 : 0);

        if (ganPostparto.getFechaParto() != null) {
            values.put(PostpartoSQLite.COLUMN_FECHAPARTO, ganPostparto.getFechaParto().getTime());
        }
        else {
            values.put(PostpartoSQLite.COLUMN_FECHAPARTO, new Date().getTime());
        }

        values.put(PostpartoSQLite.COLUMN_TIPOPARTOID, ganPostparto.getIdTipoParto());
        values.put(PostpartoSQLite.COLUMN_CODREVISION, ganPostparto.getCodRevision());
        values.put(PostpartoSQLite.COLUMN_DIAGNOSTICOID, ganPostparto.getIdDiagnostico());
        values.put(PostpartoSQLite.COLUMN_DIAGNOSTICO, ganPostparto.getDiagnostico());
        values.put(PostpartoSQLite.COLUMN_MEDCONTROLID, ganPostparto.getIdMedControl());
        values.put(PostpartoSQLite.COLUMN_MEDICAMENTO, ganPostparto.getMedicamento());

        if (ganPostparto.getFechaControl() != null) {
            values.put(PostpartoSQLite.COLUMN_FECHACONTROL, ganPostparto.getFechaControl().getTime());
        }
        else {
            values.put(PostpartoSQLite.COLUMN_FECHACONTROL, new Date().getTime());
        }

        values.put(PostpartoSQLite.COLUMN_MENSAJE, ganPostparto.getMensaje());

        return values;
    }

    /**
     * Pone una lista en estructura de tipo ContentValues
     * para usarse en bulkInsert de AppContentrProvider
     * @return ContentValues[]
     */
    protected static ContentValues[] getContentValues(List<Postparto> postpartos) {
        ContentValues[] result = new ContentValues[postpartos.size()];

        if (postpartos != null && postpartos.size() > 0) {
            int i = 0;
            for (Postparto postparto : postpartos) {
                ContentValues values = getContentValues(postparto);
                result[i] = values;
                i++;
            }
        }

        return result;
    }

    /**
     * Retorna un arreglo con todos los campos de la tabla,
     * util para pasar como parametro "projection" en query
     * @return
     */
    protected static final String[] getColumnsArray() {
        return new String [] {
            PostpartoSQLite.COLUMN_ID,
            PostpartoSQLite.COLUMN_SINCRONIZADO,
            PostpartoSQLite.COLUMN_MANGADA,

            PostpartoSQLite.COLUMN_FUNDOID,
            PostpartoSQLite.COLUMN_GANADOID,
            PostpartoSQLite.COLUMN_DIIO,
            PostpartoSQLite.COLUMN_EID,

            PostpartoSQLite.COLUMN_CANDIDATO,
            PostpartoSQLite.COLUMN_FECHAPARTO,
            PostpartoSQLite.COLUMN_TIPOPARTOID,
            PostpartoSQLite.COLUMN_CODREVISION,
            PostpartoSQLite.COLUMN_DIAGNOSTICOID,
            PostpartoSQLite.COLUMN_DIAGNOSTICO,
            PostpartoSQLite.COLUMN_MEDCONTROLID,
            PostpartoSQLite.COLUMN_MEDICAMENTO,
            PostpartoSQLite.COLUMN_FECHACONTROL,
            PostpartoSQLite.COLUMN_MENSAJE,
        };
    }

}
