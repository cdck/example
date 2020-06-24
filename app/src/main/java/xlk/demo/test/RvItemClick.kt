package xlk.demo.test

/**
 * @author by xlk
 * @date 2020/6/19 18:28
 * @desc RecyclerView item点击监听接口
 */
interface RvItemClick {
    fun onItemClick(position: Int, obj: Any)
}