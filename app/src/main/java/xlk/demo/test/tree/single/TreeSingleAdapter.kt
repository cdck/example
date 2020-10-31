package xlk.demo.test.tree.single

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import xlk.demo.test.R
import xlk.demo.test.RvItemClick
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author by xlk
 * @date 2020/8/4 16:40
 * @desc
 */
class TreeSingleAdapter(
    private val context: Context,
    private var datas: CopyOnWriteArrayList<TreeInfos>
) :
    RecyclerView.Adapter<TreeSingleAdapter.ViewHolder>() {
    private var listener: RvItemClick? = null
    fun setListener(listener: RvItemClick?) {
        this.listener = listener
    }


    class ViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        var root_view: ConstraintLayout
        var iv: ImageView
        var tv: TextView

        init {
            root_view = itemView.findViewById(R.id.item_root_view)
            iv = itemView.findViewById(R.id.item_tree_iv)
            tv = itemView.findViewById(R.id.item_tree_content)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_tree, parent, false))
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val info = datas[position]
        holder.tv.text = info.name
        holder.iv.isSelected = info.isExpand
        if (info.children == null || info.children!!.size == 0) {
            holder.iv.visibility = View.INVISIBLE
        } else {
            holder.iv.visibility = View.VISIBLE
        }
        holder.root_view.setPadding(info.level * 30 - 30, 0, 0, 0)
        holder.itemView.setOnClickListener {
            if (info.children != null && info.children!!.size > 0) {
                if (info.isExpand) shrink(info)
                else expand(info)
            }
        }
    }

    private fun expand(info: TreeInfos) {
        info.isExpand = true
        val start = datas.indexOf(info)
        info.children!!.forEachIndexed { index, info ->
            datas.add(start + index + 1, info)
            if (info.isExpand) {
                expand(info)
            }
        }
        notifyDataSetChanged()
    }

    private fun shrink(info: TreeInfos) {
        info.isExpand = false
        datas.forEach {
            if (it.pid == info.id) {
                if (it.children != null && it.children!!.isNotEmpty() && it.isExpand) {
                    shrink(it)
                }
                datas.remove(it)
            }
        }
        notifyDataSetChanged()
    }
}