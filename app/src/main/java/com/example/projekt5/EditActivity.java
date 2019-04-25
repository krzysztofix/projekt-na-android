package com.example.projekt5;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;

public class EditActivity extends AppCompatActivity {

    EditText producent;
    EditText model;
    EditText wersja,strona;
    Button www,anuluj,zapisz;
    long id;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        producent = findViewById(R.id.editText);
        model = findViewById(R.id.editText2);
        wersja = findViewById(R.id.editText3);                      //przypisanie zmiennym odpowiednich id komponentów
        strona = findViewById(R.id.editText4);
        www = findViewById(R.id.button);
        anuluj = findViewById(R.id.button2);
        zapisz = findViewById(R.id.button3);

        Bundle tobolek = getIntent().getExtras();                   //Pobranie wartości z aktywności wywołującej
        id = tobolek.getLong("id");
        if (id != -1) {                                         //jeżeli != -1 to edytujemy stworzony wiersz bazy
            obslugaUtworzonego(id);
        } else {                                            //w innym przypadku  tworzymy nowy obiekt
            obslugaNieUtworzonego();
        }
    }

    public void obslugaNieUtworzonego(){  //jeżeli tworzymy nowy wiersz w bazie
        producent.setText("");              //edittexty puste
        model.setText("");
        wersja.setText("");
        strona.setText("");
    }
    public void obslugaUtworzonego(long id){       //jeżeli edytujemy już istniejący rekord
        String [] arg={id +""};
        String[] kolumny= {PomocnikBD.KOLUMNA1,PomocnikBD.KOLUMNA2,PomocnikBD.KOLUMNA3,PomocnikBD.KOLUMNA4};
        //pobieramy z bazy danych wszystkie pola z wiersza o podanym id , typ cursor a kolejne pola pod kolejnym indexem
        Cursor odbior=getContentResolver().query(MojDiler.URI_ZAWARTOSCI,kolumny,PomocnikBD.ID+"=?",arg,null);
        odbior.moveToFirst();

        producent.setText(odbior.getString(0));     //ustawiamy w edittextach dane które są już w bazie
        model.setText(odbior.getString(1));
        wersja.setText(odbior.getString(2));
        strona.setText(odbior.getString(3));
    }
    public void onWww(View view){               //obsługa kliknięcia przycisku www
        String adres = strona.getText().toString();     //pobranie adresu z edittextu
        if(adres.startsWith("http://")) {           //sprawdzenie czy zaczyna się na http
            Intent zamiarPrzegladarki = new Intent("android.intent.action.VIEW", Uri.parse(adres));
            startActivity(zamiarPrzegladarki);  //wywołanie akcji, która jest w postaci otworzenia przeglądarki
        }
    }
    public void onAnuluj(View view){
        finish();
    }  //obsługa przycisku anuluj,  zakonczenie aktywności = powrót do aktywności wywołujacych
    public void onZapisz(View view){        //obsługa przycisku zapisz
        ArrayList<EditText> editexty = new ArrayList<EditText>();   //araylista
        editexty.add(producent);            //dodajemy do niej dane z  edittextów
        editexty.add(model);
        editexty.add(wersja);
        editexty.add(strona);

        if((sprawdz(editexty))& strona.getText().toString().startsWith("http://")) {         //funkcja sprawdzająca poprawnośc uzupełnionych edittextów
            onZapisz2(id);                //funkcja zapisująca dane
        }
        else {
            Toast tost = Toast.makeText(this, "Uzupełnij wszystkie pola !",Toast.LENGTH_LONG);
            tost.show();
        }
    }
    public void onZapisz2(long id){
        if(id!= -1) {          //jeżeli edytujemy
            ContentValues zbior = new ContentValues();         //nowy obiekt content values
            zbior.put(PomocnikBD.KOLUMNA1, producent.getText().toString());     //wpisanie wartości z edittextów
            zbior.put(PomocnikBD.KOLUMNA2, model.getText().toString());
            zbior.put(PomocnikBD.KOLUMNA3, wersja.getText().toString());
            zbior.put(PomocnikBD.KOLUMNA4, strona.getText().toString());
            String args[] = {id + ""};              //aktualizacja konkretnego id
            getContentResolver().update(MojDiler.URI_ZAWARTOSCI, zbior, PomocnikBD.ID + "=?", args);
            ////aktualizacja konkretnego id
            Toast tost = Toast.makeText(this,"Zmieniłeś dane telefonu ! :)",Toast.LENGTH_LONG);
            tost.show();
            finish();
        }
        else{
            ContentValues zbior = new ContentValues();                  //nowy obiekt content values
            zbior.put(PomocnikBD.KOLUMNA1, producent.getText().toString()); //wpisanie wartości z edittextów
            zbior.put(PomocnikBD.KOLUMNA2, model.getText().toString());
            zbior.put(PomocnikBD.KOLUMNA3, wersja.getText().toString());
            zbior.put(PomocnikBD.KOLUMNA4, www.getText().toString());
            getContentResolver().insert(MojDiler.URI_ZAWARTOSCI, zbior);  //wstawienie nowego rekordu do bazy danych
            Toast tost = Toast.makeText(this,"Dodałeś nowy telefon ! :)",Toast.LENGTH_LONG);
            tost.show();
            finish();
        }
    }
    public boolean sprawdz(ArrayList<EditText> editexty){   //funkcja sprawdzająca czy edittexty nie są puste
        for (EditText l : editexty){
            if(l.getText().toString().isEmpty()){
                return false;
            }
        }
        return true;
    }
}
