package com.example.projekt5;

import android.content.ClipData;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {      //klasa implementująca loader

    private SimpleCursorAdapter mAdapterKursora;        //adapter do wyświetlania bazy danych z postaci Cursora
    private ListView mLista;                            //lista  wyświetlająca bazy danych telefonów
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLista = (ListView)findViewById(R.id.list);

        uruchomLoader();                                //funkcja odpowiedzialna za loader

        mLista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {   //słuchacz dla każdego elementu listy - szybkie kliknnięcie
                obslugaEdycji(id);                                      //funkcja obsługująca szybkie kliknięcie
            }
        });

        mLista.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);      //tryb wielokrotnego wybierania elementów listy
        mLista.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }
            @Override
            public void onDestroyActionMode(ActionMode mode) { }
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) { //po długim przytrzymaniu któregoś elementu
                MenuInflater pompka = mode.getMenuInflater();               //uruchomi się menu kontekstowe
                pompka.inflate(R.menu.menu_kontekstowe, menu);
                return true;
            }
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {    //obsługa kliknięcia przycisków menu kontekstowego
                switch (item.getItemId()) {                                         //w tym przypadku tylko jeden przycisk
                    case R.id.akcyjka:
                        kasujZaznaczone();                                          //funkcja kasująca zaznaczone elementy listy z bazy danych
                        return true;
                }
                return false;
            }
            @Override
            public void onItemCheckedStateChanged(ActionMode mode,int position, long id,
                                                  boolean checked) { }

        });
    }



    private void kasujZaznaczone() {
        long zaznaczone[] = mLista.getCheckedItemIds();         //pobranie do tablicy id zaznaczonych elementów listy
        for (int i = 0; i < zaznaczone.length; ++i) {
            getContentResolver().delete(                        //za pomocą dostawcy treści usunięcie wybranych elementów
                    ContentUris.withAppendedId(MojDiler.URI_ZAWARTOSCI,     //Uri dostawcy
                            zaznaczone[i]), null, null);    //kolejny zaznaczony element
        }
    }
    private void uruchomLoader() {
        android.support.v4.app.LoaderManager.getInstance(this).initLoader(0, null, this).forceLoad();
        //identyfikator loadera ^ od jakiegoś czasu loaderów już się nie używa
        // utworzenie mapowania między kolumnami tabeli a kolumnami wyświetlanej listy
        String[] mapujZ = new String[]{  PomocnikBD.KOLUMNA1,PomocnikBD.KOLUMNA2,PomocnikBD.KOLUMNA3}; //nazwy „kolumn” dostawcy
        int[] mapujDo = new int[]{R.id.etykieta4, R.id.etykieta1,R.id.etykieta2}; //R.id... - identyfikatory komponentów
        // adapter wymaga aby wyniku zapytania znajdowała się kolumna _id
        mAdapterKursora = new SimpleCursorAdapter(this,                     //utworzenie adaptera
                R.layout.wiersz_listy, null, mapujZ, mapujDo, 0);
        mLista.setAdapter(mAdapterKursora);                                         //ustawienie adaptera
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // adapter wymaga aby wyniku zapytania znajdowała się kolumna _id
        String[] projekcja = { PomocnikBD.ID,PomocnikBD.KOLUMNA1,PomocnikBD.KOLUMNA2,PomocnikBD.KOLUMNA3}; // inne „kolumny” do wyświetlenia
        CursorLoader loaderKursora = new CursorLoader(MainActivity.this,
                MojDiler.URI_ZAWARTOSCI, projekcja, null,null, null);
        return loaderKursora;
    }
    @Override
    public void onLoadFinished(Loader<Cursor> arg0, Cursor dane) {
        switch(arg0.getId()){
            case 0:
                mAdapterKursora.swapCursor(dane);
                break;
        }

    }
    @Override
    public void onLoaderReset(Loader<Cursor> arg0) {
        mAdapterKursora.swapCursor(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {  //funkcja dołączająca menu podstawowe
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   //funckja identyfikująca przycisk naciśnięty na menu barze
        long id = item.getItemId();                         //pobranie id wybranego przycisku - tutaj jeden przycisk do dodawania
        long ids = -1;
        if (id == R.id.akcja_plus) {    //pierwsza akcja
            Intent intencja = new Intent(this, EditActivity.class);             //stworzenie nowej aktywności EditActivity.class
            intencja.putExtra("id",ids);                           //tu przekazuje -1 ponieważ będziemy dodawać nowy rekord do bazy
            startActivity(intencja);                        //start aktywności
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void obslugaEdycji(long id){
        Intent intencja = new Intent(this, EditActivity.class);             //stworzenie nowej aktywności EditActivity.class
        intencja.putExtra("id",id);                           //tu przekazuje id które chcę edytować
        startActivity(intencja);                        //start aktywności
    }

}