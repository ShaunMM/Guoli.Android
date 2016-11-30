package DBUtils;

import android.content.Context;

import zj.com.mc.R;


/**
 * Created by dell on 2016/7/29.
 */
public class DBOpenHelper extends DataBaseHelper {



    private static DBOpenHelper dbOpenHelper;
    public DBOpenHelper(Context context) {
        super(context);
    }

    public static DBOpenHelper getInstance(Context context){
        if (dbOpenHelper ==null){
            synchronized (DataBaseHelper.class){
                if (dbOpenHelper ==null){
                    dbOpenHelper = new DBOpenHelper(context);
                    if (dbOpenHelper.getDB()==null||!dbOpenHelper.getDB().isOpen()){
                        dbOpenHelper.open();
                    }
                }
            }
        }
        return dbOpenHelper;
    }

    @Override
    public int getMDbVersion(Context context) {

        return context.getResources().getInteger(R.integer.DATABASE_VERSION);

    }

    @Override
    public String getDbName(Context context) {
        return context.getResources().getStringArray(R.array.DATABASE_INFO)[0];
    }

    @Override
    public String[] getDbCreateSql(Context context) {

        return context.getResources().getStringArray(R.array.CREATE_TABLE_SQL);
    }

    @Override
    public String[] getDbUpdateSql(Context context) {
        return context.getResources().getStringArray(R.array.UPDATE_TABLE_SQL);

    }
}