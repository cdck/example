package xlk.demo.test.tree.single

import java.util.concurrent.CopyOnWriteArrayList

/**
 * @author by xlk
 * @date 2020/8/4 16:36
 * @desc
 */
class TreeInfos(var pid: Int, var id: Int, var name: String) {
    var level = 0
    var isExpand = false
    var children: CopyOnWriteArrayList<TreeInfos>? = null

}