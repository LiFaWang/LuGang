package lugang.app.huansi.net.util;

import android.content.Context;

import lugang.app.huansi.net.greendao.DaoMaster;
import lugang.app.huansi.net.greendao.DaoSession;

import static lugang.app.huansi.net.lugang.constant.Constant.LG_DB_NAME;

/**
 * Created by 年 on 2017/11/1.
 */

public class GreenDaoUtil {

    /**
     * 获得 greenDao的session
     * @param context
     * @return
     */
    public static DaoSession getGreenDaoSession(Context context){
        DaoMaster.DevOpenHelper devOpenHelper = new DaoMaster.DevOpenHelper(context, LG_DB_NAME, null);
        DaoMaster daoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        return daoSession;
    }
}
