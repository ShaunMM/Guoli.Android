package Utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mao on 2016/11/30.
 */

public class SpUtilis {
    public static final String WorkNo="WorkNo";
    public static final String PersonId="PersonId";
    private static SpUtilis spUtilis;
    private Context context;
    public SpUtilis(Context context){
        this.context=context;
    }
    public SpUtilis getInstance(Context context){

        if (spUtilis==null){
            spUtilis=new SpUtilis(context);
        }
        return  spUtilis;
    }

    public Map<String,String> getUserinfo(){
        SharedPreferences sharedPreferences=context.getSharedPreferences("PersonInfo",context.MODE_PRIVATE);
        String workNo=sharedPreferences.getString("WorkNo","");
        String personId=sharedPreferences.getString("PersonId","");
        Map<String,String> map=new HashMap<>();
        map.put("WorkNo",workNo);
        map.put("PersonId",personId);
        return  map;
    }
}
