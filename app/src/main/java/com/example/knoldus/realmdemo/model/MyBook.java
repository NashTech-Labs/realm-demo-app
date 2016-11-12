package com.example.knoldus.realmdemo.model;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.Required;

/**
 * Created by knoldus on 8/11/16.
 */
public class MyBook extends RealmObject {

    @Required
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
