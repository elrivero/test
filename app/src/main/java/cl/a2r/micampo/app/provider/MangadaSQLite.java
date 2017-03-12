package cl.a2r.micampo.app.provider;

/**
 * Created by fmartin on 16-08-2016.
 */
public class MangadaSQLite {

    //TABLE ------------------------------------
    public static final String TABLE = "mangada";

    //COLUMNS -----------------------------------------
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_NUMERO = "numero";
    public static final String COLUMN_PREDIOID = "predioid";
    public static final String COLUMN_ACTIVIDADID = "actividadid";
    public static final String COLUMN_FECHA = "fecha";
    public static final String COLUMN_ESTADOID = "estado";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_NUMERO + " INTEGER NOT NULL, " +
            COLUMN_PREDIOID + " INTEGER NOT NULL, " +
            COLUMN_ACTIVIDADID + " INTEGER NOT NULL, " +
            COLUMN_FECHA + " INTEGER NOT NULL, " +
            COLUMN_ESTADOID + " INTEGER NOT NULL " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;
}