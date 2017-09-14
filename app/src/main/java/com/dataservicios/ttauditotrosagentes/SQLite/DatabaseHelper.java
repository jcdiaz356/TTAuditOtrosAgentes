package com.dataservicios.ttauditotrosagentes.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.dataservicios.ttauditotrosagentes.model.Encuesta;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by usuario on 12/02/2015.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
    // Database Version
    private static final int DATABASE_VERSION = 31;
    // Database Name
    private static final String DATABASE_NAME = "db_bcp";
    // Table Names
    protected static final String TABLE_ENCUESTA = "encuesta";
    protected static final String TABLE_USER = "user";
    protected static final String TABLE_MEDIAS = "medias";

    //Nombre de columnas en comun
    protected static final String KEY_ID = "id";
    protected static final String KEY_QUESTION = "nombre";
    protected static final String KEY_ID_AUDITORIA = "idAuditoria";
    protected static final String KEY_NOMBRE = "nombre";
    protected static final String KEY_PASSWORD = "password";


    //Name columns user
    protected static final String KEY_EMAIL = "email";


    protected static final String KEY_NAME = "name";
    protected static final String KEY_STORE = "store_id";
    protected static final String KEY_POLL_ID = "poll_id";
    protected static final String KEY_DATE_CREATED= "created_at";
    protected static final String KEY_DATE_UPDATE= "update_at";

    //Name column Table medias
    protected   static final String KEY_TIPO = "tipo";
    protected   static final String KEY_NAME_FILE = "archivo";
    protected   static final String KEY_TYPE = "type";

    //Name column Presense Publicity
    protected static final String KEY_PUBLICITY_ID = "publicity_id";

    //Name columns Products
    protected static final String KEY_COMPANY_ID = "company_id";

    //Name columns SODVentanas

    //Name column Presense Product
    protected static final String KEY_STORE_ID = "store_id";
    protected static final String KEY_PRODUCT_ID = "product_id";

    // Table Create Statements
    // eNCUESTA table create statement
    private static final String CREATE_TABLE_ENCUESTA = "CREATE TABLE "
            + TABLE_ENCUESTA + "("
            + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_ID_AUDITORIA + " INTEGER,"
            + KEY_QUESTION  + " TEXT " + ")";

    // User table create statement
    // User table create statement
    private static final String CREATE_TABLE_USER = "CREATE TABLE "
            + TABLE_USER + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT,"
            + KEY_PASSWORD + " TEXT " + ")";

    private static final String CREATE_TABLE_MEDIAS  = "CREATE TABLE "
            + TABLE_MEDIAS + "("
            + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + KEY_STORE + " INTEGER ,"
            + KEY_POLL_ID + " INTEGER, "
            + KEY_PUBLICITY_ID + " INTEGER, "
            + KEY_PRODUCT_ID + " INTEGER, "
            + KEY_COMPANY_ID + " INTEGER, "
            + KEY_NAME_FILE + " TEXT, "
            + KEY_TYPE + " INTEGER, "
            + KEY_DATE_CREATED + " TEXT )";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null , DATABASE_VERSION);
    }

//
    @Override
    public void onCreate(SQLiteDatabase db) {
        // creating required tables
        db.execSQL(CREATE_TABLE_ENCUESTA);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_MEDIAS);

    }



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ENCUESTA);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEDIAS);

        // create new tables
        onCreate(db);
    }


    // ------------------------ "Pedido" table methods ----------------//

    /*
     * Creating a Encuesta
     */
    public long createEncuesta(Encuesta encuesta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(KEY_ID, encuesta.getId());
        values.put(KEY_ID_AUDITORIA, encuesta.getIdAuditoria());
        values.put(KEY_QUESTION, encuesta.getQuestion());

        // insert row
        //long todo_id = db.insert(TABLE_PEDIDO, null, values);
         db.insert(TABLE_ENCUESTA, null, values);

        long todo_id = encuesta.getId();
        return todo_id;
    }



    /*
     * get single Encuesta
     */
    public Encuesta getEncuesta(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENCUESTA + " WHERE "
                + KEY_ID + " = " + id;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Encuesta pd = new Encuesta();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setIdAiditoria(c.getInt(c.getColumnIndex(KEY_ID_AUDITORIA)));
        pd.setQuestion((c.getString(c.getColumnIndex(KEY_QUESTION))));
        return pd;
    }

    /*
     * get single Encuesta por Auditoria
     */
    public Encuesta getEncuestaAuditoria(long idAuditoria) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ENCUESTA + " WHERE "
                + KEY_ID_AUDITORIA + " = " + idAuditoria;
        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);
        if (c != null)
            c.moveToFirst();
        Encuesta pd = new Encuesta();
        pd.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        pd.setIdAiditoria(c.getInt(c.getColumnIndex(KEY_ID_AUDITORIA)));
        pd.setQuestion((c.getString(c.getColumnIndex(KEY_QUESTION))));
        return pd;
    }

    /**
     * getting all Encuesta
     * */
    public List<Encuesta> getAllEncuesta() {
        List<Encuesta> encuesta = new ArrayList<Encuesta>();
        String selectQuery = "SELECT  * FROM " + TABLE_ENCUESTA;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Encuesta pd = new Encuesta();
                pd.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                pd.setIdAiditoria(c.getInt((c.getColumnIndex(KEY_ID_AUDITORIA))));
                pd.setQuestion((c.getString(c.getColumnIndex(KEY_QUESTION))));

                // adding to todo list
                encuesta.add(pd);
            } while (c.moveToNext());
        }
        return encuesta;
    }

    /*
     * getting Encuesta count
     */
    public int getEncuestaCount() {
        String countQuery = "SELECT  * FROM " + TABLE_ENCUESTA;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        // return count
        return count;
    }
    /*
     * Updating a Encuesta
     */
    public int updateEncuesta(Encuesta encuesta) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_QUESTION, encuesta.getQuestion());
        // updating row
        return db.update(TABLE_ENCUESTA, values, KEY_ID + " = ?",
                new String[] { String.valueOf(encuesta.getId()) });
    }

    /*
     * Deleting a Encuesta
     */
    public void deleteEncuesta(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENCUESTA, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /*
     * Deleting all Encuesta
     */
    public void deleteAllEncuesta() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ENCUESTA, null, null );

    }




}
