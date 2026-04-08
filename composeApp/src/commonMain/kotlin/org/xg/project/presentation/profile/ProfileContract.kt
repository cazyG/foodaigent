package org.xg.project.presentation.profile


data class ProfileState(
    val isLoading: Boolean = false,
    val userName: String = "美食探索家",
    val avatarUrl: String? = null,
    val bio: String = "热爱美食，分享快乐",
    val totalOrders: Int = 86,
    val totalReviews: Int = 42,
    val averageStars: Float = 4.6f,
    val tasteRadarData: Map<String, Float> = emptyMap()
)

sealed class ProfileIntent {
    data class UpdateUserInfo(val name: String, val bio: String) : ProfileIntent()
    object LoadUserProfile : ProfileIntent()
}