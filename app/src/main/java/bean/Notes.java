package bean;

/**
 * 记事本基类
 */
public class Notes {

    private String title;//标题
    private String content;//内容
    private String times;//时间
    private int ids;//编号
    private String currentSite;//当前站点名

    //查询Note
    public Notes(int id, String currentSite, String ti, String con, String time) {
        this.ids = id;
        this.currentSite = currentSite;
        this.title = ti;
        this.content = con;
        this.times = time;
    }

    //创建Note
    public Notes(String currentSite, String ti, String con, String time) {
        this.currentSite = currentSite;
        this.title = ti;
        this.content = con;
        this.times = time;
    }

    //修改Note
    public Notes(int i, String ti, String content, String time) {
        this.ids = i;
        this.content = content;
        this.title = ti;
        this.times = time;
    }

    public Notes(String currentSite, String ti, String con) {
        this.currentSite = currentSite;
        this.title = ti;
        this.content = con;
    }

    public int getIds() {
        return ids;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getTimes() {
        return times;
    }

    public String getCurrentSite() {
        return currentSite;
    }

}
