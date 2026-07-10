package com.yjp.functions.ui.youtube

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yjp.functions.data.remote.result.FunctionsResult
import com.yjp.functions.data.model.YoutubeVideoInfo
import com.yjp.functions.data.repository.YoutubeRepository
import com.yjp.functions.util.FunctionsLog
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class YoutubeViewModel @Inject constructor(
    private val youtubeRepository: YoutubeRepository,
) : ViewModel() {

    private val videoIds = listOf(
        "vXiBTHJI1SY",
        "wbWudic8YkQ",
        "mSgRjI1VL_I",
        "k-5fCBEpi4I",
        "MA2FlDmw88o"
    )

    private val _videos = MutableStateFlow<List<YoutubeVideoInfo>>(emptyList())
    val videos: StateFlow<List<YoutubeVideoInfo>> = _videos.asStateFlow()

    init {
        loadVideos()
    }

    fun loadVideos() {
        viewModelScope.launch {
            when (val result = youtubeRepository.getVideos(videoIds)) {
                is FunctionsResult.Success -> {
                    _videos.value = result.data
                    FunctionsLog.d("YouTube API 성공: ${result.data.size}건")
                }
                is FunctionsResult.Fail -> {
                    _videos.value = emptyList()
                    FunctionsLog.e("YouTube API 요청 실패: ${result.message}", result.throwable)
                }
            }
        }
    }
}
