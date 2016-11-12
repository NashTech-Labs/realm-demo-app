package com.example.knoldus.realmdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.knoldus.realmdemo.model.MyBook;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public Realm mrealm;
    public EditText mEditText;
    public Button addBtn;
    public Button removeBtn;
    public TextView textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mrealm = Realm.getInstance(getBaseContext());
        addBtn = (Button) findViewById(R.id.button1);
        removeBtn = (Button) findViewById(R.id.button2);
        mEditText = (EditText) findViewById(R.id.editText);
        textview = (TextView) findViewById(R.id.textView);
        getAllBooks();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText = (EditText) findViewById(R.id.editText);
                mrealm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm){
                        MyBook book = new MyBook();
                        book.setTitle(mEditText.getText().toString().trim().toUpperCase());
                        realm.copyToRealm(book);
                    }
                });
                getAllBooks();
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText = (EditText) findViewById(R.id.editText);
                final String title = mEditText.getText().toString().trim().toUpperCase();

                mrealm.executeTransaction(new Realm.Transaction(){
                    @Override
                    public void execute(Realm realm){
                        RealmResults<MyBook> books = mrealm.where(MyBook.class).equalTo("title", title).findAll();
                        if(!books.isEmpty()) {
                            for(int i = books.size()-1; i >= 0; i--) {
                                books.get(i).removeFromRealm();
                            }
                        }
                    }
                });
                getAllBooks();
            }
        });
    }

    public void getAllBooks(){
        RealmResults<MyBook> mbooks;
        textview.setText("");
        String titles = "";
        mrealm.beginTransaction();
        mbooks = mrealm.allObjects(MyBook.class);
        mrealm.commitTransaction();
        for(int i=0;i<mbooks.size(); i ++){
            titles +=mbooks.get(i).getTitle()+"\n";
        }
        textview.setText(titles);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mrealm.close();
    }

}
