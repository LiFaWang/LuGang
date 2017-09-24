//package lugang.app.huansi.net.lugang.manager;
//
//import android.content.Context;
//import android.database.sqlite.SQLiteDatabase;
//
//import org.greenrobot.greendao.query.QueryBuilder;
//
//import java.util.List;
//
//import lugang.app.huansi.net.db.MeasureCustomBeanDb;
//import lugang.app.huansi.net.db.RemarkDetailBeanDB;
//
///**
// * Created by Tony on 2017/8/3.
// * 15:25
// */
//
//public class DBManager {
//    private final static String dbName = "remarkDetail_db";
//    private static DBManager mInstance;
//    private DaoMaster.DevOpenHelper openHelper;
//    private Context context;
//
//    public DBManager(Context context) {
//        this.context = context;
//        openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//    }
//
//    /**
//     * 获取单例引用
//     *
//     * @param context
//     * @return
//     */
//    public static DBManager getInstance(Context context) {
//        if (mInstance == null) {
//            synchronized (DBManager.class) {
//                if (mInstance == null) {
//                    mInstance = new DBManager(context);
//                }
//            }
//        }
//        return mInstance;
//    }
//    /**
//     * 获取可读数据库
//     */
//    private SQLiteDatabase getReadableDatabase() {
//        if (openHelper == null) {
//            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        }
//        SQLiteDatabase db = openHelper.getReadableDatabase();
//        return db;
//    }
//    /**
//     * 获取可写数据库
//     */
//    private SQLiteDatabase getWritableDatabase() {
//        if (openHelper == null) {
//            openHelper = new DaoMaster.DevOpenHelper(context, dbName, null);
//        }
//        SQLiteDatabase db = openHelper.getWritableDatabase();
//        return db;
//    }
//    /**
//     * 插入一条记录
//     *
//     * @param remarkDetailFromDB
//     */
//    public void insertRemarkDetail(RemarkDetailBeanDB remarkDetailFromDB) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        RemarkDetailBeanDBDao remarkDetailDao = daoSession.getRemarkDetailBeanDBDao();
//        remarkDetailDao.insert(remarkDetailFromDB);
//    }
//
//    /**
//     * 插入用户集合
//     *
//     * @param remarkDetailFromDBs
//     */
//    public void insertRemarkDetailList(List<RemarkDetailBeanDB> remarkDetailFromDBs) {
//        if (remarkDetailFromDBs == null || remarkDetailFromDBs.isEmpty()) {
//            return;
//        }
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        RemarkDetailBeanDBDao remarkDetailDao = daoSession.getRemarkDetailBeanDBDao();
//        remarkDetailDao.insertInTx(remarkDetailFromDBs);
//    }
//    /**
//     * 删除一条记录
//     *
//     * @param remarkDetailFromDB
//     */
//    public void deleteRemarkDetail(RemarkDetailBeanDB remarkDetailFromDB) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        RemarkDetailBeanDBDao remarkDetailDao = daoSession.getRemarkDetailBeanDBDao();
//        remarkDetailDao.deleteByKey(remarkDetailFromDB.getId());
//    }
//    /**
//     * 更新一条记录
//     *
//     * @param remarkDetailFromDB
//     */
//    public void updateRemarkDetail(RemarkDetailBeanDB remarkDetailFromDB) {
//        DaoMaster daoMaster = new DaoMaster(getWritableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        RemarkDetailBeanDBDao remarkDetailDao = daoSession.getRemarkDetailBeanDBDao();
//        remarkDetailDao.delete(remarkDetailFromDB);
//    }
//    /**
//     * 查询备注详情列表
//     */
//    public List<RemarkDetailBeanDB> queryRemarkDetailList() {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        RemarkDetailBeanDBDao remarkDetailDao = daoSession.getRemarkDetailBeanDBDao();
//        QueryBuilder<RemarkDetailBeanDB> qb = remarkDetailDao.queryBuilder();
//        return qb.list();
//    }
//
//    /**
//     * 查询备注详情列表
//     */
//    public List<RemarkDetailBeanDB> queryRemarkDetailList(int id,int iid) {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        RemarkDetailBeanDBDao remarkDetailDao = daoSession.getRemarkDetailBeanDBDao();
//        QueryBuilder<RemarkDetailBeanDB> qb = remarkDetailDao.queryBuilder();
//        qb.where(RemarkDetailBeanDBDao.Properties.Isdstyletypemstid.eq(id),RemarkDetailBeanDBDao.Properties.CoustomOrderIid.eq(iid));
//        List<RemarkDetailBeanDB> list = qb.list();
//        return list;
//    }
//    /**
//     * 查询测量列表
//     */
//    public List<MeasureCustomBeanDb> queryMeasureCustomList(int id, int iid) {
//        DaoMaster daoMaster = new DaoMaster(getReadableDatabase());
//        DaoSession daoSession = daoMaster.newSession();
//        MeasureCustomBeanDbDao measureCustomBeanDbDao = daoSession.getMeasureCustomBeanDbDao();
//        QueryBuilder<MeasureCustomBeanDb> qb = measureCustomBeanDbDao.queryBuilder();
//        qb.where(MeasureCustomBeanDbDao.Properties.Isdstyletypemstid.eq(id),MeasureCustomBeanDbDao.Properties.CoustomOrderIid.eq(iid));
//        List<MeasureCustomBeanDb> list = qb.list();
//        return list;
//    }
//
//}
