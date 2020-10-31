package xlk.demo.test.tree

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_tree_list.*
import xlk.demo.test.R
import xlk.demo.test.tree.single.TreeInfos
import xlk.demo.test.tree.single.TreeSingleAdapter
import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author by xlk
 * @date 2020/7/2 14:20
 * @desc 说明
 */
class TreeListActivity : AppCompatActivity() {

    private var treeInfos = CopyOnWriteArrayList<TreeInfos>()
    private var singleAdapter: TreeSingleAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tree_list)
        initData()
        initSingleTreeAdapter()
    }

    private fun initData() {
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
    }

    private fun initSingleTreeAdapter() {
        singleAdapter = TreeSingleAdapter(this, treeInfos)
        tree_single_rv!!.layoutManager = LinearLayoutManager(this)
        tree_single_rv!!.adapter = singleAdapter
    }
}