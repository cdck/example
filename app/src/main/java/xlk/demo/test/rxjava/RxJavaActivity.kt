package xlk.demo.test.rxjava

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_rx_java.*
import xlk.demo.test.MyApplication
import xlk.demo.test.R
import xlk.demo.test.util.logd
import java.util.*

class RxJavaActivity : AppCompatActivity() {
    private val TAG = "RxJavaActivity-->"
    private val observer: Observer<Int>? = null
    private val observable: Observable<Int>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_java)
        button.setOnClickListener { (application as MyApplication).exitApp() }
        val items = arrayOf(1, 2, 3)
        val list: MutableList<Int> = ArrayList()
        list.add(1)
        list.add(2)
        list.add(3)
        //在创建后就会发送这些对象，相当于执行了onNext(1)、onNext(2)、onNext(3)  最多只能发送10个参数
//        Observable.just(1, 2, 3)
        //在创建后就会将该数组转换成Observable & 发送该对象中的所有数据
//        Observable.fromArray(items)
        //通过fromIterable()将集合中的对象 / 数据发送出去
        Observable.fromIterable(list)
            .subscribe(object : Observer<Int> {
                override fun onSubscribe(d: Disposable) {
                    "onSubscribe".logd()
                }

                override fun onNext(integer: Int) {
                    "onNext $integer".logd()
                }

                override fun onError(e: Throwable) {
                    "onError ${e.message}".logd()
                }

                override fun onComplete() {
                    "onComplete".logd()
                }
            })
    }
}