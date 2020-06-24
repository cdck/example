package xlk.demo.test.wifi

import android.content.Context
import android.net.wifi.ScanResult
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xlk.demo.test.R
import xlk.demo.test.RvItemClick
import xlk.demo.test.util.WifiUtil

/**
 * @author by xlk
 * @date 2020/6/19 17:30
 * @desc 说明
 */
class WifiAdapter(val context: Context, private val data: List<ScanResult>) :
    RecyclerView.Adapter<WifiAdapter.ViewHolder>() {
    var ssid: String? = null
    var listener: RvItemClick? = null

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val iv = view.findViewById(R.id.item_wifi_connected) as ImageView
        val item_wifi_ssid = view.findViewById(R.id.item_wifi_ssid) as TextView
        val item_wifi_bssid = view.findViewById(R.id.item_wifi_bssid) as TextView
        val item_wifi_capabilities = view.findViewById(R.id.item_wifi_capabilities) as TextView
        val item_wifi_frequency = view.findViewById(R.id.item_wifi_frequency) as TextView
        val item_wifi_bandwidth = view.findViewById(R.id.item_wifi_bandwidth) as TextView
    }

    public fun setItemClickListentr(listener: RvItemClick) {
        this.listener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_wifi, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    public fun isConnected(ssid: String) {
        this.ssid = ssid
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        ssid?.let { holder.iv.isSelected = data[position].SSID.equals(ssid) }
        holder.item_wifi_ssid.text = "SSID：${data[position].SSID}"
        holder.item_wifi_bssid.text = "BSSID: ${data[position].BSSID}"
        holder.item_wifi_capabilities.text = "加密类型：${data[position].capabilities}"
        holder.item_wifi_frequency.text = "频率：${data[position].frequency}"
        holder.item_wifi_bandwidth.text =
            "带宽： ${WifiUtil.getChannelBandwidth(data[position].channelWidth)}"
        holder.itemView.setOnClickListener {
            listener?.let {
                it.onItemClick(holder.layoutPosition, data[position])
            }
        }
    }
}