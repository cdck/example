package xlk.demo.test.wifi

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.wifi.ScanResult
import android.net.wifi.SupplicantState
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiManager.ERROR_AUTHENTICATING
import android.net.wifi.WifiManager.EXTRA_NEW_STATE
import android.os.Bundle
import android.util.Log.*
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_wifi.*
import xlk.demo.test.R
import xlk.demo.test.RvItemClick
import xlk.demo.test.util.WifiUtil
import xlk.demo.test.util.toast

class WifiActivity : AppCompatActivity(), View.OnClickListener {
    val TAG = "WifiActivity"
    lateinit var wifiManager: WifiManager
    var adapter: WifiAdapter? = null
    var datas = mutableListOf<ScanResult>()
    var toast: Toast? = null
    var firstTime: Long = 0
    var twoTime: Long = 0
    var oldMsg = ""
    var tryConnectSSID = ""
    var currentSSID = ""
    var wifiLog = ""

    fun toast(text: String) {
        if (toast == null) {
            toast = Toast.makeText(this, text, Toast.LENGTH_LONG)
            toast!!.show()
            firstTime = System.currentTimeMillis()
        } else {
            twoTime = System.currentTimeMillis()
            if (text.equals(oldMsg)) {
                if (twoTime - firstTime > Toast.LENGTH_LONG) {
                    toast!!.show()
                }
            } else {
                oldMsg = text
                toast!!.setText(text)
                toast!!.duration = Toast.LENGTH_LONG
                toast!!.show()
            }
        }
        firstTime = twoTime
    }

    private fun initAdapter() {
        d(TAG, "wifi个数：${datas.size}")
        if (adapter == null) {
            wifi_rv.layoutManager = LinearLayoutManager(this)
            adapter = WifiAdapter(this, datas)
            wifi_rv.adapter = adapter
        } else {
            adapter!!.notifyDataSetChanged()
        }
        adapter!!.setItemClickListentr(object : RvItemClick {
            override fun onItemClick(position: Int, vararg obj: Any) {
                position.toString().toast()
                val scanResult = obj[0] as ScanResult
                showPop(scanResult)
            }
        })
    }

    private fun refresh() {
        datas.clear()
        d(TAG, "refresh ${wifiManager.scanResults.size}")
        wifiManager.scanResults.filter { it.SSID != null && it.SSID.isNotEmpty() }
            .forEach { datas.add(it) }
        initAdapter()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    fun showPop(scanResult: ScanResult) {
        e(TAG, "${scanResult.isPasspointNetwork} \n ${scanResult.toString()}")
        val inflater = LayoutInflater.from(this).inflate(R.layout.pop_wifi_pwd, null)
        val pop = PopupWindow(
            inflater,
            500,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        pop.isOutsideTouchable = true
        pop.isFocusable = true
        pop.setBackgroundDrawable(BitmapDrawable(resources, null as Bitmap?))
        val pop_wifi_title = inflater.findViewById<TextView>(R.id.pop_wifi_title)
        val pop_wifi_pwd = inflater.findViewById<EditText>(R.id.pop_wifi_pwd)
        pop_wifi_title.text = scanResult.SSID
        inflater.findViewById<Button>(R.id.pop_wifi_cancel).setOnClickListener {
            pop.dismiss()
        }
        inflater.findViewById<Button>(R.id.pop_wifi_connect).setOnClickListener {
            val pwd = pop_wifi_pwd.text.toString().trim()
            if (pwd.isNotEmpty() && pwd.length >= 8) {
                tryConnectSSID = "\"${scanResult.SSID}\""
                WifiUtil.connectWifi(wifiManager, scanResult.SSID, pwd, scanResult.capabilities)
                pop.dismiss()
            } else {
                toast("密码不能为空或小于8位数")
            }
        }
        pop.showAtLocation(activity_wifi_root, Gravity.CENTER, 0, 0)
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.scan_wifi -> {
                refresh()
            }
            R.id.clean_log -> {
                wifiLog = ""
                wifi_log.text = wifiLog
            }
            R.id.clean_conf -> {
                wifiManager.configuredNetworks.forEach {
                    i(TAG, "将要删除的网络ID=${it.networkId}")
                    val removed = wifiManager.removeNetwork(it.networkId)
                    if (!removed) {
                        wifiLog += "无法删除${it.SSID}网络\n"
                    }
                }
//                val a = wifiManager.configuredNetworks.iterator()
//                while (a.hasNext()) {
//                    i(TAG,"将要删除的网络ID=${a.next().networkId}")
//                    wifiManager.removeNetwork(a.next().networkId)
//                }
                if (wifiManager.configuredNetworks.size == 0) {
                    wifiLog += "清空了所有配置网络\n"
                    wifi_log.text = wifiLog
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wifi)
        scan_wifi.setOnClickListener(this)
        clean_conf.setOnClickListener(this)
        clean_log.setOnClickListener(this)
        initFilter()
        wifiManager = application.getSystemService(WIFI_SERVICE) as WifiManager
        wifiManager!!.isWifiEnabled = true//开启WiFi
        wifiManager!!.configuredNetworks.forEach {
            d(TAG, "configuredNetworks 当前WiFi信息：${it.toString()}")
            it.allowedAuthAlgorithms//获取IEEE 802.11的加密方法
            it.allowedGroupCiphers//获取组密钥
            it.allowedKeyManagement//获取密码管理体制
            it.allowedPairwiseCiphers//获取WPA方式的成对密钥
            it.allowedProtocols//加密协议
        }
    }

    private fun initFilter() {
        val filter = IntentFilter()
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION)//RSSI（信号强度）已更改。
        //扫描到一个热点, 并且此热点达到可用状态 会触发此广播; 此时, 你可以通过 wifiManager.getScanResult() 来取出当前所扫描到的 ScanResult;
        //同时, 你可以从intent中取出一个boolean值; 如果此值为true, 代表着扫描热点已完全成功; 为false, 代表此次扫描不成功, ScanResult 距离上次扫描并未得到更新;
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION)//监听wifi是开关变化的状态
        //在网络配置, 保存, 添加, 连接, 断开, 忘记的操作过后, 均会对 WIFI 热点配置形成影响
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION)
        //建立连接的热点正在发生变化. 象征变化的相关类为: SupplicantState, 你可以在接收到此广播时, 观察到已经建立连接的热点的整个连接过程, 包含可能会出现连接错误的错误码.
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)//广播意图操作，指示与接入点建立连接的状态已更改。
        //广播意图操作，指示配置的网络已更改。 这可能是添加/更新/删除网络的结果。 如果{@link #EXTRA_MULTIPLE_NETWORKS_CHANGED}设置为true，
        // 则可以额外使用{@link #EXTRA_WIFI_CONFIGURATION}来获取新配置。
        // 如果更改了多个Wi-Fi配置，则{@link #EXTRA_WIFI_CONFIGURATION}将不存在。
        filter.addAction("android.net.wifi.CONFIGURED_NETWORKS_CHANGE")
        //广播意图操作，指示链接配置在wifi上已更改。
        filter.addAction("android.net.wifi.LINK_CONFIGURATION_CHANGED")
        //广播意图操作，指示Wi-Fi连接状态已更改。 一个额外功能以{@link android.net.NetworkInfo}对象的形式提供了新状态。
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION)
        registerReceiver(receiver, filter)
    }

    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            intent?.let {
                i(TAG, "当前WiFi广播：${it.action}")
                when (it.action) {
                    WifiManager.WIFI_STATE_CHANGED_ACTION -> {
                        val currentState = it.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
                        val preState = it.getIntExtra(WifiManager.EXTRA_PREVIOUS_WIFI_STATE, 0)
                        i(TAG, "WiFi当前状态：$currentState ，WiFi之前的状态：$preState")
                        when (currentState) {
                            WifiManager.WIFI_STATE_DISABLED -> {//Wi-Fi已禁用。
                                wifiLog += "Wi-Fi已禁用\n"
                                datas.clear()
                                initAdapter()
                            }
                            WifiManager.WIFI_STATE_ENABLING -> {//目前正在启用Wi-Fi。
                                wifiLog += "目前正在启用Wi-Fi\n"
                            }
                            WifiManager.WIFI_STATE_ENABLED -> {//Wi-Fi已启用。
                                wifiLog += "Wi-Fi已启用\n"
                                refresh()
                            }
                            WifiManager.WIFI_STATE_UNKNOWN -> {//Wi-Fi处于未知状态。 启用或禁用时发生错误时，将发生此状态。
                                wifiLog += "Wi-Fi处于未知状态\n"
                                datas.clear()
                                initAdapter()
                            }
                            else -> {

                            }
                        }
                    }
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION -> {
                        val results = it.getBooleanExtra(WifiManager.EXTRA_RESULTS_UPDATED, false)
                        wifiLog += "接入点扫描已完成，并且结果可用 是否扫描成功：$results \n"
                        if (results && wifiManager.isWifiEnabled) {
                            wifiLog += "扫描到的WiFi个数：${wifiManager.scanResults.size} \n"
                            refresh()
                        } else {
                            wifiLog += "扫描失败\n"
                        }
                    }
                    WifiManager.NETWORK_IDS_CHANGED_ACTION -> {
                        i(TAG, "配置的网络的网络ID可能已更改")
                        //配置的网络的网络ID可能已更改。
                    }
                    WifiManager.NETWORK_STATE_CHANGED_ACTION -> {
                        //配置的网络的网络ID可能已更改。
                        val netWorkInfo =
                            it.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO) as NetworkInfo
                        e(TAG, "配置的网络的网络ID可能已更改 广播意图操作，指示Wi-Fi连接状态已更改。 ${netWorkInfo.toString()}")
                        d(
                            TAG,
                            "配置的网络的网络ID可能已更改 " +
                                    "\nisConnected=${netWorkInfo.isConnected}, " +
                                    "\nextraInfo=${netWorkInfo.extraInfo}, " +
                                    "\nnetWorkInfo.detailedState.name=${netWorkInfo.detailedState.name}, " +
                                    "\nnetWorkInfo.detailedState.ordinal=${netWorkInfo.detailedState.ordinal}, " +
                                    "\nnetWorkInfo.state.name=${netWorkInfo.state.name}, " +
                                    "\nnetWorkInfo.state.ordinal=${netWorkInfo.state.ordinal}, " +
                                    "\nsubtypeName=${netWorkInfo.subtypeName}, " +
                                    "\nsubtype=${netWorkInfo.subtype}"
                        )
                        /*when (netWorkInfo.state.name) {
                            NetworkInfo.State.CONNECTING.name -> {
                                toast("正在尝试连接")
                            }
                            NetworkInfo.State.CONNECTED.name -> {
                                toast("连接成功")
                            }
                            NetworkInfo.State.SUSPENDED.name -> {
                                toast("已经暂停")
                            }
                            NetworkInfo.State.DISCONNECTING.name -> {
                                toast("正在断开连接")
                            }
                            NetworkInfo.State.DISCONNECTED.name -> {
                                toast("已经断开连接")
                            }
                            NetworkInfo.State.UNKNOWN.name -> {
                                toast("未知")
                            }
                        }*/
                        when (netWorkInfo.detailedState.ordinal) {
                            1 -> {
                                wifiLog += "正在扫描WiFi\n"
                            }
                            2 -> {
                                wifiLog += "正在尝试连接\n"
                            }
                            3 -> {
                                wifiLog += "正在进行身份验证\n"
                            }
                            5 -> {
                                currentSSID = wifiManager.connectionInfo.ssid
                                if (tryConnectSSID == currentSSID) {
                                    wifiLog += "连接成功：${currentSSID}\n"
                                    tryConnectSSID = ""
                                } else {
                                    if (tryConnectSSID.isNotEmpty()) {
                                        wifiLog += "手动连接${tryConnectSSID}失败，当前连接：${currentSSID}\n"
                                        WifiUtil.removeNetwork(wifiManager, tryConnectSSID)
                                        tryConnectSSID = ""
                                    } else {
                                        wifiLog += "当前连接WiFi：${currentSSID}\n"
                                    }
                                }
                            }
                            8 -> {
                                if (tryConnectSSID.isNotEmpty()) {
                                    wifiLog += "连接${tryConnectSSID}失败\n"
                                    WifiUtil.removeNetwork(wifiManager, tryConnectSSID)
                                    tryConnectSSID = ""
                                }
                                wifiLog += "已经断开连接\n"
                            }
                        }
                        refresh()
                    }
                    "android.net.wifi.CONFIGURED_NETWORKS_CHANGE" -> {
                        //操作了 wifiManager.configuredNetworks 中的WiFi配置后会收到该广播
                        val isMultipleChanged = intent.getBooleanExtra("multipleChanges", false)
                        if (!isMultipleChanged) {
                            val conf =
                                intent.getParcelableExtra("wifiConfiguration") as WifiConfiguration
                            i(TAG, "网络配置发生改变：${conf.toString()}")
                        }

                    }
                    WifiManager.SUPPLICANT_STATE_CHANGED_ACTION -> {
                        val a = intent.getParcelableExtra(EXTRA_NEW_STATE) as SupplicantState
                        val b = intent.getIntExtra(WifiManager.EXTRA_SUPPLICANT_ERROR, 111)
                        if (b == ERROR_AUTHENTICATING) {
                            wifiLog + "验证失败\n"
                        }
                    }
                    else -> {

                    }
                }
                wifi_log.text = wifiLog
            }
        }
    }
}
