package xlk.demo.test.media


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_play.*
import xlk.demo.test.R

class PlayActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_play)
        restore.setOnClickListener { video_play_view.restore() }
        pause.setOnClickListener { video_play_view.pause() }
    }
}
