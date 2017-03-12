package cl.a2r.micampo.app.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import cl.a2r.micampo.postparto.provider.DiagnosticoSQLite;
import cl.a2r.micampo.postparto.provider.MedControlSQLite;
import cl.a2r.micampo.postparto.provider.PostpartoSQLite;

/**
 * Created by fmartin on 16-08-2016.
 */
public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "micampo.db";
    public static final int DATABASE_VERSION = 9;

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(AppSQLite.CREATE);
        db.execSQL(ClienteSQLite.CREATE);
        db.execSQL(PredioSQLite.CREATE);
        db.execSQL(MangadaSQLite.CREATE);
        db.execSQL(MangadaDetalleSQLite.CREATE);
        db.execSQL(PostpartoSQLite.CREATE);
        db.execSQL(DiagnosticoSQLite.CREATE);
        db.execSQL(MedControlSQLite.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL(AppSQLite.DROP);
        db.execSQL(ClienteSQLite.DROP);
        db.execSQL(PredioSQLite.DROP);
        db.execSQL(MangadaSQLite.DROP);
        db.execSQL(MangadaDetalleSQLite.DROP);
        db.execSQL(PostpartoSQLite.DROP);
        db.execSQL(DiagnosticoSQLite.DROP);
        db.execSQL(MedControlSQLite.DROP);

        onCreate(db);
    }
}
