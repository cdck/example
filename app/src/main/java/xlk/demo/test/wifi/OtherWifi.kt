package xlk.demo.test.wifi

import android.content.Context
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import xlk.demo.test.R

class OtherWifi : AppCompatActivity() {
    lateinit var manager: ConnectivityManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_other_wifi)
        manager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        manager.allNetworks
    }
}
