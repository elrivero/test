package cl.a2r.micampo.app.provider;

/**
 * Created by fmartin on 16-08-2016.
 */
public class AppSQLite {

    //TABLE ---------------------------------
    public static final String TABLE = "app";
    public static final String QUERY = "query";

    //COLUMNS ----------------------------------
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USERID = "userid";
    public static final String COLUMN_USEREMAIL = "useremail";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_USERDATE = "userdate";
    public static final String COLUMN_PREDIOID = "predioid";
    public static final String COLUMN_MANGADA = "mangada";

    public static final String CREATE =
        "CREATE TABLE " + TABLE + "(" +
            COLUMN_ID + " INTEGER NOT NULL, " +
            COLUMN_USERID + " INTEGER NOT NULL, " +
            COLUMN_USEREMAIL + " TEXT NOT NULL, " +
            COLUMN_USERNAME + " TEXT NOT NULL, " +
            COLUMN_USERDATE + " INTEGER NOT NULL, " +
            COLUMN_PREDIOID + " INTEGER NOT NULL, " +
            COLUMN_MANGADA + " INTEGER NOT NULL " +
        ");";

    public static final String DROP =
        "DROP TABLE IF EXISTS " + TABLE;

}