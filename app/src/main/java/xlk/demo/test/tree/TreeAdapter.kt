package xlk.demo.test.tree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import xlk.demo.test.R
import xlk.demo.test.RvItemClick

/**
 * @author by xlk
 * @date 2020/7/2 14:57
 * @desc 说明
 */
class TreeAdapter(
    private val context: Context,
    private val datas: List<Node>?
) : RecyclerView.Adapter<TreeAdapter.ViewHolder>() {
    private var listener: RvItemClick? = null
    fun setListener(listener: RvItemClick?) {
        this.listener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflate =
            LayoutInflater.from(context).inflate(R.layout.item_tree, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.tv.text = datas!![position].name
        if (datas[position] is DeviceInfo) {
            holder.iv.visibility = View.INVISIBLE
        } else {
            holder.iv.visibility = View.VISIBLE
            holder.iv.isSelected = datas[position].isExpand
        }
        holder.itemView.setOnClickListener {
            if (listener != null) {
                listener!!.onItemClick(position, datas[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return datas?.size ?: 0
    }

    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var iv: ImageView
        var tv: TextView

        init {
            iv = itemView.findViewById(R.id.item_tree_iv)
            tv = itemView.findViewById(R.id.item_tree_content)
        }
    }

}