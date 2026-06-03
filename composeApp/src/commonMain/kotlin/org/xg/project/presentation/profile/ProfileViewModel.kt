package org.xg.project.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.screen.profile.AchievementStyle
import org.xg.project.screen.profile.MemberDietaryStyle
import org.xg.project.screen.profile.PreferenceStyle
import org.xg.project.screen.profile.ProfileAchievementUi
import org.xg.project.screen.profile.ProfileMemberUi
import org.xg.project.screen.profile.ProfilePreferenceUi

class ProfileViewModel(
    private val userSessionRepository: UserSessionRepository,
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

            _state.value = _state.value.copy(
                isLoading = false,
                userName = displayName,
                avatarUrl = avatarUrl,
                familyName = "${displayName.take(2)}的温馨家园",
                members = defaultMembers(),
                preferences = defaultPreferences(),
                achievements = defaultAchievements(),
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
