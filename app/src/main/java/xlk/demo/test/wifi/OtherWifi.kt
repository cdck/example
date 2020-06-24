package xlk.demo.test.wifi

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xlk.demo.test.R

class OtherWifi : AppCompatActivity() {
    lateinit var manager: ConnectivityManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_wifi)
        manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.activeNetworkInfo.toString()
        manager.getNetworkCapabilities(manager.activeNetwork)
        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_FOTA)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_WIFI_P2P)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)//表示此网络应该可以访问Internet。
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)//表示该网络可用于一般用途。
            .addCapability(NetworkCapabilities.NET_CAPABILITY_FOREGROUND)//表示此网络可供应用程序使用，而不是一直在后台运行以促进快速网络切换的网络。
            .build()
        manager.registerNetworkCallback(networkRequest, networkCallback)
    }

    val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network?) {
            super.onAvailable(network)
        }

        override fun onCapabilitiesChanged(
            network: Network?,
            networkCapabilities: NetworkCapabilities?
        ) {
            super.onCapabilitiesChanged(network, networkCapabilities)
        }

        override fun onLost(network: Network?) {
            super.onLost(network)
        }

        override fun onUnavailable() {
            super.onUnavailable()
        }
    }
}

