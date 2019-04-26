package com.emt.fatri.wearbaidusdkdemo.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.emt.fatri.wearbaidusdkdemo.datamodel.NodeInfo;
import com.emt.fatri.wearbaidusdkdemo.utils.MainApplication;

import java.util.ArrayList;

/**
 * description:
 * Created by kingkong on 2018/8/14 0014.
 * changed by kingkong on 2018/8/14 0014.
 */

public class NodeManageApi {


    /**
     *
     * 删除前一天的数据
     * @author lijg4
     */
    public static void deleNeedlessNode() {

                ContentResolver contentResolver = MainApplication.getInstance().getContentResolver();
                Uri uri = MyProvider.CONTENT_URI_NODE_LIST;
                contentResolver.delete(uri, NodeInfo.CREATE_TIMME_INMILLS+"<?",
                        new String[]{String.valueOf(System.currentTimeMillis()-24*3600000)});


    }

    /**
     *插入一条数据到数据库
     */
    public static void insertNewItemToDb(final NodeInfo info) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(NodeInfo.CREATE_TIMME_INMILLS, info.createTimeInMills);
                values.put(NodeInfo.STATE_CODE, info.stateCode);
                MainApplication
                        .getInstance()
                        .getApplicationContext()
                        .getContentResolver()
                        .insert(MyProvider.CONTENT_URI_NODE_LIST,
                                values);
            }
        }).start();

    }

    /**
     *
     * @return
     */
    public static ArrayList<NodeInfo> getNodeInfoList(){
        ArrayList<NodeInfo> magazineList = new ArrayList<NodeInfo>();
        ContentResolver contentResolver = MainApplication.getInstance().getContentResolver();
        Uri uri = MyProvider.CONTENT_URI_NODE_LIST;
        Cursor cursor = contentResolver.query(uri, null, null, null,null);
        try {
            if (null != cursor && cursor.moveToFirst()) {
                do {
                    NodeInfo magazineData=new NodeInfo();
                    magazineData.createTimeInMills=cursor.getLong(cursor.getColumnIndex(NodeInfo.CREATE_TIMME_INMILLS));
                    magazineData.stateCode=cursor.getString(cursor.getColumnIndex(NodeInfo.STATE_CODE));
                    magazineList.add(magazineData);
                } while (cursor.moveToNext());
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return magazineList;

    }

}
