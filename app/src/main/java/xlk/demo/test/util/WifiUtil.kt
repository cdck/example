package xlk.demo.test.util

import android.net.wifi.ScanResult
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log.i

/**
 * @author by xlk
 * @date 2020/6/19 14:39
 * @desc wifi工具类
 */
object WifiUtil {
    /**
     * 连接到指定WiFi
     * @param name wifi名称 SSID
     * @param pwd  wifi密码
     */
    fun connectWifi(manager: WifiManager, name: String, pwd: String, capabilities: String) {
        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        val ssid = "\"$name\""
        val password = "\"$pwd\""
        i("connectWifi","WiFi名称和密码 ssid：$ssid password:$password")
        //2、配置wifi信息
        val conf = WifiConfiguration()
        conf.allowedAuthAlgorithms.clear()
        conf.allowedGroupCiphers.clear()
        conf.allowedKeyManagement.clear()
        conf.allowedPairwiseCiphers.clear()
        conf.allowedProtocols.clear()
        conf.SSID = ssid
        when {
            capabilities.contains("WPA") || capabilities.contains("WPA2") -> {
                i("connectWifi","WPA")
                //基本上都会走这一步
                conf.preSharedKey = password
                //conf.hiddenSSID = true;
                conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
                conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
                conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
                conf.status = WifiConfiguration.Status.ENABLED
            }
            capabilities.contains("WEP") -> {
                i("connectWifi","WEP")
                conf.wepKeys[0] = password
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
            }
            else -> {
                i("connectWifi","Other")
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
        }
        //3、添加到 configuredNetworks 中
        manager.addNetwork(conf)
        i("connectWifi","添加到 configuredNetworks 中")
        manager.configuredNetworks.forEach {
            if (it.SSID != null && it.SSID == ssid) {
                manager.disconnect()
                //连接到netId对应的wifi,boolean参数，主要用于指定是否需要断开其它Wifi网络
                val enable = manager.enableNetwork(it.networkId, true)
                i("connectWifi", "enable: $enable")
                //可选操作，让Wifi重新连接最近使用过的接入点
                //如果上文的enableNetwork成功，那么reconnect同样连接netId对应的网络
                //若失败，则连接之前成功过的网络
                val reconnect = manager.reconnect()
                i("connectWifi", "reconnect: $reconnect")
                return
            }
        }
    }

    fun getChannelBandwidth(type: Int): String {
        when (type) {
            ScanResult.CHANNEL_WIDTH_20MHZ -> return "20MHZ"
            ScanResult.CHANNEL_WIDTH_40MHZ -> return "40MHZ"
            ScanResult.CHANNEL_WIDTH_80MHZ -> return "80MHZ"
            ScanResult.CHANNEL_WIDTH_160MHZ -> return "160MHZ"
            ScanResult.CHANNEL_WIDTH_80MHZ_PLUS_MHZ -> return "80MHZ+80MHZ"
        }
        return ""
    }

    fun saveWifi(manager: WifiManager, config: WifiConfiguration) {
        val save =
            manager.javaClass.getDeclaredMethod("save", WifiConfiguration::class.java)
        save.isAccessible = true
        save.invoke(manager, config, null)
    }

    fun removeNetwork(manager: WifiManager, ssid: String) {
        manager.configuredNetworks.forEach {
            if (it.SSID == ssid) {
                manager.removeNetwork(it.networkId)
                return
            }
        }
    }
}