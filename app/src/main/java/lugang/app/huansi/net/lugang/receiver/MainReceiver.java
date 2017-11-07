package lugang.app.huansi.net.lugang.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import org.greenrobot.eventbus.EventBus;

import lugang.app.huansi.net.lugang.event.NetConnectionEvent;

import static android.net.ConnectivityManager.CONNECTIVITY_ACTION;

/**
 * Created by 年 on 2017/11/6.
 */

public class MainReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action=intent.getAction();
        if(action==null) action="";
        switch (action){
            //网络连接
            case CONNECTIVITY_ACTION:
                NetConnectionEvent event=new NetConnectionEvent();
                event.isConnected=netIsConnected(context);
                EventBus.getDefault().post(event);
                break;
        }
    }

    /**
     * 判断网络是否通畅
     * @param context
     * @return
     */
    private boolean netIsConnected(Context context){
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(manager==null){
           return false;
        }
        NetworkInfo activeNetworkInfo = manager.getActiveNetworkInfo();
        if (activeNetworkInfo == null) {
            return false;
        }
        if (!activeNetworkInfo.isAvailable() || activeNetworkInfo.isFailover()) {
            return false;
        }
        return true;
    }
}
