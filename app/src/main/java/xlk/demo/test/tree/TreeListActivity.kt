package xlk.demo.test.tree

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_tree_list.*
import xlk.demo.test.R
import xlk.demo.test.RvItemClick
import xlk.demo.test.tree.single.TreeInfos
import xlk.demo.test.tree.single.TreeSingleAdapter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author by xlk
 * @date 2020/7/2 14:20
 * @desc 说明
 */
class TreeListActivity : AppCompatActivity() {
//    private val nodes = CopyOnWriteArrayList<Node>() //树形分组数据

    private var nodes: MutableList<Node> = ArrayList()
    private var treeInfos = CopyOnWriteArrayList<TreeInfos>()

    //    private var treeInfos: MutableList<TreeInfos> = ArrayList()
//    private var treeInfos = ConcurrentHashMap<Int, TreeInfos>()
    private var groupInfos: MutableList<GroupInfo> = ArrayList()
    private var deviceInfos: MutableList<DeviceInfo> = ArrayList()
    private var tree_rv: RecyclerView? = null
    private var treeAdapter: TreeAdapter? = null
    private var singleAdapter: TreeSingleAdapter? = null
    private val all_group = GroupInfo(0, "group_0")
    private val no_group = GroupInfo(51, "group_no")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree_list)
        initSingleDatas()
        initSingleTreeAdapter()
//        initDatas()
//        setNodeRelationship(groupInfos, deviceInfos)
//        initTreeAdapter()
    }

    private fun initSingleDatas() {
        val temps1 = CopyOnWriteArrayList<TreeInfos>()
        val temps2 = CopyOnWriteArrayList<TreeInfos>()
        for (index in 0..4) {
            val item = TreeInfos(index + 5, index + 10, "三级分组${index + 10}")
            item.level = 3
            temps2.add(item)
        }
        for (index in 0..4) {
            val item = TreeInfos(index, index + 5, "二级分组${index + 5}")
            item.level = 2
            val a = CopyOnWriteArrayList<TreeInfos>()
            a.add(temps2[index])
            item.children = a
            temps1.add(item)
        }
        for (index in 0..4) {
            val item = TreeInfos(100 + index, index, "一级分组$index")
            item.level = 1
            val a = CopyOnWriteArrayList<TreeInfos>()
            a.add(temps1[index])
            item.children = a
            treeInfos.add(item)
        }

//        for (i in 0..4) {
//            val item = TreeInfos(100 + i, i, "一级分组$i")
//            item.level = 1
//            val a = CopyOnWriteArrayList<TreeInfos>()
//            for (j in 5..9) {
//                val item = TreeInfos(i, j + i, "二级分组${j + i}")
//                item.level = 2
//                val b = CopyOnWriteArrayList<TreeInfos>()
//                for (k in 10..14) {
//                    val item = TreeInfos(j, k + j, "三级分组${k + j}")
//                    item.level = 3
//                    b.add(item)
//                }
//                item.children = b
//                a.add(item)
//            }
//            item.children = a
//            treeInfos.add(item)
//        }
    }

    private fun initSingleTreeAdapter() {
        singleAdapter = TreeSingleAdapter(this, treeInfos)
        tree_single_rv!!.layoutManager = LinearLayoutManager(this)
        tree_single_rv!!.adapter = singleAdapter
        singleAdapter!!.setListener(object : RvItemClick {
            override fun onItemClick(position: Int, vararg obj: Any) {
                val info = obj[0] as TreeInfos
                if (info.children == null || info.children.size == 0) return
                if (info.isExpand) shrink(info)
                else expand(info)
            }
        })
    }

    fun expand(info: TreeInfos) {
        info.isExpand = true
        val start = treeInfos.indexOf(info)
        info.children.forEachIndexed { index, info ->
            treeInfos.add(start + index + 1, info)
            if (info.isExpand) {
                expand(info)
            }
        }
        singleAdapter!!.notifyDataSetChanged()
    }

    fun shrink(info: TreeInfos) {
        info.isExpand = false
        treeInfos.forEach {
            if (it.pid == info.id) {
                if (it.children != null && it.children.isNotEmpty() && it.isExpand) {
                    shrink(it)
                }
                treeInfos.remove(it)
            }
        }
//        val iterator = treeInfos.iterator()
//        while (iterator.hasNext()) {
//            val next = iterator.next()
//            if (next.pid == info.id) {
//                if (next.children != null
//                    && next.children.isNotEmpty()
//                    && next.isExpand
//                ) {
//                    shrink(next)
//                }
//                iterator.remove()
//            }
//        }
        singleAdapter!!.notifyDataSetChanged()
    }


    private fun initTreeAdapter() {
        treeAdapter = TreeAdapter(this, nodes)
        tree_rv!!.layoutManager = LinearLayoutManager(this)
        tree_rv!!.adapter = treeAdapter
        treeAdapter!!.setListener(object : RvItemClick {
            override fun onItemClick(position: Int, vararg obj: Any) {
                //"$position".toast()
                val node = obj[0] as Node
                if (node is GroupInfo) {
                    notifyAdapter(position, nodes)
                }
            }
        })
    }

    private fun notifyAdapter(position: Int, nodes: MutableList<Node>) {
        val node = nodes[position]
        if (node.isExpand) {
            collapse(node, nodes)
            node.isExpand = false
        } else {
            expand(node, nodes)
            node.isExpand = true
        }
        treeAdapter!!.notifyDataSetChanged()
    }

    //展开
    private fun expand(node: Node, nodes: MutableList<Node>) {
        val tmpList: MutableList<Node> = ArrayList()
        tmpList.add(node)
        var tmp: Node
        while (tmpList.isNotEmpty()) {
            tmp = tmpList[0]
            tmpList.removeAt(0)
            var index = nodes.indexOf(tmp)
            tmp.children.forEach {
                if (it.isExpand) {
                    tmpList.add(it)
                }
                nodes.add(++index, it)
            }
        }
    }

    //收缩
    private fun collapse(node: Node, nodes: MutableList<Node>) {
        //查找
        val tempList: MutableList<Node> = ArrayList()
        val resultList: MutableList<Node> = ArrayList()
        tempList.add(node)
        var temp: Node
        while (tempList.isNotEmpty()) {
            temp = tempList[0]
            tempList.removeAt(0)
            resultList.add(0, temp)
            temp.children.forEach {
                if (it.isExpand) {
                    tempList.add(0, it)
                }
            }
        }
        tempList.clear()
        while (resultList.isNotEmpty()) {
            temp = resultList[0]
            resultList.removeAt(0)
            val index = nodes.indexOf(temp)
            if (index != -1) {
                temp.children.forEachIndexed { index, node ->
                    val rmindex = index + 1
                    if (rmindex < nodes.size) {
                        nodes.removeAt(rmindex)
                    }
                }
            }
        }
    }

    private fun initDatas() {
        val ass = mutableListOf<Node>()
        for (i in 1..50) {
            if (i <= 25) {
                deviceInfos.add(DeviceInfo(i, "device_$i"))
            } else {
                ass.add(DeviceInfo(i, "device_$i", 52))
            }
        }
        val element = GroupInfo(52, "group_$52")
        element.children = ass
        groupInfos.add(element)
    }

    private fun setNodeRelationship(
        groups: MutableList<GroupInfo>,
        devices: MutableList<DeviceInfo>
    ) {
        val a = mutableListOf<Node>()
        groups.forEach {
            it.parent = all_group
            a.add(it)
        }
        devices.forEach {
            it.parent = no_group
            a.add(it)
        }
        nodes.add(all_group)
        no_group.parent = all_group
        no_group.children = a
        all_group.children = mutableListOf<Node>(no_group)
    }
}