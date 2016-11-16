package com.example.knoldus.realmdemo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.knoldus.realmdemo.model.MyBook;

import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    public Realm mrealm;
    public EditText mEditText;
    public Button addBtn;
    public Button removeBtn;
    public TextView textview;
    private String title = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RealmConfiguration config = new RealmConfiguration.Builder(getBaseContext()).deleteRealmIfMigrationNeeded().build();
        mrealm = Realm.getInstance(config);
        addBtn = (Button) findViewById(R.id.button1);
        removeBtn = (Button) findViewById(R.id.button2);
        mEditText = (EditText) findViewById(R.id.editText);
        textview = (TextView) findViewById(R.id.textView);
        getAllBooks();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText = (EditText) findViewById(R.id.editText);
                mrealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        title = mEditText.getText().toString().trim().toUpperCase();
                        if(!title.isEmpty()){
                        MyBook book = new MyBook();
                        book.setId(setUniqueId());
                        book.setTitle(title);
                        realm.copyToRealmOrUpdate(book);
                    } else {
                            Toast.makeText(getApplicationContext(), "Please enter a valid book name", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                getAllBooks();
                mEditText.setText("");
            }
        });

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mEditText = (EditText) findViewById(R.id.editText);

                mrealm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        title = mEditText.getText().toString().trim().toUpperCase();
                        if(!title.isEmpty()){
                        RealmResults<MyBook> books = mrealm.where(MyBook.class).equalTo("title", title).findAll();
                        if (!books.isEmpty()) {
                            for (int i = books.size() - 1; i >= 0; i--) {
                                books.get(i).removeFromRealm();
                            }
                        } else{
                            Toast.makeText(getApplicationContext(), "Please enter a valid book name", Toast.LENGTH_SHORT).show();
                        }
                        }
                    }
                });
                getAllBooks();
                mEditText.setText("");
            }
        });
    }

    public void getAllBooks() {
        RealmResults<MyBook> mbooks;
        textview.setText("");
        String titles = "";
        mrealm.beginTransaction();
        mbooks = mrealm.allObjects(MyBook.class);
        mrealm.commitTransaction();
        for (int i = 0; i < mbooks.size(); i++) {
            titles += mbooks.get(i).getTitle() + "\n";
        }
        textview.setText(titles);
    }

    public long setUniqueId() {
        Number num = mrealm.where(MyBook.class).max("id");
        if (num == null) return 1;
        else return ((long) num + 1);
    }

    public void updateBook(View v) {
      final String oldTitle = mEditText.getText().toString().trim().toUpperCase();
        if (!oldTitle.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Update Book\n\n");
            builder.setMessage("Enter new book name");
            final EditText input = new EditText(this);
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String newTitle = input.getText().toString().toUpperCase();
                    mrealm.beginTransaction();
                    RealmResults<MyBook> books = mrealm.where(MyBook.class).equalTo("title", oldTitle).findAll();
                    if (!books.isEmpty()) {
                        for (int i = 0; i < books.size(); i++) {
                            MyBook bk = new MyBook();

                            bk.setId(books.get(i).getId());
                            bk.setTitle(newTitle);
                            mrealm.copyToRealmOrUpdate(bk);
                        }
                        mrealm.commitTransaction();
                        getAllBooks();
                        mEditText.setText("");
                    } else {
                        Toast.makeText(getApplicationContext(), "Record not found !!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            RealmResults<MyBook> books = mrealm.where(MyBook.class).equalTo("title", oldTitle).findAll();
            if (!books.isEmpty()) {
                builder.show();
            } else {
                Toast.makeText(getApplicationContext(), "Book not found !!", Toast.LENGTH_SHORT).show();
                mEditText.setText("");
            }
// Set up the buttons

        } else {
            Toast.makeText(getApplicationContext(), "Please enter a valid book name", Toast.LENGTH_SHORT).show();
            mEditText.setText("");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mrealm.close();
    }


}
