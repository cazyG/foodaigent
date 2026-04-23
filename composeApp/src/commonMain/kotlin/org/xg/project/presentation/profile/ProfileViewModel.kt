package org.xg.project.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.repository.FoodRepository

class ProfileViewModel(
    private val repository: FoodRepository = FoodRepository()
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.UpdateUserInfo -> updateUserInfo(intent.name, intent.bio)
            ProfileIntent.LoadUserProfile -> loadUserProfile()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            // 模拟网络请求，实际项目中从 Repository 获取数据
            kotlinx.coroutines.delay(500)
            
            val radarData = when (val result = repository.fetchTasteRadar()) {
                is org.xg.project.domain.Result.Success -> result.data
                is org.xg.project.domain.Result.Error -> emptyMap()
            }
            
            _state.value = _state.value.copy(
                isLoading = false,
                userName = "美食探索家",
                bio = "热爱美食，分享快乐",
                totalOrders = 86,
                totalReviews = 42,
                averageStars = 4.6f,
                tasteRadarData = radarData
            )
        }
    }

    private fun updateUserInfo(name: String, bio: String) {
        viewModelScope.launch {
            // 模拟更新用户信息
            _state.value = _state.value.copy(userName = name, bio = bio)
        }
    }

    init {
        loadUserProfile()
    }
}