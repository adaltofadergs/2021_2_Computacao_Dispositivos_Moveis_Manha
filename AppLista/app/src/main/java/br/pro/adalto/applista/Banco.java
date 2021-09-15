package br.pro.adalto.applista;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Banco extends SQLiteOpenHelper {

    private static final String NOME_BANCO = "AppLista";
    private static final int VERSAO = 2;

    public Banco(Context context){
        super(context, NOME_BANCO, null, VERSAO);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(     "CREATE TABLE IF NOT EXISTS produtos ( " +
                        " id INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT , " +
                        " nome TEXT  NOT NULL , " +
                        " categoria TEXT );  "
                    );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
