package cl.a2r.micampo.postparto.provider;

/**
 * Created by fmartin on 16-08-2016.
 */
public class DiagnosticoSQLite {

    //TABLE
    public static final String TABLE = "diagnostico";

    //COLUMNS
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_CODIGO = "codigo";
    public static final String COLUMN_NOMBRE = "nombre";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_CODIGO + " TEXT NOT NULL, " +
            COLUMN_NOMBRE + " TEXT NOT NULL " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;
}