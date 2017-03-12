package cl.a2r.micampo.postparto.provider;

import cl.a2r.sip.model.MedicamentoControl;

/**
 * Created by fmartin on 16-08-2016.
 */
public class MedControlSQLite {

    //TABLE
    public static final String TABLE = "medicamentocontrol";

    //COLUMNS
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NOMBRE = "nombre";
    public static final String COLUMN_SERIE = "serie";
    public static final String COLUMN_LOTE = "lote";
    public static final String COLUMN_CANTIDAD = "cantidad";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER PRIMARY KEY, " +
            COLUMN_NOMBRE + " TEXT NOT NULL, " +
            COLUMN_SERIE + " INTEGER, " +
            COLUMN_LOTE + " INTEGER, " +
            COLUMN_CANTIDAD + " INTEGER " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;
}