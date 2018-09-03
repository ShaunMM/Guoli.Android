package config;

import android.content.Context;

/**
 * Created by dell on 2017/3/23.
 */
public class SystemConfigFactory {
    private Context context;

    public static SystemConfigFactory getInstance(Context ctx) {
        return new SystemConfigFactory(ctx);
    }

    private SystemConfigFactory(Context ctx) {
        this.context = ctx;
    }

    public ISystemConfig getSystemConfig() {
        return new SystemConfig(this.context);
    }
}
