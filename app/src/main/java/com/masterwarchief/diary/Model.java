package com.masterwarchief.diary;

public class Model {
    String title,note, imp, date, time;

    public Model() {
    }

    public Model(String title, String note, String imp, String date, String time) {
        this.title = title;
        this.note = note;
        this.imp=imp;
        this.date=date;
        this.time=time;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getImp() {
        return imp;
    }

    public void setImp(String imp) {
        this.imp = imp;
    }

    public String getdate() {
        return date;
    }

    public String gettime() {
        return time;
    }

    public void setdate(String date) {
        this.date = date;
    }

    public void settime(String time) {
        this.time = time;
    }
}