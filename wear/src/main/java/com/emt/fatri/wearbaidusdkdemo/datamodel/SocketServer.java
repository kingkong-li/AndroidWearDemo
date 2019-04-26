package com.emt.fatri.wearbaidusdkdemo.datamodel;

import android.util.Log;

import com.emt.fatri.wearbaidusdkdemo.ui.MainActivity;
import com.emt.fatri.wearbaidusdkdemo.utils.GlobalConstant;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Socket服务器
 */
public class SocketServer {
    private static String TAG = SocketServer.class.getSimpleName();
    private static SocketServer mInstance;
    private OnDataReceiveListener mListener;
    private Thread connWatchDog;
    private final int SOCKET_PORT = 1111;// 端口号，变量命名不规范。
    private volatile boolean running = false;
    public static SocketServer getInstance() {
        if (mInstance == null) {
            mInstance = new SocketServer();
        }
        return mInstance;
    }

    public interface OnDataReceiveListener {
        void onReceiveData(String data);
    }

    public void setOnDataListener(OnDataReceiveListener listener) {
        mListener = listener;
    }

    public void start() {
        if (running) {
            return;

        }
        running = true;
        connWatchDog = new Thread(new ConnWatchDog());
        connWatchDog.start();
    }
    class ConnWatchDog implements Runnable {

        @Override
        public void run() {
            try {
                if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "ConnWatchDog:");
                ServerSocket ss = new ServerSocket(SOCKET_PORT);
                while (true) {
                    Socket s = ss.accept();
                    new Thread(new SocketAction(s)).start();
                }
            } catch (Exception e) {
                e.printStackTrace();
                SocketServer.this.stop();
                if(GlobalConstant.ERROR_LOG)Log.e(TAG, "Could not listen on port:" + SOCKET_PORT);
            }
        }
    }
    class SocketAction implements Runnable{
        Socket s;
        boolean run = true;
        long lastReceiveTime = System.currentTimeMillis();
        public SocketAction(Socket s) {
            this.s = s;
        }

        @Override
        public void run() {
            if(GlobalConstant.DEBUG_LOG)Log.d(TAG, "accept");
            while(running && run){
                    try {
                        InputStream in = s.getInputStream();
                        if (in.available()>0) {
                            byte[] recData = new byte[1024];
                            in.read(recData);
                            String data = new String(recData, "gb2312");
                            lastReceiveTime = System.currentTimeMillis();
                            if(GlobalConstant.DEBUG_LOG)Log.v(TAG,"data="+data.trim());
                            if(mListener!=null)
                            {
                                mListener.onReceiveData(data.trim());
                            }

                        }else{
                            Thread.sleep(10);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        overThis();
                    }
            }
        }

        public void overThis() {
            if(run)run=false;
            if(s!=null){
                try {
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(GlobalConstant.ERROR_LOG)Log.e(TAG,"关闭："+s.getRemoteSocketAddress());
        }
    }

    private void stop(){

        if(running)running = false;
        if(connWatchDog != null) {
            try {
                connWatchDog.stop();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}
