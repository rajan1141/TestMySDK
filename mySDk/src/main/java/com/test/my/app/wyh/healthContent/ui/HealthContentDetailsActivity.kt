package com.test.my.app.wyh.healthContent.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import androidx.lifecycle.ViewModelProvider
import com.test.my.app.R
import com.test.my.app.common.base.BaseActivity
import com.test.my.app.common.base.BaseViewModel
import com.test.my.app.common.constants.Constants
import com.test.my.app.common.utils.AppColorHelper
import com.test.my.app.common.utils.DateHelper
import com.test.my.app.common.utils.Utilities
import com.test.my.app.common.view.CommonBinding.setImgUrl
import com.test.my.app.databinding.ActivityHealthContentDetailsBinding
import com.test.my.app.wyh.common.HealthContent
import com.test.my.app.wyh.common.WyhHelper
import com.test.my.app.wyh.healthContent.viewmodel.WyhHealthContentViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

@AndroidEntryPoint
class HealthContentDetailsActivity : BaseActivity() {

    lateinit var binding: ActivityHealthContentDetailsBinding
    private val wyhHealthContentViewModel: WyhHealthContentViewModel by lazy {
        ViewModelProvider(this)[WyhHealthContentViewModel::class.java]
    }
    private val appColorHelper = AppColorHelper.instance!!
    private lateinit var mediaPlayer: MediaPlayer
    private val handler = Handler(Looper.getMainLooper())
    private var audioIndex = 0
    private var elapsedTime: Long = 0 // Time in milliseconds
    private var timerJob: Job? = null
    var isBookMarkChanged = false

    /*    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(category) {
                    Constants.BLOGS,Constants.QUICK_READS -> {
                        val minutes = (elapsedTime / (1000 * 60)) % 60
                        wyhHealthContentViewModel.callAddBlogReadingDurationApi(minutes.toInt(),healthContent,this@HealthContentDetailsActivity)
                        //finish()
                    }
                    else -> finish()
                }
            }
        }*/

    private val onBackPressedCallBack = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            val minutes = (elapsedTime / (1000 * 60)) % 60
            wyhHealthContentViewModel.callAddBlogReadingDurationApi(minutes.toInt(),healthContent,this@HealthContentDetailsActivity)
            finishActivityWithResult()
        }
    }

    private fun finishActivityWithResult() {
        val data = Intent()
        data.putExtra(Constants.IS_CHANGED,isBookMarkChanged)
        data.putExtra(Constants.CATEGORY,category)
        setResult(Activity.RESULT_OK, data)
        finish()
    }

    companion object {
        var category = "Category"
        var healthContent = HealthContent()
        var healthContentList : MutableList<HealthContent> = mutableListOf()
    }

    override fun getViewModel(): BaseViewModel = wyhHealthContentViewModel

    override fun onCreateEvent(savedInstanceState: Bundle?) {
        binding = ActivityHealthContentDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(this,onBackPressedCallBack)
        try {
            Utilities.printLogError("Category--->$category")
            Utilities.printData("HealthContent",healthContent)
            for ( i in 0 until healthContentList.size ) {
                if ( healthContent.contentUrl == healthContentList[i].contentUrl ) {
                    audioIndex = i
                }
            }
            setUpToolbar()
            initialise()
        } catch ( e:Exception ) {
            e.printStackTrace()
        }
    }

    private fun initialise() {
        binding.txtReadingDuration.visibility = View.GONE
        binding.imgBookMark.visibility = View.GONE
        when(category) {
            Constants.BLOGS,Constants.QUICK_READS -> {
                binding.layoutBlogDetails.visibility = View.VISIBLE
                binding.layoutVideoDetails.visibility = View.GONE
                binding.layoutAudioDetails.visibility = View.GONE
                renderBlogContent()
            }
            Constants.VIDEOS -> {
                binding.layoutBlogDetails.visibility = View.GONE
                binding.layoutVideoDetails.visibility = View.VISIBLE
                binding.layoutAudioDetails.visibility = View.GONE
                renderVideoContent()
            }
            Constants.AUDIOS -> {
                binding.layoutBlogDetails.visibility = View.GONE
                binding.layoutVideoDetails.visibility = View.GONE
                binding.layoutAudioDetails.visibility = View.VISIBLE
                renderAudioContent()
            }
        }
    }

    private fun renderBlogContent() {
        startTimer()
        binding.txtReadingDuration.visibility = View.VISIBLE
        binding.imgBookMark.visibility = View.VISIBLE
        if ( healthContent.isBookMarked ) {
            binding.imgBookMark.setImageResource(R.drawable.ic_bookmark_selected)
        } else {
            binding.imgBookMark.setImageResource(R.drawable.ic_bookmark_unselected)
        }
        binding.imgBlog.setImgUrl(WyhHelper.getWyhUrlToLoad(healthContent.contentImgUrl!!))
        if ( !Utilities.isNullOrEmpty(healthContent.createdOn) ) {
            binding.txtBlogDate.text = DateHelper.getDateTimeAs_ddMMMyyyyNew(healthContent.createdOn!!.split("T").toTypedArray()[0])
            binding.txtBlogDate.visibility = View.VISIBLE
            binding.imgCalender.visibility = View.VISIBLE
        } else {
            binding.txtBlogDate.text = ""
            binding.txtBlogDate.visibility = View.GONE
            binding.imgCalender.visibility = View.GONE
        }
        binding.txtBlogTitle.text = healthContent.contentName
        if ( !Utilities.isNullOrEmpty(healthContent.contentDesc) ) {
            binding.txtBlogDesciption.text = healthContent.contentDesc
            binding.txtBlogDesciption.visibility = View.VISIBLE
        } else {
            binding.txtBlogDesciption.text = ""
            binding.txtBlogDesciption.visibility = View.GONE
        }

        val htmlContent = """
    <html>
    <head>
        <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Outfit:wght@400;600&display=swap">
        <style>
            body {
                font-family: 'Outfit', sans-serif;
                font-size: 15px;
                color: #333;
            }
        </style>
    </head>
    <body>
        <p>${healthContent.htmlContent!!}</p>
    </body>
    </html>
"""

        binding.webViewBlogs.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)

        wyhHealthContentViewModel.addBlogReadingDuration.observe(this) { }
        wyhHealthContentViewModel.addBookMark.observe(this) { }

        binding.imgBookMark.setOnClickListener {
            //healthContent.isBookMarked = !healthContent.isBookMarked
            if ( healthContent.isBookMarked ) {
                //binding.imgBookMark.setImageResource(R.drawable.ic_bookmark_selected)
                wyhHealthContentViewModel.callAddBookMarkApi(healthContent,false,this@HealthContentDetailsActivity)
            } else {
                //binding.imgBookMark.setImageResource(R.drawable.ic_bookmark_unselected)
                wyhHealthContentViewModel.callAddBookMarkApi(healthContent,true,this@HealthContentDetailsActivity)
            }
        }
    }

    private fun renderVideoContent() {

        binding.txtVideoDate.text = DateHelper.getDateTimeAs_ddMMMyyyyNew(healthContent.createdOn!!.split("T").toTypedArray()[0])
        binding.txtVideoTitle.text = healthContent.contentName
        if ( !Utilities.isNullOrEmpty(healthContent.contentDesc) ) {
            binding.txtVideoDesciption.text = healthContent.contentDesc
            binding.txtVideoDesciption.visibility = View.VISIBLE
        } else {
            binding.txtVideoDesciption.text = ""
            binding.txtVideoDesciption.visibility = View.GONE
        }

        val youTubePlayerView = binding.videoView
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.wrapContent()

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady( youTubePlayer: YouTubePlayer ) {
                //val videoId = "S0Q4gqBUs7c"
                val videoId = Utilities.getYouTubeVideoId(healthContent.contentUrl!!)
                youTubePlayer.loadVideo(videoId,0f)
            }
        })

        /*        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                    override fun onReady( youTubePlayer: YouTubePlayer) {
                        // loading the selected video into the YouTube Player
                        youTubePlayer.loadVideo(healthContent.contentUrl!!,0f)
                    }
                    override fun onStateChange(youTubePlayer: YouTubePlayer, state: PlayerConstants.PlayerState) {
                        // this method is called if video has ended,
                        super.onStateChange(youTubePlayer, state)
                    }
                })*/
    }

    private fun renderAudioContent() {
        setAudioDetails()
        setupMediaPlayer()

        // Play/Pause functionality
        binding.imgPlayPause.setOnClickListener {
            if (mediaPlayer.isPlaying) {
                mediaPlayer.pause()
                binding.imgPlayPause.setImageResource(R.drawable.ic_play)
            } else {
                mediaPlayer.start()
                binding.imgPlayPause.setImageResource(R.drawable.ic_pause)
                updateSeekBar()
            }
        }

        // Slider change listener
        binding.seekBar.addOnChangeListener { _, value, fromUser ->
            // Handle value changes
            Utilities.printLogError("Current value: $value")
            if (fromUser) {
                mediaPlayer.seekTo(value.toInt())
                binding.txtCurrent.text = formatTime(value.toInt())
            }
        }

        // Update SeekBar and time labels
        mediaPlayer.setOnCompletionListener {
            resetAudioPlayer()
        }

        // Play/Pause functionality
        binding.imgPrevious.setOnClickListener {
            playPrevious()
        }

        binding.imgNext.setOnClickListener {
            playNext()
        }
    }

    private fun setupMediaPlayer() {
        //wyhHealthContentViewModel.showProgress()
        binding.progressBar.visibility = View.VISIBLE
        //val audioUrl = "https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3"
        val audioUrl = WyhHelper.getWyhUrlToLoad(healthContent.contentUrl!!)
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener {
                binding.seekBar.valueTo = it.duration.toFloat()
                binding.txtTotal.text = formatTime(duration)
                //wyhHealthContentViewModel.hideProgress()
                binding.progressBar.visibility = View.GONE
                mediaPlayer.start()
                binding.imgPlayPause.setImageResource(R.drawable.ic_pause)
                updateSeekBar()
            }
        }
    }

    private fun setAudioDetails() {
        binding.imgAudio.setImgUrl(WyhHelper.getWyhUrlToLoad(healthContent.contentImgUrl!!))
        //binding.txtBlogDate.text = DateHelper.getDateTimeAs_ddMMMyyyyNew(healthContent.createdOn!!.split("T").toTypedArray()[0])
        binding.txtAudioTitle.text = healthContent.contentName
        if ( !Utilities.isNullOrEmpty(healthContent.contentDesc) ) {
            binding.txtBlogDesciption.text = healthContent.contentDesc
            binding.txtBlogDesciption.visibility = View.VISIBLE
        } else {
            binding.txtBlogDesciption.text = ""
            binding.txtBlogDesciption.visibility = View.GONE
        }
    }

    private fun playAudio(item : HealthContent) {
        healthContent = item
        Utilities.printData("HealthContent", healthContent)
        mediaPlayer.reset()
        setAudioDetails()
        resetAudioPlayer()
        setupMediaPlayer()
        //mediaPlayer.setDataSource(WyhHelper.getWyhUrlToLoad(healthContent.contentUrl!!))
        //mediaPlayer.prepareAsync()
    }

    private fun playPrevious() {
        audioIndex = if (audioIndex - 1 < 0) healthContentList.size - 1 else audioIndex - 1
        playAudio(healthContentList[audioIndex])
    }

    private fun playNext() {
        audioIndex = (audioIndex + 1) % healthContentList.size
        if ( audioIndex < healthContentList.size) {
            playAudio(healthContentList[audioIndex])
        }
    }

    private fun resetAudioPlayer() {
        binding.imgPlayPause.setImageResource(R.drawable.ic_play)
        binding.seekBar.value = 0f
        binding.txtCurrent.text = "00:00"
        binding.txtTotal.text = formatTime(mediaPlayer.duration)
    }

    private fun updateSeekBar() {
        if (mediaPlayer.isPlaying) {
            binding.seekBar.value = mediaPlayer.currentPosition.toFloat()
            binding.txtCurrent.text = formatTime(mediaPlayer.currentPosition)
            handler.postDelayed({ updateSeekBar() },1000)
        }
    }

    private fun formatTime(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format(Locale.ENGLISH,"%02d:%02d",minutes,seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        when(category) {
            Constants.AUDIOS -> {
                mediaPlayer.release()
                handler.removeCallbacksAndMessages(null)
            }
            Constants.BLOGS,Constants.QUICK_READS -> {
                stopTimer() // Stop the timer when the activity is destroyed
            }
        }
    }

    private fun setUpToolbar() {
        setSupportActionBar(binding.toolBarView.toolbarCommon)
        binding.toolBarView.toolbarTitle.text = "Content Detail"
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayShowHomeEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_back)
        binding.toolBarView.toolbarCommon.navigationIcon?.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(appColorHelper.textColor, BlendModeCompat.SRC_ATOP)
        binding.toolBarView.toolbarCommon.setNavigationOnClickListener {
            onBackPressedCallBack.handleOnBackPressed()
        }
        binding.toolBarView.imgHelp.setImageResource(R.drawable.ic_close)
        binding.toolBarView.imgHelp.visibility = View.GONE
        binding.toolBarView.imgHelp.setOnClickListener {
            finish()
        }
    }

    /*    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_health_content_details)
            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }*/

    @SuppressLint("SetTextI18n")
    private fun startTimer() {
        // Start a Coroutine to increment elapsedTime every second
        timerJob = CoroutineScope(Dispatchers.Main).launch {
            while (isActive) {
                delay(1000) // Wait for 1 second
                elapsedTime += 1000 // Increment time by 1000ms (1 second)
                // Convert elapsedTime to hours, minutes, and seconds
                val seconds = (elapsedTime / 1000) % 60
                val minutes = (elapsedTime / (1000 * 60)) % 60
                //val hours = (elapsedTime / (1000 * 60 * 60))
                // Update the UI or print the time
                //println("Elapsed Time: ${hours}h ${minutes}m ${seconds}s")
                // Example: textView.text = "$hours h $minutes m $seconds s"
                binding.txtReadingDuration.text = "Reading Time - ${String.format(Locale.ENGLISH,"%02d:%02d", minutes, seconds)}"
            }
        }
    }

    private fun stopTimer() {
        timerJob?.cancel() // Stop the timer
        timerJob = null
        Utilities.printLogError("Timer Destroyed")
    }

}