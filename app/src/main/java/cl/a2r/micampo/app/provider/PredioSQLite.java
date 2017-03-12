package cl.a2r.micampo.app.provider;

/**
 * Created by fmartin on 16-08-2016.
 */
public class PredioSQLite {

    //TABLE ------------------------------------
    public static final String TABLE = "predio";

    //COLUMNS -----------------------------------------
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODIGO = "codigo";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_CLIENTID = "clientid";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_CODIGO + " TEXT NOT NULL, " +
            COLUMN_NOMBRE + " TEXT NOT NULL, " +
            COLUMN_CLIENTID + " INTEGER NOT NULL " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;
}