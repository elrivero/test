package cl.a2r.micampo.postparto.provider;

/**
 * Created by fmartin on 16-08-2016.
 */
public class PostpartoSQLite {

    //TABLE
    public static final String TABLE = "postparto";
    public static final String QUERY = "query";

    //COLUMNS
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SINCRONIZADO = "sincronizado";
    public static final String COLUMN_MANGADA = "mangada";

    public static final String COLUMN_FUNDOID = "fundoid";
    public static final String COLUMN_GANADOID = "ganadoid";
    public static final String COLUMN_DIIO = "diio";
    public static final String COLUMN_EID = "eid";

    public static final String COLUMN_CANDIDATO = "candidato";
    public static final String COLUMN_FECHAPARTO = "fechaparto";
    public static final String COLUMN_TIPOPARTOID = "tipopartoid";
    public static final String COLUMN_CODREVISION = "codrevision";
    public static final String COLUMN_DIAGNOSTICOID = "diagnosticoid";
    public static final String COLUMN_DIAGNOSTICO = "diagnostico";
    public static final String COLUMN_MEDCONTROLID = "medcontrolid";
    public static final String COLUMN_MEDICAMENTO = "medicamento";
    public static final String COLUMN_FECHACONTROL = "fechacontrol";
    public static final String COLUMN_MENSAJE = "mensaje";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COLUMN_SINCRONIZADO + " INTEGER NOT NULL, " +
            COLUMN_MANGADA + " INTEGER NOT NULL, " +

            COLUMN_FUNDOID + " INTEGER NOT NULL, " +
            COLUMN_GANADOID + " INTEGER NOT NULL, " +
            COLUMN_EID + " TEXT, " +
            COLUMN_DIIO + " INTEGER NOT NULL, " +

            COLUMN_CANDIDATO + " INTEGER NOT NULL, " +
            COLUMN_FECHAPARTO + " INTEGER NOT NULL, " +
            COLUMN_TIPOPARTOID + " INTEGER NOT NULL, " +
            COLUMN_CODREVISION + " INTEGER NOT NULL, " +
            COLUMN_DIAGNOSTICOID + " INTEGER, " +
            COLUMN_DIAGNOSTICO + " TEXT, " +
            COLUMN_MEDCONTROLID + " INTEGER, " +
            COLUMN_MEDICAMENTO + " TEXT, " +
            COLUMN_FECHACONTROL + " INTEGER, " +
            COLUMN_MENSAJE + " TEXT " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;
}