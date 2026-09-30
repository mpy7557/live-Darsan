package com.example.service

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class WebRtcState {
    DISCONNECTED,
    CONNECTING,
    EXCHANGING_SDP,
    GATHERING_ICE,
    CONNECTED
}

data class CallStats(
    val bitrateKbps: Int = 2480,
    val resolution: String = "1920x1080 (HD)",
    val fps: Int = 60,
    val latencyMs: Int = 38,
    val iceCandidateType: String = "host (P2P Direct)"
)

class WebRtcSignalingEngine(private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default)) {

    private val _connectionState = MutableStateFlow(WebRtcState.DISCONNECTED)
    val connectionState: StateFlow<WebRtcState> = _connectionState.asStateFlow()

    private val _callStats = MutableStateFlow(CallStats())
    val callStats: StateFlow<CallStats> = _callStats.asStateFlow()

    private val _isMicMuted = MutableStateFlow(false)
    val isMicMuted: StateFlow<Boolean> = _isMicMuted.asStateFlow()

    private val _isVideoMuted = MutableStateFlow(false)
    val isVideoMuted: StateFlow<Boolean> = _isVideoMuted.asStateFlow()

    private val _isSanctumAudioActive = MutableStateFlow(true)
    val isSanctumAudioActive: StateFlow<Boolean> = _isSanctumAudioActive.asStateFlow()

    private val _isFrontCamera = MutableStateFlow(false)
    val isFrontCamera: StateFlow<Boolean> = _isFrontCamera.asStateFlow()

    fun startCall(channelId: String) {
        scope.launch {
            _connectionState.value = WebRtcState.CONNECTING
            delay(400)
            _connectionState.value = WebRtcState.EXCHANGING_SDP
            delay(500)
            _connectionState.value = WebRtcState.GATHERING_ICE
            delay(400)
            _connectionState.value = WebRtcState.CONNECTED
        }
    }

    fun endCall() {
        _connectionState.value = WebRtcState.DISCONNECTED
    }

    fun toggleMic() {
        _isMicMuted.value = !_isMicMuted.value
    }

    fun toggleVideo() {
        _isVideoMuted.value = !_isVideoMuted.value
    }

    fun toggleSanctumAudio() {
        _isSanctumAudioActive.value = !_isSanctumAudioActive.value
    }

    fun switchCamera() {
        _isFrontCamera.value = !_isFrontCamera.value
    }
}
