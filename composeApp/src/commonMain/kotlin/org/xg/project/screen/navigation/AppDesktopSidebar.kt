package org.xg.project.screen.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.xg.project.Routes.BottomTabRoute
import org.xg.project.data.session.UserAccount
import org.xg.project.screen.home.HomeColors

val AppSidebarWidth = 240.dp

data class AppTabNavItem(
    val label: String,
    val icon: ImageVector,
    val route: BottomTabRoute,
)

val appTabNavItems = listOf(
    AppTabNavItem("首页", Icons.Default.Home, BottomTabRoute.Home),
    AppTabNavItem("食谱库", Icons.Default.Book, BottomTabRoute.Recipes),
    AppTabNavItem("历史", Icons.Default.History, BottomTabRoute.History),
    AppTabNavItem("我的", Icons.Default.Person, BottomTabRoute.Profile),
)

@Composable
fun AppDesktopSidebar(
    activeTab: BottomTabRoute,
    onTabClick: (BottomTabRoute) -> Unit,
    onProfileClick: () -> Unit,
    userAccount: UserAccount?,
    modifier: Modifier = Modifier,
    subtitle: String = "KitchenMaster",
    footerTop: @Composable () -> Unit = {},
) {
    Column(
        modifier = modifier
            .width(AppSidebarWidth)
            .fillMaxHeight()
            .background(HomeColors.CardWhite)
            .padding(horizontal = 20.dp, vertical = 28.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(HomeColors.BrandOrange),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        Icons.Default.Restaurant,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(22.dp),
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "锅铲黄小厨",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = HomeColors.BrandBrown,
                    )
                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = HomeColors.TextSecondary,
                    )
                }
            }
            Spacer(modifier = Modifier.height(28.dp))
            appTabNavItems.forEach { item ->
                AppDesktopSidebarNavItem(
                    label = item.label,
                    icon = item.icon,
                    selected = activeTab == item.route,
                    onClick = { onTabClick(item.route) },
                )
            }
        }
        Column {
            footerTop()
            AppDesktopSidebarUserProfile(
                userAccount = userAccount,
                onClick = onProfileClick,
            )
        }
    }
}

@Composable
fun AppDesktopSidebarUserProfile(
    userAccount: UserAccount?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val displayName = userAccount?.displayName ?: "黄小厨"
    val tierLabel = userAccount?.tierLabel ?: "Pro Tier"
    val avatarUrl = userAccount?.avatarUrl

    Column(modifier = modifier.fillMaxWidth()) {
        HorizontalDivider(color = Color(0xFFE5E7EB))
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .clickable(onClick = onClick)
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(HomeColors.BrandOrange.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center,
            ) {
                if (!avatarUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = displayName,
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop,
                    )
                } else {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = HomeColors.BrandBrown,
                        modifier = Modifier.size(22.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = displayName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HomeColors.TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = tierLabel,
                    fontSize = 12.sp,
                    color = HomeColors.TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun AppDesktopSidebarNavItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
) {
    val bg = if (selected) Color(0xFFF5EDE4) else Color.Transparent
    val indicator = if (selected) HomeColors.BrandBrown else Color.Transparent
    val accent = if (selected) HomeColors.BrandBrown else HomeColors.TextSecondary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(start = 12.dp, end = 8.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (selected) HomeColors.TextPrimary else HomeColors.TextSecondary,
            modifier = Modifier.weight(1f),
        )
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(22.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(indicator),
        )
    }
}
