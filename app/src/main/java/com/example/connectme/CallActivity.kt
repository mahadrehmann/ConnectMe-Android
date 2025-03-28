package com.example.connectme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.ChannelMediaOptions

class CallActivity : AppCompatActivity() {

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
            runOnUiThread {
                Toast.makeText(this@CallActivity, "Joined channel $channel", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onUserJoined(uid: Int, elapsed: Int) {
            super.onUserJoined(uid, elapsed)
            runOnUiThread {
                Toast.makeText(this@CallActivity, "User joined: $uid", Toast.LENGTH_SHORT).show()
            }
        }
        override fun onUserOffline(uid: Int, reason: Int) {
            super.onUserOffline(uid, reason)
            runOnUiThread {
                Toast.makeText(this@CallActivity, "User offline: $uid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Check and request necessary permissions
    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.RECORD_AUDIO),
            PERMISSION_REQ_ID
        )
    }

    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (requestCode == PERMISSION_REQ_ID && grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startVoiceCall()
        } else {
            Toast.makeText(this, "Permissions not granted", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    // Initialize Agora engine for audio calls only
    private fun initializeAgoraAudioSDK() {
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

    private fun startVoiceCall() {
        mRtcEngine?.enableAudio()  // Enable audio functionality
        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            publishMicrophoneTrack = true
            publishCameraTrack = false  // Do not publish video
        }
        mRtcEngine?.joinChannel(token, channelName, 0, options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        // Get user info from intent
        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val profileImageResId = intent.getIntExtra("profileImage", R.drawable.asim)

        // Set user info in UI
        findViewById<TextView>(R.id.tvUserName).text = userName
        findViewById<ImageView>(R.id.ivProfilePicture).setImageResource(profileImageResId)

        // End call button: finish activity
        val endCallButton = findViewById<ImageView>(R.id.ivEndCall)
        endCallButton.setOnClickListener { finish() }

        // Mute button: toggle mute
        val muteButton = findViewById<ImageView>(R.id.ivMute)
        muteButton.setOnClickListener {
            mRtcEngine?.muteLocalAudioStream(true)
            Toast.makeText(this, "Muted", Toast.LENGTH_SHORT).show()
        }

        // Speaker button: enable speakerphone
        val speakerButton = findViewById<ImageView>(R.id.ivSpeaker)
        speakerButton.setOnClickListener {
            mRtcEngine?.setEnableSpeakerphone(true)
            Toast.makeText(this, "Speaker enabled", Toast.LENGTH_SHORT).show()
        }

        // (Optional) Video button: if pressed, navigate to VideoCallActivity (not used in voice call)
        val videoButton = findViewById<ImageView>(R.id.ivVideo)
        videoButton.setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImage", profileImageResId)
            startActivity(intent)
        }

        // Check permissions and start voice call
        if (checkPermissions()) {
            initializeAgoraAudioSDK()
            startVoiceCall()
        } else {
            requestPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.apply {
            leaveChannel()
        }
        RtcEngine.destroy()
        mRtcEngine = null
    }
}
