package com.jediupc.helloandroid.model;

import java.util.ArrayList;

public class TodoListModel {

    public String title;
    public ArrayList<TodoListItemModel> items = new ArrayList<>();

    public static class TodoListItemModel {
        public String text;
        public boolean checked;
    }

}
