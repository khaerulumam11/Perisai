package co.id.perisaijava

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import co.id.perisaijava.databinding.ActivityAlarmInfoBinding
import co.id.perisaijava.databinding.ActivityMainBinding

class AlarmInfoActivity : AppCompatActivity() {
    private lateinit var activityAlarmInfoBinding: ActivityAlarmInfoBinding
    var mediaPlayer:MediaPlayer ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAlarmInfoBinding = ActivityAlarmInfoBinding.inflate(layoutInflater)
        setContentView(activityAlarmInfoBinding.root)
        mediaPlayer = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM))
        setAlarmNow()

        activityAlarmInfoBinding.btnOffAlarm.setOnClickListener {
            mediaPlayer!!.stop()
            finish()
        }
    }

    private fun setAlarmNow() {
        val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(4000)
        mediaPlayer!!.start()
    }

}