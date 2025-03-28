package com.example.connectme

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.SurfaceView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas
import io.agora.rtc2.ChannelMediaOptions


class VideoCallActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQ_ID = 22
    }
    private val myAppId = "your_app_id"
    private val channelName = "ConnectMe"
    private val token = "your_token"
    private var mRtcEngine: RtcEngine? = null
    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            super.onJoinChannelSuccess(channel, uid, elapsed)
            showToast("Joined channel $channel")
        }
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            runOnUiThread {
                setupRemoteVideo(uid)
                showToast("User joined: $uid")
            }
        }
        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            runOnUiThread {
                showToast("User offline: $uid")
            }
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(this, getRequiredPermissions(), PERMISSION_REQ_ID)
    }
    private fun checkPermissions(): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }
    private fun getRequiredPermissions(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.BLUETOOTH_CONNECT
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQ_ID && checkPermissions()) {
            startVideoCalling()
        }
    }
    private fun startVideoCalling() {
        initializeAgoraVideoSDK()
        enableVideo()
        setupLocalVideo()
        joinChannel()
    }
    private fun initializeAgoraVideoSDK() {
        try {
            val config = RtcEngineConfig().apply {
                mContext = applicationContext
                mAppId = myAppId
                mEventHandler = mRtcEventHandler
            }
            mRtcEngine = RtcEngine.create(config)
        } catch (e: Exception) {
            throw RuntimeException("Error initializing RTC engine: ${e.message}")
        }
    }
    private fun enableVideo() {
        mRtcEngine?.apply {
            enableVideo()
            startPreview()
        }
    }
    private fun setupLocalVideo() {
        val container: FrameLayout = findViewById(R.id.myvideoPreviewContainer)
        val surfaceView = SurfaceView(baseContext)
        container.addView(surfaceView)
        mRtcEngine?.setupLocalVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, 0))
    }
    private fun joinChannel() {
        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            publishMicrophoneTrack = true
            publishCameraTrack = true
        }
        mRtcEngine?.joinChannel(token, channelName, 0, options)
    }
    private fun setupRemoteVideo(uid: Int) {
        val container: FrameLayout = findViewById(R.id.othervideoPreviewContainer)
        val surfaceView = SurfaceView(applicationContext).apply {
            setZOrderMediaOverlay(true)
            container.addView(this)
        }
        mRtcEngine?.setupRemoteVideo(VideoCanvas(surfaceView, VideoCanvas.RENDER_MODE_FIT, uid))
    }
    override fun onDestroy() {
        super.onDestroy()
        cleanupAgoraEngine()
    }
    private fun cleanupAgoraEngine() {
        mRtcEngine?.apply {
            stopPreview()
            leaveChannel()
        }
        mRtcEngine = null
    }
    private fun showToast(message: String) {
        runOnUiThread {
            Toast.makeText(this@VideoCallActivity, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        // Get the user info from the intent passed from the DM page
        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val profileImageResId = intent.getIntExtra("profileImage", R.drawable.asim)

        // Set the user info in the UI
        findViewById<TextView>(R.id.tvUserName).text = userName

        // Handle call actions (e.g., mute, video, speaker, end call)
        val endCallButton = findViewById<ImageView>(R.id.ivEndCall)
        endCallButton.setOnClickListener {
            // Finish activity to simulate ending the call
            finish()
        }

        // Mute button functionality
        val muteButton = findViewById<ImageView>(R.id.ivMute)
        muteButton.setOnClickListener {
            Toast.makeText(this, "Mute", Toast.LENGTH_SHORT).show()
        }

        // Speaker button functionality
        val speakerButton = findViewById<ImageView>(R.id.ivSpeaker)
        speakerButton.setOnClickListener {
            Toast.makeText(this, "Speaker", Toast.LENGTH_SHORT).show()
        }

        // Video button functionality (toggle video view or options)
        val videoButton = findViewById<ImageView>(R.id.ivVideo)
        videoButton.setOnClickListener {
            finish()

        }

        if (checkPermissions()) {
            startVideoCalling()
        } else {
            requestPermissions()
        }

    }
}