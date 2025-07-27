package com.example.connectme

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.rtc2.*

class CallActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQ_ID = 22
    }

    private val myAppId = "0c35b0f5e5544c9f90c74fa958a5f931"
    private val channelName = "ConnectMe"
    private val token = "007eJxTYDA8PsmCt6lzp858/3mvku7un3tgafXzp5cXOTk5fP5cpj1bgcEg2dg0ySDNNNXU1MQk2TLN0iDZ3CQt0dLUItE0zdLYUKmMI6MhkJHBf18oAyMUgvicDM75eXmpySW+qQwMAHvYIk0="
    private var mRtcEngine: RtcEngine? = null

    private val mRtcEventHandler = object : IRtcEngineEventHandler() {
        override fun onJoinChannelSuccess(channel: String?, uid: Int, elapsed: Int) {
            runOnUiThread {
                Toast.makeText(this@CallActivity, "Joined channel $channel", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onUserJoined(uid: Int, elapsed: Int) {
            runOnUiThread {
                Toast.makeText(this@CallActivity, "User joined: $uid", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            runOnUiThread {
                Toast.makeText(this@CallActivity, "User offline: $uid", Toast.LENGTH_SHORT).show()
            }
        }
    }

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
        mRtcEngine?.enableAudio()
        val options = ChannelMediaOptions().apply {
            clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
            channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
            publishMicrophoneTrack = true
            publishCameraTrack = false
        }
        mRtcEngine?.joinChannel(token, channelName, 0, options)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        // Get user info from intent
        val userName = intent.getStringExtra("userName") ?: "Unknown"
        val profileBase64 = intent.getStringExtra("profileImageBase64").orEmpty()

        findViewById<TextView>(R.id.tvUserName).text = userName
        val ivProfile = findViewById<ImageView>(R.id.ivProfilePicture)

        if (profileBase64.isNotEmpty()) {
            try {
                val decodedBytes = Base64.decode(profileBase64, Base64.DEFAULT)
                val bitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.size)
                ivProfile.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
                ivProfile.setImageResource(R.drawable.circle_background)
            }
        } else {
            ivProfile.setImageResource(R.drawable.circle_background)
        }

        findViewById<ImageView>(R.id.ivEndCall).setOnClickListener { finish() }

        findViewById<ImageView>(R.id.ivMute).setOnClickListener {
            mRtcEngine?.muteLocalAudioStream(true)
            Toast.makeText(this, "Muted", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.ivSpeaker).setOnClickListener {
            mRtcEngine?.setEnableSpeakerphone(true)
            Toast.makeText(this, "Speaker enabled", Toast.LENGTH_SHORT).show()
        }

        findViewById<ImageView>(R.id.ivVideo).setOnClickListener {
            val intent = Intent(this, VideoCallActivity::class.java)
            intent.putExtra("userName", userName)
            intent.putExtra("profileImageBase64", profileBase64)
            startActivity(intent)
        }

        if (checkPermissions()) {
            initializeAgoraAudioSDK()
            startVoiceCall()
        } else {
            requestPermissions()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mRtcEngine?.leaveChannel()
        RtcEngine.destroy()
        mRtcEngine = null
    }
}
