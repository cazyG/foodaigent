package org.xg.project.screen.profile

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.xg.project.presentation.profile.ProfileState

object ProfileBreakpoints {
    /** 双栏桌面布局（平板横屏及以上） */
    val DesktopMin = 720.dp
}

object ProfileColors {
    val PageBackground = Color(0xFFF8F9FB)
    val CardWhite = Color.White
    val FamilyGradientStart = Color(0xFFF5A623)
    val FamilyGradientEnd = Color(0xFFF0883A)
    val HeroOverlay = Color(0x66000000)
    val TagOrangeBg = Color(0xFFFFF3E0)
    val TagBlueBg = Color(0xFFE8F4FD)
    val LogoutBorder = Color(0xFFEF4444)
    val LogoutText = Color(0xFFDC2626)
}

data class ProfileContentUi(
    val userName: String,
    val avatarUrl: String?,
    val familyName: String,
    val healthGuardDays: Int,
    val familySharingEnabled: Boolean,
    val familyMeals: Int,
    val favoriteRecipes: Int,
    val weeklyNewFavorites: Int,
    val healthScore: String,
    val healthScoreHint: String,
    val extraMemberCount: Int,
    val members: List<ProfileMemberUi>,
    val preferences: List<ProfilePreferenceUi>,
    val achievements: List<ProfileAchievementUi>,
)

data class ProfileMemberUi(
    val name: String,
    val dietaryLabel: String,
    val style: MemberDietaryStyle,
)

enum class MemberDietaryStyle {
    LowFat,
    HighProtein,
    Growth,
    AllergenFree,
}

data class ProfilePreferenceUi(
    val label: String,
    val description: String,
    val style: PreferenceStyle,
)

enum class PreferenceStyle {
    Orange,
    Blue,
}

data class ProfileAchievementUi(
    val title: String,
    val style: AchievementStyle,
)

enum class AchievementStyle {
    Chef,
    FamilyChef,
    HealthCheck,
}

fun ProfileState.toProfileContentUi(): ProfileContentUi = ProfileContentUi(
    userName = userName,
    avatarUrl = avatarUrl,
    familyName = familyName,
    healthGuardDays = healthGuardDays,
    familySharingEnabled = familySharingEnabled,
    familyMeals = familyMeals,
    favoriteRecipes = favoriteRecipes,
    weeklyNewFavorites = weeklyNewFavorites,
    healthScore = healthScore,
    healthScoreHint = healthScoreHint,
    extraMemberCount = extraMemberCount,
    members = members,
    preferences = preferences,
    achievements = achievements,
)
