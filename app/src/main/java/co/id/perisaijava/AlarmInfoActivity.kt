package co.id.perisaijava

import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Vibrator
import androidx.appcompat.app.AppCompatActivity
import co.id.perisaijava.databinding.ActivityAlarmInfoBinding


class AlarmInfoActivity : AppCompatActivity() {
    private lateinit var activityAlarmInfoBinding: ActivityAlarmInfoBinding
    var mediaPlayer:MediaPlayer ?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityAlarmInfoBinding = ActivityAlarmInfoBinding.inflate(layoutInflater)
        setContentView(activityAlarmInfoBinding.root)
        mediaPlayer = MediaPlayer.create(this, R.raw.emergency_alarm)
        setAlarmNow()

        activityAlarmInfoBinding.btnOffAlarm.setOnClickListener {
            mediaPlayer!!.stop()
            finish()
        }
    }

    private fun setAlarmNow() {
        val am = getSystemService(AUDIO_SERVICE) as AudioManager

        am.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
            0
        )
        val vibrator = application.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(4000)
        mediaPlayer!!.isLooping = true
        mediaPlayer!!.start()

    }

}