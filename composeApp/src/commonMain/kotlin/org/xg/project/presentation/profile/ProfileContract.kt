package org.xg.project.presentation.profile

import org.xg.project.screen.profile.ProfileAchievementUi
import org.xg.project.screen.profile.ProfileMemberUi
import org.xg.project.screen.profile.ProfilePreferenceUi

data class ProfileState(
    val isLoading: Boolean = false,
    val userName: String = "锅铲黄小厨",
    val avatarUrl: String? = null,
    val familyName: String = "黄小厨的温馨家园",
    val healthGuardDays: Int = 328,
    val familySharingEnabled: Boolean = true,
    val familyMeals: Int = 128,
    val favoriteRecipes: Int = 24,
    val weeklyNewFavorites: Int = 3,
    val healthScore: String = "A+",
    val healthScoreHint: String = "营养均衡度超过 95% 的家庭",
    val extraMemberCount: Int = 2,
    val members: List<ProfileMemberUi> = emptyList(),
    val preferences: List<ProfilePreferenceUi> = emptyList(),
    val achievements: List<ProfileAchievementUi> = emptyList(),
)

sealed class ProfileIntent {
    data class UpdateUserInfo(val name: String, val bio: String) : ProfileIntent()
    data object LoadUserProfile : ProfileIntent()
    data object Logout : ProfileIntent()
}
