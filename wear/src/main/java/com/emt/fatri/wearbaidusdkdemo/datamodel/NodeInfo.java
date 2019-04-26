package com.emt.fatri.wearbaidusdkdemo.datamodel;

/**
 * description:节点信息类，包括监测节点信息封装和数据的解析。
 * Created by kingkong on 2018/5/10 0010.
 * changed by kingkong on 2018/5/10 0010.
 */

public class NodeInfo {
    /**节点id 字段*/
    public static final String NODE_ID = "nodeId";
    /**创建时间 字段*/
    public static final String CREATE_TIMME_INMILLS = "createTimeInMills";
    /**节点状态码 字段*/
    public static final String STATE_CODE = "stateCode";
    /**节点id */
    public int nodeId;
    /**创建时间字段*/
    public long createTimeInMills;
    /**节点状态码*/
    public String stateCode;

    @Override
    public String toString() {
        return "NodeInfo ---"
                +NODE_ID+":"+nodeId
                +CREATE_TIMME_INMILLS+":"+createTimeInMills
                +STATE_CODE+":"+stateCode;
    }
}
