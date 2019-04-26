package com.emt.fatri.wearbaidusdkdemo.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

import com.emt.fatri.wearbaidusdkdemo.datamodel.NodeInfo;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;

import java.util.ArrayList;

/**
 * description:报警信息内容提供者，负责管理数据库。
 * Created by kingkong on 2018/5/10 0010.
 * changed by kingkong on 2018/5/10 0010.
 */

public class MyProvider extends ContentProvider {
    private static final String LOG_CLASS_NAME = "MyProvider";
    /** 数据库版本号 */
    private static final int DATABASE_VERSION = 1;
    /** 数据库名称 */
    private static final String DATABASE_NAME = "MyProvider.db";
    public static final String AUTHORITY = "com.emt.fatri.MyProvider";
    /** 监控节点数据表*/
    private static final String NODE_TABLE_NAME = "NodeList";
    private static final UriMatcher mUriMatcher;
    private static final int NODE_LIST = 1;
    public static final Uri CONTENT_URI_NODE_LIST = Uri.parse("content://" + AUTHORITY + "/NodeList");

    /** 数据库操作帮助类 */
    private DatabaseHelper	mOpenHelper;

    static{
        mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        mUriMatcher.addURI(AUTHORITY, NODE_TABLE_NAME, NODE_LIST);

    }



    @Override
    public boolean onCreate() {
        mOpenHelper = DatabaseHelper.getInstance(getContext());
        if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onCreate this:"+this);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        switch (mUriMatcher.match(uri)) {
            case NODE_LIST:
                cursor=db.query(NODE_TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (cursor != null) {
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return cursor;
    }

    @Override
    public String getType(Uri uri) {

        return null;
    }

    /**
     * 根据Uri向表里插入一条数据。
     * @author lijg4
     *
     */

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        ContentValues values;
        Uri returnUri;
        if (initialValues != null){
            values = new ContentValues(initialValues);
        }
        else{
            values = new ContentValues();
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId;
        switch (mUriMatcher.match(uri)){
            case NODE_LIST:
                //values要和table的列对应 否则会出现插入错误*
                rowId = db.insert(NODE_TABLE_NAME, null, values);
                if (rowId > 0){
                    returnUri = ContentUris.withAppendedId(CONTENT_URI_NODE_LIST, rowId);
                    getContext().getContentResolver().notifyChange(returnUri, null);

                    return returnUri;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return null;
    }

    /**
     * 批量插入
     * @param uri 插入的表名地址
     * @param values 插入的值
     * @return 此次插入了多少个值
     */
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (values == null || values.length == 0) {
            return 0;
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;

        switch (mUriMatcher.match(uri)){
            case NODE_LIST:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        db.insert(NODE_TABLE_NAME, null, value);
                        count++;
                    }
                    db.setTransactionSuccessful();
                    getContext().getContentResolver().notifyChange(uri, null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                } finally {
                    db.endTransaction();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        return count;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;

        switch (mUriMatcher.match(uri)){
            case NODE_LIST:
                count = db.delete(NODE_TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count ;
        switch (mUriMatcher.match(uri)){
            case NODE_LIST:
                count = db.update(NODE_TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public ContentProviderResult[] applyBatch(
            ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        ContentProviderResult[] result;
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            result = super.applyBatch(operations);
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
        return result;
    }

    /**
     * 创建数据库并且创建数据库里面的表
     * @author lijg4
     *
     *
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        private static DatabaseHelper mInstance = null;

        private static final String CREATE_NODE_LIST_TABLE="CREATE TABLE "
                + NODE_TABLE_NAME
                + " ("
                + "_id" + " INTEGER PRIMARY KEY,"
                + NodeInfo.CREATE_TIMME_INMILLS	+ " TEXT,"
                + NodeInfo.STATE_CODE       + " TEXT"
                + ");";



        public static DatabaseHelper getInstance(Context context) {
            if (mInstance == null) {
                mInstance = new DatabaseHelper(context.getApplicationContext());
            }

            return mInstance;
        }

        private DatabaseHelper(Context context){
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "NewDataBaseHelper："+getDatabaseName());
        }

        @Override
        public void onCreate(SQLiteDatabase db){
            if(GlobalConstant.DEBUG_LOG)Log.v(LOG_CLASS_NAME, "onCreate Table in(db):"+ " " +db);

            // 创建监控节点数据库表
            db.execSQL(CREATE_NODE_LIST_TABLE);

        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

                db.execSQL("DROP TABLE IF EXISTS " +NODE_TABLE_NAME);
                onCreate(db);

        }

        @Override
        public void onDowngrade (SQLiteDatabase db, int oldVersion, int newVersion){
            db.execSQL("DROP TABLE IF EXISTS " + NODE_TABLE_NAME);
            onCreate(db);
        }

        /**
         * 添加列
         * @param dbTable 数据表名称
         * @param columnName 列名称
         * @param columnDefinition 列定义
         */
        @SuppressWarnings("unused")
        private void addColumn(SQLiteDatabase db, String dbTable, String columnName,
                               String columnDefinition) throws SQLException {
            db.execSQL("ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " "
                    + columnDefinition);
        }

    }
}
