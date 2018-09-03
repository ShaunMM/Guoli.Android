package zj.com.mc;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import bean.Notes;


public class MyDataBase {
    Context context;
    MyOpenHelper myHelper;
    SQLiteDatabase myDatabase;

    public MyDataBase(Context con) {
        this.context = con;
        myHelper = new MyOpenHelper(context);
    }

    public ArrayList<Notes> getArray() {
        ArrayList<Notes> array = new ArrayList<Notes>();
        ArrayList<Notes> array1 = new ArrayList<Notes>();
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor = myDatabase.rawQuery("select ids,currentSite,title,content,times from mybook", null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            int id = cursor.getInt(cursor.getColumnIndex("ids"));
            String currentSite = cursor.getString(cursor.getColumnIndex("currentSite"));
            String title = cursor.getString(cursor.getColumnIndex("title"));
            String content = cursor.getString(cursor.getColumnIndex("content"));
            String times = cursor.getString(cursor.getColumnIndex("times"));
            Notes note = new Notes(id, currentSite, title, content, times);
            array.add(note);
            cursor.moveToNext();
        }
        myDatabase.close();
        for (int i = array.size(); i > 0; i--) {
            array1.add(array.get(i - 1));
        }
        return array1;
    }


    public Notes getTiandCon(int id) {
        myDatabase = myHelper.getWritableDatabase();
        Cursor cursor = myDatabase.rawQuery("select currentSite,title,content from mybook where ids='" + id + "'", null);
        cursor.moveToFirst();
        String currentSite = cursor.getString(cursor.getColumnIndex("currentSite"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String content = cursor.getString(cursor.getColumnIndex("content"));
        Notes note = new Notes(currentSite, title, content);
        myDatabase.close();
        return note;
    }

    public void toUpdate(Notes note) {
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("update mybook set title='" + note.getTitle() + "',times='" + note.getTimes() + "',content='" + note.getContent() + "' where ids='" + note.getIds() + "'");
        myDatabase.close();
    }

    public void toInsert(Notes note) {
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("insert into mybook(currentSite,title,content,times)values('" + note.getCurrentSite() + "','" + note.getTitle() + "','" + note.getContent() + "','" + note.getTimes() + "')");
        myDatabase.close();
    }

    public void toDelete(int ids) {
        myDatabase = myHelper.getWritableDatabase();
        myDatabase.execSQL("delete  from mybook where ids=" + ids + "");
        myDatabase.close();
    }
}
