package xlk.demo.test

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

/**
 * @author by xlk
 * @date 2020/7/24 9:36
 * @desc
 */
class FuncAdapter(val context: Context, val data: Array<String>) :
    RecyclerView.Adapter<FuncAdapter.ViewHolder>() {
    var listener: RvItemClick? = null

    fun setItemClickListener(listener: RvItemClick) {
        this.listener = listener
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btn = view.findViewById<Button>(R.id.item_btn)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_func, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.btn.text = data[position]
        holder.btn.setOnClickListener {
            System.out.println("listener是否为空${listener==null}")
            if (listener != null) {
                listener!!.onItemClick(position,data[position])
            }
        }
    }
}