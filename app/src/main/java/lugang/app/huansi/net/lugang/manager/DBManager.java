package lugang.app.huansi.net.lugang.manager;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import lugang.app.huansi.net.greendao.db.DaoMaster;
import lugang.app.huansi.net.greendao.db.DaoSession;
import lugang.app.huansi.net.greendao.db.RemarkDetail;
import lugang.app.huansi.net.greendao.db.RemarkDetailDao;

/**
 * Created by Tony on 2017/8/3.
 * 15:25
 */

public class DBManager {
    private final static String dbName = "remarkDetail_db";
    private static DBManager mInstance;
    private DaoMaster.DevOpenHelper openHelper;
    private Context context;

    public DBManager(Context context) {
        this.context = context;
        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
    }

    /**
     * 获取单例引用
     *
     * @param context
     * @return
     */
    public static DBManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (DBManager.class) {
                if (mInstance == null) {
                    mInstance = new DBManager(context);
                }
            }
        }
        return mInstance;
    }
    /**
     * 获取可读数据库
     */
    private SQLiteDatabase getReadableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getReadableDatabase();
        return db;
    }
    /**
     * 获取可写数据库
     */
    private SQLiteDatabase getWritableDatabase() {
        if (openHelper == null) {
            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
        }
        SQLiteDatabase db = openHelper.getWritableDatabase();
        return db;
    }
    /**
     * 插入一条记录
     *
     * @param remarkDetail
     */
    public void insertRemarkDetail(RemarkDetail remarkDetail) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        RemarkDetailDao remarkDetailDao = daoSession.getRemarkDetailDao();
        remarkDetailDao.insert(remarkDetail);
    }

    /**
     * 插入用户集合
     *
     * @param remarkDetails
     */
    public void insertRemarkDetailList(List<RemarkDetail> remarkDetails) {
        if (remarkDetails == null || remarkDetails.isEmpty()) {
            return;
        }
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        RemarkDetailDao remarkDetailDao = daoSession.getRemarkDetailDao();
        remarkDetailDao.insertInTx(remarkDetails);
    }
    /**
     * 删除一条记录
     *
     * @param remarkDetail
     */
    public void deleteRemarkDetail(RemarkDetail remarkDetail) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        RemarkDetailDao remarkDetailDao = daoSession.getRemarkDetailDao();
        remarkDetailDao.delete(remarkDetail);
    }
    /**
     * 更新一条记录
     *
     * @param remarkDetail
     */
    public void updateRemarkDetail(RemarkDetail remarkDetail) {
        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        RemarkDetailDao remarkDetailDao = daoSession.getRemarkDetailDao();
        remarkDetailDao.delete(remarkDetail);
    }
    /**
     * 查询用户列表
     */
    public List<RemarkDetail> queryRemarkDetailList() {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        RemarkDetailDao remarkDetailDao = daoSession.getRemarkDetailDao();
        QueryBuilder<RemarkDetail> qb = remarkDetailDao.queryBuilder();
        List<RemarkDetail> list = qb.list();
        return list;
    }

    /**
     * 查询用户列表
     */
    public List<RemarkDetail> queryRemarkDetailList(int age) {
        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
        DaoSession daoSession = daoMaster.newSession();
        RemarkDetailDao remarkDetailDao = daoSession.getRemarkDetailDao();
        QueryBuilder<RemarkDetail> qb = remarkDetailDao.queryBuilder();
//        qb.where(RemarkDetail.Properties.Name.gt(name)).orderAsc(RemarkDetail.Properties.Name);
        List<RemarkDetail> list = qb.list();
        return list;
    }

}
