package cl.a2r.micampo.app.provider;

/**
 * Created by fmartin on 16-09-2016.
 */
public class MangadaDetalleSQLite {

    //TABLE ------------------------------------
    public static final String TABLE = "mangadadetalle";

    //COLUMNS -----------------------------------------
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_MANGADAID = "mangadaid";
    public static final String COLUMN_GANADOID = "ganadoid";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_MANGADAID + " INTEGER NOT NULL, " +
            COLUMN_GANADOID + " INTEGER NOT NULL " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;
}