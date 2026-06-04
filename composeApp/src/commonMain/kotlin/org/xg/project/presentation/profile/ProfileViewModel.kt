package org.xg.project.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.model.ResponseResult
import org.xg.project.data.repository.FoodRepository
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.screen.profile.AchievementStyle
import org.xg.project.screen.profile.MemberDietaryStyle
import org.xg.project.screen.profile.PreferenceStyle
import org.xg.project.screen.profile.ProfileAchievementUi
import org.xg.project.screen.profile.ProfileMemberUi
import org.xg.project.screen.profile.ProfilePreferenceUi

class ProfileViewModel(
    private val userSessionRepository: UserSessionRepository,
    private val foodRepository: FoodRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            is ProfileIntent.UpdateUserInfo -> updateUserInfo(intent.name)
            ProfileIntent.LoadUserProfile -> loadUserProfile()
            ProfileIntent.Logout -> logout()
        }
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val displayName = userSessionRepository.currentUser.value?.displayName ?: "锅铲黄小厨"
            val avatarUrl = userSessionRepository.currentUser.value?.avatarUrl

            val recipeCount = when (val result = foodRepository.getAllRecipe()) {
                is ResponseResult.Success -> result.data.size
                is ResponseResult.Error -> _state.value.favoriteRecipes
                is ResponseResult.Loading -> _state.value.favoriteRecipes
            }

            val preferences = when (val radarResult = foodRepository.fetchTasteRadar()) {
                is org.xg.project.domain.Result.Success -> tasteRadarToPreferences(radarResult.data)
                is org.xg.project.domain.Result.Error -> defaultPreferences()
            }

            _state.value = _state.value.copy(
                isLoading = false,
                userName = displayName,
                avatarUrl = avatarUrl,
                familyName = "${displayName.take(2)}的温馨家园",
                favoriteRecipes = recipeCount,
                members = defaultMembers(),
                preferences = preferences,
                achievements = defaultAchievements(),
            )
        }
    }

    private fun tasteRadarToPreferences(radar: Map<String, Float>): List<ProfilePreferenceUi> {
        if (radar.isEmpty()) return defaultPreferences()
        return radar.entries
            .sortedByDescending { it.value }
            .take(4)
            .mapIndexed { index, (label, value) ->
                ProfilePreferenceUi(
                    label = label,
                    description = "偏好强度 ${(value * 100).toInt()}%",
                    style = if (index % 2 == 0) PreferenceStyle.Orange else PreferenceStyle.Blue,
                )
            }
    }

    private fun updateUserInfo(name: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(userName = name)
        }
    }

    private fun logout() {
        userSessionRepository.clearSession()
    }

    init {
        loadUserProfile()
    }

    private fun defaultMembers(): List<ProfileMemberUi> = listOf(
        ProfileMemberUi("妈妈", "低脂饮食", MemberDietaryStyle.LowFat),
        ProfileMemberUi("爸爸", "高蛋白", MemberDietaryStyle.HighProtein),
        ProfileMemberUi("小明", "成长餐", MemberDietaryStyle.Growth),
        ProfileMemberUi("小红", "过敏原回避", MemberDietaryStyle.AllergenFree),
    )

    private fun defaultPreferences(): List<ProfilePreferenceUi> = listOf(
        ProfilePreferenceUi("低盐低糖", "全家口味更清淡健康", PreferenceStyle.Orange),
        ProfilePreferenceUi("儿童友好", "适合孩子的成长食谱", PreferenceStyle.Orange),
        ProfilePreferenceUi("营养均衡", "每日搭配更科学", PreferenceStyle.Orange),
        ProfilePreferenceUi("微辣适中", "兼顾大人与孩子的口味", PreferenceStyle.Blue),
    )

    private fun defaultAchievements(): List<ProfileAchievementUi> = listOf(
        ProfileAchievementUi("烹饪大师", AchievementStyle.Chef),
        ProfileAchievementUi("家庭大厨", AchievementStyle.FamilyChef),
        ProfileAchievementUi("健康周打卡", AchievementStyle.HealthCheck),
    )
}
