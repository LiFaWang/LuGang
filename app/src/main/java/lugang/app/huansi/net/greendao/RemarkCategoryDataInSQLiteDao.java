package lugang.app.huansi.net.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import lugang.app.huansi.net.db.RemarkCategoryDataInSQLite;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "REMARK_CATEGORY_DATA_IN_SQLITE".
*/
public class RemarkCategoryDataInSQLiteDao extends AbstractDao<RemarkCategoryDataInSQLite, Long> {

    public static final String TABLENAME = "REMARK_CATEGORY_DATA_IN_SQLITE";

    /**
     * Properties of entity RemarkCategoryDataInSQLite.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property StyleId = new Property(1, String.class, "styleId", false, "STYLE_ID");
        public final static Property IId = new Property(2, String.class, "iId", false, "I_ID");
        public final static Property SBillNo = new Property(3, String.class, "sBillNo", false, "S_BILL_NO");
        public final static Property SMeterMarkName = new Property(4, String.class, "sMeterMarkName", false, "S_METER_MARK_NAME");
    };


    public RemarkCategoryDataInSQLiteDao(DaoConfig config) {
        super(config);
    }
    
    public RemarkCategoryDataInSQLiteDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"REMARK_CATEGORY_DATA_IN_SQLITE\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"STYLE_ID\" TEXT," + // 1: styleId
                "\"I_ID\" TEXT," + // 2: iId
                "\"S_BILL_NO\" TEXT," + // 3: sBillNo
                "\"S_METER_MARK_NAME\" TEXT);"); // 4: sMeterMarkName
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"REMARK_CATEGORY_DATA_IN_SQLITE\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, RemarkCategoryDataInSQLite entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String styleId = entity.getStyleId();
        if (styleId != null) {
            stmt.bindString(2, styleId);
        }
 
        String iId = entity.getIId();
        if (iId != null) {
            stmt.bindString(3, iId);
        }
 
        String sBillNo = entity.getSBillNo();
        if (sBillNo != null) {
            stmt.bindString(4, sBillNo);
        }
 
        String sMeterMarkName = entity.getSMeterMarkName();
        if (sMeterMarkName != null) {
            stmt.bindString(5, sMeterMarkName);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, RemarkCategoryDataInSQLite entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String styleId = entity.getStyleId();
        if (styleId != null) {
            stmt.bindString(2, styleId);
        }
 
        String iId = entity.getIId();
        if (iId != null) {
            stmt.bindString(3, iId);
        }
 
        String sBillNo = entity.getSBillNo();
        if (sBillNo != null) {
            stmt.bindString(4, sBillNo);
        }
 
        String sMeterMarkName = entity.getSMeterMarkName();
        if (sMeterMarkName != null) {
            stmt.bindString(5, sMeterMarkName);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public RemarkCategoryDataInSQLite readEntity(Cursor cursor, int offset) {
        RemarkCategoryDataInSQLite entity = new RemarkCategoryDataInSQLite( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // styleId
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // iId
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // sBillNo
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // sMeterMarkName
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, RemarkCategoryDataInSQLite entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setStyleId(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setIId(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setSBillNo(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setSMeterMarkName(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(RemarkCategoryDataInSQLite entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(RemarkCategoryDataInSQLite entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
