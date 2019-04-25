package com.example.projekt5;

        import android.content.Context;
        import android.database.Cursor;
        import android.database.sqlite.SQLiteDatabase;
        import android.database.sqlite.SQLiteOpenHelper;

public class PomocnikBD extends SQLiteOpenHelper {   //klasa pomocnicza do połączenia z bazą danych

    private Context mKontekst;
    public final static int WERSJA_BAZY = 1;
    public final static String ID = "_id";
    public final static String NAZWA_BAZY = "projekt5";
    public final static String NAZWA_TABELI = "telefony";
    public final static String KOLUMNA1 = "producent";
    public final static String KOLUMNA2 = "model";
    public final static String KOLUMNA4 = "link";
    public final static String KOLUMNA3 = "wersja";
    public final static String TW_BAZY = "CREATE TABLE " + NAZWA_TABELI +
            "("+ID+" integer primary key autoincrement, " +
            KOLUMNA1+" text not null,"+
            KOLUMNA2+" text not null,"+
            KOLUMNA3+" text not null, "+
            KOLUMNA4+" text not null "+
            ");";
    private static final String KAS_BAZY = "DROP TABLE IF EXISTS "+NAZWA_TABELI;

    public PomocnikBD(Context context){
        super(context,NAZWA_BAZY,null, WERSJA_BAZY);
        mKontekst=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TW_BAZY);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TW_BAZY);
        onCreate(db);

    }
    public void drop(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL(KAS_BAZY);
        db.close();
    }
}