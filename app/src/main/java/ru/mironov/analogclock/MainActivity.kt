package ru.mironov.analogclock

import android.annotation.SuppressLint
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.coroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.mironov.analogclock.databinding.ActivityMainBinding
import kotlin.concurrent.fixedRateTimer

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null

    private val binding get() = _binding!!

    private lateinit var timerJob: Job

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_main)

        val arrowSecond = findViewById<View>(R.id.arrow_second)
        val arrowMinute = findViewById<View>(R.id.arrow_minute)
        val arrowHour = findViewById<View>(R.id.arrow_hour)

        for (i in 1..12) {
            val item = findViewById<ViewGroup>(R.id.main)
            val numberView: View = layoutInflater.inflate(R.layout.clock_number_layout, null)
            val textNumber = numberView.findViewById<TextView>(R.id.clockNumber)
            textNumber.text = i.toString()
            numberView.rotation = i * 360 / 12f
            numberView.invalidate()
            textNumber.rotation=-i * 360 / 12f
            textNumber.invalidate()
            item.addView(numberView)
        }

        //Start timer
        timerJob = lifecycle.coroutineScope.launch(Dispatchers.Default) {
            fixedRateTimer("timer", false, 0L, 100)
            {
                lifecycle.coroutineScope.launch(Dispatchers.Main) {

                    val rightNow = Calendar.getInstance()
                    val hour = rightNow.get(Calendar.HOUR)
                    val minute = rightNow.get(Calendar.MINUTE)
                    val second =
                        rightNow.get(Calendar.MILLISECOND) / 1000f + rightNow.get(Calendar.SECOND)

                    arrowSecond.rotation = -90f + second * 360 / 60f
                    arrowSecond.invalidate()
                    arrowMinute.rotation = -90f + minute * 360 / 60f
                    arrowMinute.invalidate()
                    arrowHour.rotation = -90f + hour * 360 / 12f
                    arrowHour.invalidate()
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timerJob.cancel()
        _binding = null
    }
}
