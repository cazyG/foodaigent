package org.xg.project.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.xg.project.screen.home.HomeColors

private val CardShape = RoundedCornerShape(16.dp)
private val PanelShape = RoundedCornerShape(12.dp)

@Composable
fun ProfileMobileHeader(
    userName: String,
    avatarUrl: String?,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ProfileAvatar(
                avatarUrl = avatarUrl,
                size = 48.dp,
                fallbackTint = HomeColors.BrandBrown,
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = HomeColors.TextPrimary,
            )
        }
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .clickable(onClick = onSettingsClick),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "设置",
                tint = HomeColors.BrandBrown,
                modifier = Modifier.size(24.dp),
            )
        }
    }
}

@Composable
fun ProfileFamilyHeroCard(
    content: ProfileContentUi,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(CardShape)
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        ProfileColors.FamilyGradientStart,
                        ProfileColors.FamilyGradientEnd,
                    ),
                ),
            )
            .padding(20.dp),
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    if (content.familySharingEnabled) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(Color.White.copy(alpha = 0.22f))
                                .padding(horizontal = 10.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Default.Link,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(14.dp),
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "家庭共享模式",
                                fontSize = 12.sp,
                                color = Color.White,
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                    Text(
                        text = content.familyName,
                        fontSize = if (compact) 20.sp else 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "已守护家庭健康 ${content.healthGuardDays} 天",
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.92f),
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                ProfileMemberAvatarStack(
                    members = content.members,
                    extraCount = content.extraMemberCount,
                )
                OutlinedButton(
                    onClick = onInviteClick,
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.White,
                        contentColor = HomeColors.BrandOrange,
                    ),
                    border = null,
                ) {
                    Text("邀请成员", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

@Composable
fun ProfileDesktopHeroBanner(
    content: ProfileContentUi,
    onInviteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFF6B4A32),
                        Color(0xFF8B5E3C),
                        Color(0xFFB87333),
                    ),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(ProfileColors.HeroOverlay),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(HomeColors.BrandOrange.copy(alpha = 0.85f))
                    .padding(horizontal = 12.dp, vertical = 5.dp),
            ) {
                Text(
                    text = "家庭分享模式",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium,
                )
            }
            Column {
                Text(
                    text = content.familyName,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    ProfileMemberAvatarStack(
                        members = content.members,
                        extraCount = content.extraMemberCount,
                    )
                    OutlinedButton(
                        onClick = onInviteClick,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = Color.White.copy(alpha = 0.95f),
                            contentColor = HomeColors.BrandBrown,
                        ),
                        border = null,
                    ) {
                        Text("+ 邀请成员", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileStatsRow(
    content: ProfileContentUi,
    modifier: Modifier = Modifier,
    compact: Boolean = true,
) {
    if (compact) {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            ProfileCompactStatCard(
                value = content.familyMeals.toString(),
                label = "家庭餐食",
                modifier = Modifier.weight(1f),
            )
            ProfileCompactStatCard(
                value = content.favoriteRecipes.toString(),
                label = "最爱菜谱",
                modifier = Modifier.weight(1f),
            )
            ProfileCompactStatCard(
                value = content.healthScore,
                label = "健康评分",
                modifier = Modifier.weight(1f),
            )
        }
    } else {
        Row(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ProfileDesktopStatCard(
                title = "家庭累计用餐",
                value = "${content.familyMeals} 顿",
                subtitle = "本周新增 ${content.weeklyNewFavorites} 顿",
                showProgress = true,
                progress = 0.72f,
                modifier = Modifier.weight(1f),
            )
            ProfileDesktopStatCard(
                title = "最爱食谱",
                value = "${content.favoriteRecipes} 个",
                subtitle = "本周新增 ${content.weeklyNewFavorites} 个热门收藏",
                modifier = Modifier.weight(1f),
            )
            ProfileDesktopStatCard(
                title = "家庭健康评分",
                value = content.healthScore,
                subtitle = content.healthScoreHint,
                highlight = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ProfileCompactStatCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = PanelShape,
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = HomeColors.BrandBrown,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                color = HomeColors.TextSecondary,
            )
        }
    }
}

@Composable
private fun ProfileDesktopStatCard(
    title: String,
    value: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    showProgress: Boolean = false,
    progress: Float = 0f,
    highlight: Boolean = false,
) {
    Card(
        modifier = modifier,
        shape = PanelShape,
        colors = CardDefaults.cardColors(
            containerColor = if (highlight) HomeColors.BrandOrange.copy(alpha = 0.12f) else ProfileColors.CardWhite,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = title,
                fontSize = 14.sp,
                color = HomeColors.TextSecondary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                fontSize = if (highlight) 32.sp else 26.sp,
                fontWeight = FontWeight.Bold,
                color = if (highlight) HomeColors.BrandOrange else HomeColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = subtitle,
                fontSize = 12.sp,
                color = HomeColors.TextSecondary,
                lineHeight = 16.sp,
            )
            if (showProgress) {
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = HomeColors.BrandOrange,
                    trackColor = Color(0xFFE5E7EB),
                )
            }
        }
    }
}

@Composable
fun ProfileMembersSection(
    members: List<ProfileMemberUi>,
    onManageClick: () -> Unit,
    modifier: Modifier = Modifier,
    manageLabel: String = "管理",
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ProfileSectionHeader(
            title = "家庭成员",
            actionLabel = manageLabel,
            onActionClick = onManageClick,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = PanelShape,
            colors = CardDefaults.cardColors(containerColor = ProfileColors.CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
            ) {
                members.forEach { member ->
                    ProfileMemberItem(member = member)
                }
            }
        }
    }
}

@Composable
private fun ProfileMemberItem(member: ProfileMemberUi) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            ProfileAvatar(
                avatarUrl = null,
                size = 52.dp,
                backgroundColor = member.style.backgroundColor(),
            )
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = member.style.icon(),
                    contentDescription = null,
                    tint = member.style.accentColor(),
                    modifier = Modifier.size(12.dp),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = member.name,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = HomeColors.TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = member.dietaryLabel,
            fontSize = 10.sp,
            color = member.style.accentColor(),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfilePreferencesSection(
    preferences: List<ProfilePreferenceUi>,
    modifier: Modifier = Modifier,
    desktopGrid: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ProfileSectionHeader(title = "饮食偏好")
        Spacer(modifier = Modifier.height(12.dp))
        if (desktopGrid) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                preferences.forEach { preference ->
                    ProfileDesktopPreferenceCard(
                        preference = preference,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        } else {
            Card(
                shape = PanelShape,
                colors = CardDefaults.cardColors(containerColor = ProfileColors.CardWhite),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                FlowRow(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    preferences.forEach { preference ->
                        ProfilePreferenceTag(preference = preference)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfilePreferenceTag(preference: ProfilePreferenceUi) {
    val bg = if (preference.style == PreferenceStyle.Blue) {
        ProfileColors.TagBlueBg
    } else {
        ProfileColors.TagOrangeBg
    }
    val tint = if (preference.style == PreferenceStyle.Blue) {
        Color(0xFF3B82F6)
    } else {
        HomeColors.BrandOrange
    }
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = preference.style.icon(),
            contentDescription = null,
            tint = tint,
            modifier = Modifier.size(16.dp),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = preference.label,
            fontSize = 13.sp,
            color = HomeColors.TextPrimary,
        )
    }
}

@Composable
private fun ProfileDesktopPreferenceCard(
    preference: ProfilePreferenceUi,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        shape = PanelShape,
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            Icon(
                imageVector = preference.style.icon(),
                contentDescription = null,
                tint = if (preference.style == PreferenceStyle.Blue) {
                    Color(0xFF3B82F6)
                } else {
                    HomeColors.BrandOrange
                },
                modifier = Modifier.size(22.dp),
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = preference.label,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = HomeColors.TextPrimary,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = preference.description,
                fontSize = 12.sp,
                color = HomeColors.TextSecondary,
                lineHeight = 16.sp,
            )
        }
    }
}

@Composable
fun ProfileAchievementsSection(
    achievements: List<ProfileAchievementUi>,
    onViewAllClick: () -> Unit,
    modifier: Modifier = Modifier,
    showViewAll: Boolean = false,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        ProfileSectionHeader(
            title = if (showViewAll) "家庭荣誉榜" else "家庭成就",
            actionLabel = if (showViewAll) null else null,
            onActionClick = {},
        )
        Spacer(modifier = Modifier.height(12.dp))
        Card(
            shape = PanelShape,
            colors = CardDefaults.cardColors(containerColor = ProfileColors.CardWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (showViewAll) {
                Column(modifier = Modifier.padding(16.dp)) {
                    achievements.forEachIndexed { index, achievement ->
                        ProfileAchievementListItem(achievement = achievement)
                        if (index < achievements.lastIndex) {
                            HorizontalDivider(
                                color = Color(0xFFF3F4F6),
                                modifier = Modifier.padding(vertical = 8.dp),
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = onViewAllClick,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                    ) {
                        Text("查看全部成就", fontSize = 13.sp)
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp, horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    achievements.forEach { achievement ->
                        ProfileAchievementBadge(achievement = achievement)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileAchievementBadge(achievement: ProfileAchievementUi) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(HomeColors.BrandOrange.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = achievement.style.icon(),
                contentDescription = null,
                tint = HomeColors.BrandOrange,
                modifier = Modifier.size(28.dp),
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = achievement.title,
            fontSize = 12.sp,
            color = HomeColors.TextPrimary,
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun ProfileAchievementListItem(achievement: ProfileAchievementUi) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(HomeColors.BrandOrange.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = achievement.style.icon(),
                contentDescription = null,
                tint = HomeColors.BrandOrange,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = achievement.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = HomeColors.TextPrimary,
        )
    }
}

@Composable
fun ProfileSettingsSection(
    onNotificationsClick: () -> Unit,
    onFamilySharingClick: () -> Unit,
    onPrivacyClick: () -> Unit,
    onHelpClick: () -> Unit,
    modifier: Modifier = Modifier,
    includeFamilySharing: Boolean = true,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = PanelShape,
        colors = CardDefaults.cardColors(containerColor = ProfileColors.CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(vertical = 4.dp)) {
            ProfileSettingsItem(
                icon = Icons.Default.Notifications,
                title = "通知设置",
                onClick = onNotificationsClick,
            )
            if (includeFamilySharing) {
                ProfileSettingsItem(
                    icon = Icons.Default.Share,
                    title = "家庭共享",
                    onClick = onFamilySharingClick,
                )
            }
            ProfileSettingsItem(
                icon = Icons.Default.Security,
                title = "隐私与安全",
                onClick = onPrivacyClick,
            )
            ProfileSettingsItem(
                icon = Icons.AutoMirrored.Filled.Help,
                title = "帮助与反馈",
                onClick = onHelpClick,
            )
        }
    }
}

@Composable
private fun ProfileSettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomeColors.TextSecondary,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontSize = 15.sp,
            color = HomeColors.TextPrimary,
            modifier = Modifier.weight(1f),
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = HomeColors.TextSecondary.copy(alpha = 0.6f),
            modifier = Modifier.size(14.dp),
        )
    }
}

@Composable
fun ProfileLogoutButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, ProfileColors.LogoutBorder),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = ProfileColors.CardWhite,
            contentColor = ProfileColors.LogoutText,
        ),
    ) {
        Text(
            text = "退出登录",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun ProfileSectionHeader(
    title: String,
    actionLabel: String? = null,
    onActionClick: () -> Unit = {},
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = title,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            color = HomeColors.TextPrimary,
        )
        if (actionLabel != null) {
            Text(
                text = actionLabel,
                fontSize = 13.sp,
                color = HomeColors.BrandOrange,
                modifier = Modifier.clickable(onClick = onActionClick),
            )
        }
    }
}

@Composable
private fun ProfileAvatar(
    avatarUrl: String?,
    size: androidx.compose.ui.unit.Dp,
    backgroundColor: Color = HomeColors.BrandOrange.copy(alpha = 0.22f),
    fallbackTint: Color = HomeColors.BrandBrown,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = fallbackTint,
                modifier = Modifier.size(size * 0.5f),
            )
        }
    }
}

@Composable
private fun ProfileMemberAvatarStack(
    members: List<ProfileMemberUi>,
    extraCount: Int,
) {
    val visibleMembers = members.take(2)
    Row {
        visibleMembers.forEachIndexed { index, member ->
            Box(
                modifier = Modifier
                    .offset(x = (-10 * index).dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .padding(2.dp),
            ) {
                ProfileAvatar(
                    avatarUrl = null,
                    size = 32.dp,
                    backgroundColor = member.style.backgroundColor(),
                )
            }
        }
        if (extraCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = (-10 * visibleMembers.size).dp)
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "+$extraCount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = HomeColors.BrandBrown,
                )
            }
        }
    }
}

private fun MemberDietaryStyle.backgroundColor(): Color = when (this) {
    MemberDietaryStyle.LowFat -> Color(0xFFE8F5E9)
    MemberDietaryStyle.HighProtein -> Color(0xFFFFF3E0)
    MemberDietaryStyle.Growth -> Color(0xFFE3F2FD)
    MemberDietaryStyle.AllergenFree -> Color(0xFFFCE4EC)
}

private fun MemberDietaryStyle.accentColor(): Color = when (this) {
    MemberDietaryStyle.LowFat -> Color(0xFF4CAF50)
    MemberDietaryStyle.HighProtein -> Color(0xFFFF9800)
    MemberDietaryStyle.Growth -> Color(0xFF2196F3)
    MemberDietaryStyle.AllergenFree -> Color(0xFFE91E63)
}

private fun MemberDietaryStyle.icon(): ImageVector = when (this) {
    MemberDietaryStyle.LowFat -> Icons.Default.Spa
    MemberDietaryStyle.HighProtein -> Icons.Default.Restaurant
    MemberDietaryStyle.Growth -> Icons.Default.ChildCare
    MemberDietaryStyle.AllergenFree -> Icons.Default.Eco
}

private fun PreferenceStyle.icon(): ImageVector = when (this) {
    PreferenceStyle.Orange -> Icons.Default.LocalFireDepartment
    PreferenceStyle.Blue -> Icons.Default.Eco
}

private fun AchievementStyle.icon(): ImageVector = when (this) {
    AchievementStyle.Chef -> Icons.Default.EmojiEvents
    AchievementStyle.FamilyChef -> Icons.Default.Restaurant
    AchievementStyle.HealthCheck -> Icons.Default.CalendarMonth
}
