package com.suntek.ibmsapp.component;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class NetWorkStateReceiver extends BroadcastReceiver
{
    private OnNetworkStateLisener onNetworkStateLisener;

    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiinfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (!networkInfo.isConnected() && !wifiinfo.isConnected())
        {
            if(onNetworkStateLisener != null)
                onNetworkStateLisener.onState(OnNetworkStateLisener.NO_NETWORK_STATE);
        }
        else
        {
            if (wifiinfo.isConnected())
            {
                if(onNetworkStateLisener != null)
                    onNetworkStateLisener.onState(OnNetworkStateLisener.WIFI_STATE);

            }
            if (networkInfo.isConnected())
            {
                if(onNetworkStateLisener != null)
                    onNetworkStateLisener.onState(OnNetworkStateLisener.MOBILE_STATE);
            }
        }
    }

    public void setOnNetworkStateLisener(OnNetworkStateLisener onNetworkStateLisener)
    {
        this.onNetworkStateLisener = onNetworkStateLisener;
    }

    public interface OnNetworkStateLisener
    {
        int WIFI_STATE = 0;

        int MOBILE_STATE = 1;

        int NO_NETWORK_STATE = 2;

        void onState(int state);
    }
}
