package cl.a2r.micampo.postparto.provider;

/**
 * Created by fmartin on 11-08-2016.
 */

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;

import cl.a2r.micampo.app.provider.AppDatabaseHelper;

public class PostpartoContentProvider extends ContentProvider {

    // CONTENTPROVIDER
    //----------------------------------------------------------------------------------------------
    private static AppDatabaseHelper database;
    private static final String AUTHORITY = "cl.a2r.micampo.postparto.provider";

    public static final Uri CONTENT_URI_POSTPARTO = Uri.parse("content://" + AUTHORITY + "/" + PostpartoSQLite.TABLE);
    public static final Uri CONTENT_URI_DIAGNOSTICO = Uri.parse("content://" + AUTHORITY + "/" + DiagnosticoSQLite.TABLE);
    public static final Uri CONTENT_URI_MEDICAMENTOCONTROL = Uri.parse("content://" + AUTHORITY + "/" + MedControlSQLite.TABLE);
    public static final Uri CONTENT_URI_QUERY = Uri.parse("content://" + AUTHORITY + "/" + PostpartoSQLite.QUERY);

    // used for the UriMatcher
    private static final int POSTPARTOS = 10;
    private static final int POSTPARTO_ID = 20;

    private static final int DIAGNOSTICOS = 30;
    private static final int DIAGNOSTICO_ID = 40;

    private static final int MEDICAMENTOSCONTROL = 50;
    private static final int MEDICAMENTOSCONTROL_ID = 60;

    private static final int QUERY = 9999;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, PostpartoSQLite.TABLE, POSTPARTOS);
        sURIMatcher.addURI(AUTHORITY, PostpartoSQLite.TABLE + "/#", POSTPARTO_ID);

        sURIMatcher.addURI(AUTHORITY, DiagnosticoSQLite.TABLE, DIAGNOSTICOS);
        sURIMatcher.addURI(AUTHORITY, DiagnosticoSQLite.TABLE + "/#", DIAGNOSTICO_ID);

        sURIMatcher.addURI(AUTHORITY, MedControlSQLite.TABLE, MEDICAMENTOSCONTROL);
        sURIMatcher.addURI(AUTHORITY, MedControlSQLite.TABLE + "/#", MEDICAMENTOSCONTROL_ID);

        sURIMatcher.addURI(AUTHORITY, PostpartoSQLite.QUERY, QUERY);
    }

    @Override
    public boolean onCreate() {
        database = new AppDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Uisng SQLiteQueryBuilder instead of query() method
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        // check if the caller has requested a column which does not exists
        //checkColumns(projection);

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case QUERY:
                return database.getWritableDatabase().rawQuery(selection, selectionArgs);

            case POSTPARTO_ID:
                // adding the ID to the original query
                queryBuilder.setTables(PostpartoSQLite.TABLE);
                queryBuilder.appendWhere(PostpartoSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case POSTPARTOS:
                queryBuilder.setTables(PostpartoSQLite.TABLE);
                break;

            case DIAGNOSTICO_ID:
                queryBuilder.setTables(DiagnosticoSQLite.TABLE);
                queryBuilder.appendWhere(DiagnosticoSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case DIAGNOSTICOS:
                queryBuilder.setTables(DiagnosticoSQLite.TABLE);
                break;

            case MEDICAMENTOSCONTROL_ID:
                queryBuilder.setTables(MedControlSQLite.TABLE);
                queryBuilder.appendWhere(MedControlSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case MEDICAMENTOSCONTROL:
                queryBuilder.setTables(MedControlSQLite.TABLE);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }

        SQLiteDatabase db = database.getWritableDatabase();
        Cursor cursor = queryBuilder.query(db, projection, selection, selectionArgs, null, null, sortOrder);

        // make sure that potential listeners are getting notified
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sURIMatcher.match(uri)){
            case POSTPARTOS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + PostpartoSQLite.TABLE;
            case POSTPARTO_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + PostpartoSQLite.TABLE;

            case DIAGNOSTICOS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + DiagnosticoSQLite.TABLE;
            case DIAGNOSTICO_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + DiagnosticoSQLite.TABLE;

            case MEDICAMENTOSCONTROL:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + MedControlSQLite.TABLE;
            case MEDICAMENTOSCONTROL_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + MedControlSQLite.TABLE;

            default:
                //throw new IllegalArgumentException("Unsupported URI: " + uri);
                return null;
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        long id = 0;
        switch (uriType) {
            case POSTPARTOS:
                id = sqlDB.insert(PostpartoSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_POSTPARTO, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            case DIAGNOSTICOS:
                id = sqlDB.insert(DiagnosticoSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_DIAGNOSTICO, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            case MEDICAMENTOSCONTROL:
                id = sqlDB.insert(MedControlSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_MEDICAMENTOCONTROL, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        //getContext().getContentResolver().notifyChange(uri, null);
        return uri;//Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        int insertCount = 0;
        if (values.length == 0) {
            return insertCount;
        }

        SQLiteDatabase db = database.getWritableDatabase();
        try {
            switch (sURIMatcher.match(uri)) {
                case POSTPARTOS:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(PostpartoSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                            if (id > 0) {
                                insertCount++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        // Your error handling
                        return insertCount;
                    } finally {
                        db.endTransaction();
                    }
                    break;

                case DIAGNOSTICOS:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(DiagnosticoSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                            if (id > 0) {
                                insertCount++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        // Your error handling
                        return insertCount;
                    } finally {
                        db.endTransaction();
                    }
                    break;

                case MEDICAMENTOSCONTROL:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(MedControlSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
                            if (id > 0) {
                                insertCount++;
                            }
                        }
                        db.setTransactionSuccessful();
                    } catch (Exception e) {
                        // Your error handling
                        return insertCount;
                    } finally {
                        db.endTransaction();
                    }
                    break;

                default:
                    throw new IllegalArgumentException("Unknown URI " + uri);
            }
            getContext().getContentResolver().notifyChange(uri, null);
        } catch (Exception e) {
            //Log.i(TAG, "Exception : " + e);
            return insertCount;
        }

        return insertCount;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase db = database.getWritableDatabase();
        int rowsDeleted = 0;
        switch (uriType) {

            case POSTPARTOS:
                rowsDeleted = db.delete(PostpartoSQLite.TABLE, selection, selectionArgs);
                break;
            case POSTPARTO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(PostpartoSQLite.TABLE, PostpartoSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(PostpartoSQLite.TABLE, PostpartoSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case DIAGNOSTICOS:
                rowsDeleted = db.delete(DiagnosticoSQLite.TABLE, selection, selectionArgs);
                break;
            case DIAGNOSTICO_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(DiagnosticoSQLite.TABLE, DiagnosticoSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(DiagnosticoSQLite.TABLE, DiagnosticoSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case MEDICAMENTOSCONTROL:
                rowsDeleted = db.delete(MedControlSQLite.TABLE, selection, selectionArgs);
                break;
            case MEDICAMENTOSCONTROL_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(MedControlSQLite.TABLE, MedControlSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(MedControlSQLite.TABLE, MedControlSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        int uriType = sURIMatcher.match(uri);
        SQLiteDatabase sqlDB = database.getWritableDatabase();
        int rowsUpdated = 0;
        switch (uriType) {

            case POSTPARTOS:
                rowsUpdated = sqlDB.update(PostpartoSQLite.TABLE, values, selection, selectionArgs);
                break;
            case POSTPARTO_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PostpartoSQLite.TABLE, values, PostpartoSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(PostpartoSQLite.TABLE, values, PostpartoSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case DIAGNOSTICOS:
                rowsUpdated = sqlDB.update(DiagnosticoSQLite.TABLE, values, selection, selectionArgs);
                break;
            case DIAGNOSTICO_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(DiagnosticoSQLite.TABLE, values, DiagnosticoSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(DiagnosticoSQLite.TABLE, values, DiagnosticoSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case MEDICAMENTOSCONTROL:
                rowsUpdated = sqlDB.update(MedControlSQLite.TABLE, values, selection, selectionArgs);
                break;
            case MEDICAMENTOSCONTROL_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MedControlSQLite.TABLE, values, MedControlSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(MedControlSQLite.TABLE, values, MedControlSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsUpdated;
    }

//    private void checkColumns(String[] projection) {
//        String[] available = getColumnsArray();
//        if (projection != null) {
//            HashSet<String> requestedColumns = new HashSet<String>(Arrays.asList(projection));
//            HashSet<String> availableColumns = new HashSet<String>(Arrays.asList(available));
//            // check if all columns which are requested are available
//            if (!availableColumns.containsAll(requestedColumns)) {
//                throw new IllegalArgumentException("Unknown columns in projection");
//            }
//        }
//    }

}