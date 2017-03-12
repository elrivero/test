package cl.a2r.micampo.app.provider;

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

public class AppContentProvider extends ContentProvider {

    // CONTENTPROVIDER
    //----------------------------------------------------------------------------------------------
    private static AppDatabaseHelper database;
    private static final String AUTHORITY = "cl.a2r.micampo.app.provider";

    public static final Uri CONTENT_URI_APP = Uri.parse("content://" + AUTHORITY + "/" + AppSQLite.TABLE);
    public static final Uri CONTENT_URI_CLIENT = Uri.parse("content://" + AUTHORITY + "/" + ClienteSQLite.TABLE);
    public static final Uri CONTENT_URI_PREDIO = Uri.parse("content://" + AUTHORITY + "/" + PredioSQLite.TABLE);
    public static final Uri CONTENT_URI_MANGADA = Uri.parse("content://" + AUTHORITY + "/" + MangadaSQLite.TABLE);
    public static final Uri CONTENT_URI_MANGADADETALLE = Uri.parse("content://" + AUTHORITY + "/" + MangadaDetalleSQLite.TABLE);
    public static final Uri CONTENT_URI_QUERY = Uri.parse("content://" + AUTHORITY + "/" + AppSQLite.QUERY);

    // used for the UriMatcher
    private static final int APPS = 10;
    private static final int APP_ID = 20;

    private static final int CLIENTS = 30;
    private static final int CLIENT_ID = 40;

    private static final int PREDIOS = 50;
    private static final int PREDIO_ID = 60;

    private static final int MANGADAS = 70;
    private static final int MANGADA_ID = 80;

    private static final int MANGADADETALLES = 90;
    private static final int MANGADADETALLE_ID = 100;

    private static final int QUERY = 9999;

    private static final UriMatcher sURIMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        sURIMatcher.addURI(AUTHORITY, AppSQLite.TABLE, APPS);
        sURIMatcher.addURI(AUTHORITY, AppSQLite.TABLE + "/#", APP_ID);

        sURIMatcher.addURI(AUTHORITY, ClienteSQLite.TABLE, CLIENTS);
        sURIMatcher.addURI(AUTHORITY, ClienteSQLite.TABLE + "/#", CLIENT_ID);

        sURIMatcher.addURI(AUTHORITY, PredioSQLite.TABLE, PREDIOS);
        sURIMatcher.addURI(AUTHORITY, PredioSQLite.TABLE + "/#", PREDIO_ID);

        sURIMatcher.addURI(AUTHORITY, MangadaSQLite.TABLE, MANGADAS);
        sURIMatcher.addURI(AUTHORITY, MangadaSQLite.TABLE + "/#", MANGADA_ID);

        sURIMatcher.addURI(AUTHORITY, MangadaDetalleSQLite.TABLE, MANGADADETALLES);
        sURIMatcher.addURI(AUTHORITY, MangadaDetalleSQLite.TABLE + "/#", MANGADADETALLE_ID);

        sURIMatcher.addURI(AUTHORITY, AppSQLite.QUERY, QUERY);
    }

    @Override
    public boolean onCreate() {
        database = new AppDatabaseHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();

        int uriType = sURIMatcher.match(uri);
        switch (uriType) {
            case QUERY:
                return database.getWritableDatabase().rawQuery(selection, selectionArgs);

            case APP_ID:
                queryBuilder.setTables(AppSQLite.TABLE);
                queryBuilder.appendWhere(AppSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case APPS:
                queryBuilder.setTables(AppSQLite.TABLE);
                break;

            case CLIENT_ID:
                queryBuilder.setTables(ClienteSQLite.TABLE);
                queryBuilder.appendWhere(ClienteSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case CLIENTS:
                queryBuilder.setTables(ClienteSQLite.TABLE);
                break;

            case PREDIO_ID:
                queryBuilder.setTables(PredioSQLite.TABLE);
                queryBuilder.appendWhere(PredioSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case PREDIOS:
                queryBuilder.setTables(PredioSQLite.TABLE);
                break;

            case MANGADA_ID:
                queryBuilder.setTables(MangadaSQLite.TABLE);
                queryBuilder.appendWhere(MangadaSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case MANGADAS:
                queryBuilder.setTables(MangadaSQLite.TABLE);
                break;

            case MANGADADETALLE_ID:
                queryBuilder.setTables(MangadaDetalleSQLite.TABLE);
                queryBuilder.appendWhere(MangadaDetalleSQLite.COLUMN_ID + "=" + uri.getLastPathSegment());
                break;
            case MANGADADETALLES:
                queryBuilder.setTables(MangadaDetalleSQLite.TABLE);
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
            case APPS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + AppSQLite.TABLE;
            case APP_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + AppSQLite.TABLE;

            case CLIENTS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + ClienteSQLite.TABLE;
            case CLIENT_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + ClienteSQLite.TABLE;

            case PREDIOS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + PredioSQLite.TABLE;
            case PREDIO_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + PredioSQLite.TABLE;

            case MANGADAS:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + MangadaSQLite.TABLE;
            case MANGADA_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + MangadaSQLite.TABLE;

            case MANGADADETALLES:
                return "vnd.android.cursor.dir/" + AUTHORITY + "/" + MangadaDetalleSQLite.TABLE;
            case MANGADADETALLE_ID:
                return "vnd.android.cursor.item/" + AUTHORITY + "/" + MangadaDetalleSQLite.TABLE;

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
            case APPS:
                id = sqlDB.insert(AppSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_APP, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            case CLIENTS:
                id = sqlDB.insert(ClienteSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_CLIENT, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            case PREDIOS:
                id = sqlDB.insert(PredioSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_PREDIO, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            case MANGADAS:
                id = sqlDB.insert(MangadaSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_MANGADA, id);
                    getContext().getContentResolver().notifyChange(uri, null);
                }
                break;

            case MANGADADETALLES:
                id = sqlDB.insert(MangadaDetalleSQLite.TABLE, null, values);
                if (id > 0) {
                    uri = ContentUris.withAppendedId(CONTENT_URI_MANGADADETALLE, id);
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
                case CLIENTS:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(ClienteSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
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

                case PREDIOS:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(PredioSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
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

                case MANGADAS:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(MangadaSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
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

                case MANGADADETALLES:
                    try {

                        db.beginTransaction();
                        for (ContentValues value : values) {
                            long id = db.insertWithOnConflict(MangadaDetalleSQLite.TABLE, null, value, SQLiteDatabase.CONFLICT_REPLACE);
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
            case APPS:
                rowsDeleted = db.delete(AppSQLite.TABLE, selection, selectionArgs);
                break;
            case APP_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(AppSQLite.TABLE, AppSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(AppSQLite.TABLE, AppSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case CLIENTS:
                rowsDeleted = db.delete(ClienteSQLite.TABLE, selection, selectionArgs);
                break;
            case CLIENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(ClienteSQLite.TABLE, AppSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(ClienteSQLite.TABLE, AppSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case PREDIOS:
                rowsDeleted = db.delete(PredioSQLite.TABLE, selection, selectionArgs);
                break;
            case PREDIO_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(PredioSQLite.TABLE, AppSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(PredioSQLite.TABLE, AppSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case MANGADAS:
                rowsDeleted = db.delete(MangadaSQLite.TABLE, selection, selectionArgs);
                break;
            case MANGADA_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(MangadaSQLite.TABLE, MangadaSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(MangadaSQLite.TABLE, MangadaSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case MANGADADETALLES:
                rowsDeleted = db.delete(MangadaDetalleSQLite.TABLE, selection, selectionArgs);
                break;
            case MANGADADETALLE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsDeleted = db.delete(MangadaDetalleSQLite.TABLE, MangadaDetalleSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsDeleted = db.delete(MangadaDetalleSQLite.TABLE, MangadaDetalleSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
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
            case APPS:
                rowsUpdated = sqlDB.update(AppSQLite.TABLE, values, selection, selectionArgs);
                break;
            case APP_ID:
                String id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(AppSQLite.TABLE, values, AppSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(AppSQLite.TABLE, values, AppSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case CLIENTS:
                rowsUpdated = sqlDB.update(ClienteSQLite.TABLE, values, selection, selectionArgs);
                break;
            case CLIENT_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(ClienteSQLite.TABLE, values, AppSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(ClienteSQLite.TABLE, values, AppSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case PREDIOS:
                rowsUpdated = sqlDB.update(PredioSQLite.TABLE, values, selection, selectionArgs);
                break;
            case PREDIO_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(PredioSQLite.TABLE, values, AppSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(PredioSQLite.TABLE, values, AppSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case MANGADAS:
                rowsUpdated = sqlDB.update(MangadaSQLite.TABLE, values, selection, selectionArgs);
                break;
            case MANGADA_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MangadaSQLite.TABLE, values, MangadaSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(MangadaSQLite.TABLE, values, MangadaSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
                }
                break;

            case MANGADADETALLES:
                rowsUpdated = sqlDB.update(MangadaDetalleSQLite.TABLE, values, selection, selectionArgs);
                break;
            case MANGADADETALLE_ID:
                id = uri.getLastPathSegment();
                if (TextUtils.isEmpty(selection)) {
                    rowsUpdated = sqlDB.update(MangadaDetalleSQLite.TABLE, values, MangadaDetalleSQLite.COLUMN_ID + "=" + id, null);
                }
                else {
                    rowsUpdated = sqlDB.update(MangadaDetalleSQLite.TABLE, values, MangadaDetalleSQLite.COLUMN_ID + "=" + id + " and " + selection, selectionArgs);
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