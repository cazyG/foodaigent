package org.xg.project.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.xg.project.presentation.profile.ProfileViewModel
import androidx.compose.material3.Scaffold

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(GlassStyle.BgGradient)
                .padding(innerPadding)
                .verticalScroll(scrollState)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            UserInfoCard(
                name = state.userName,
                avatarUrl = state.avatarUrl,
                bio = state.bio,
                onEditClick = { /* 跳转编辑资料页面 */ }
            )

            Spacer(modifier = Modifier.height(16.dp))

            StatsCard(
                totalOrders = state.totalOrders,
                totalReviews = state.totalReviews,
                averageStars = state.averageStars
            )

            Spacer(modifier = Modifier.height(16.dp))

            PreferenceCard(
                tasteRadarData = state.tasteRadarData
            )

            Spacer(modifier = Modifier.height(16.dp))

            SettingsList()

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { /* 处理登出逻辑 */ },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.20f),
                    contentColor = GlassStyle.Danger
                ),
                shape = RoundedCornerShape(32.dp)
            ) {
                Text("退出登录", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun UserInfoCard(
    name: String,
    avatarUrl: String?,
    bio: String,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .glassPanelStrong(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        GlassHighlight {
            Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.16f))
            ) {
                if (avatarUrl != null) {
                    AsyncImage(
                        model = avatarUrl,
                        contentDescription = "用户头像",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        tint = Color(0xFF9CA3AF)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 昵称和简介
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = GlassStyle.TextPrimary
                )
                Text(
                    text = bio,
                    fontSize = 13.sp,
                    color = GlassStyle.TextSecondary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            // 编辑按钮
            Button(
                onClick = onEditClick,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White.copy(alpha = 0.18f),
                    contentColor = GlassStyle.TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "编辑",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("编辑", fontSize = 13.sp)
            }
        }
        }
    }
}

@Composable
fun StatsCard(
    totalOrders: Int,
    totalReviews: Int,
    averageStars: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .glassPanel(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(
                value = totalOrders.toString(),
                label = "累计点餐",
                color = GlassStyle.TextPrimary
            )
            StatItem(
                value = totalReviews.toString(),
                label = "评价次数",
                color = GlassStyle.TextPrimary
            )
            StatItem(
                value = ((averageStars * 10).toInt() / 10f).toString(),
                label = "平均星级",
                color = GlassStyle.TextPrimary
            )
        }
    }
}

@Composable
fun StatItem(
    value: String,
    label: String,
    color: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Text(
            text = label,
            fontSize = 13.sp,
            color = GlassStyle.TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
fun PreferenceCard(
    tasteRadarData: Map<String, Float>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .glassPanel(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = "饮食偏好",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = GlassStyle.TextPrimary
            )

            if (tasteRadarData.isNotEmpty()) {
                Spacer(Modifier.height(16.dp))
                RadarChart(
                    data = tasteRadarData,
                    modifier = Modifier.fillMaxWidth().height(200.dp)
                )
            } else {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "暂无偏好设置，去添加",
                    fontSize = 14.sp,
                    color = GlassStyle.TextSecondary
                )
            }
        }
    }
}

@Composable
fun SettingsList() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .glassPanel(RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            SettingsItem(
                icon = Icons.Default.Notifications,
                title = "通知设置",
                onClick = { /* 跳转通知设置 */ }
            )
            SettingsItem(
                icon = Icons.Default.Security,
                title = "隐私与安全",
                onClick = { /* 跳转隐私设置 */ }
            )
            SettingsItem(
                icon = Icons.Default.Settings,
                title = "通用设置",
                onClick = { /* 跳转通用设置 */ }
            )
        }
    }
}

@Composable
fun SettingsItem(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GlassStyle.TextPrimary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = GlassStyle.TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = GlassStyle.TextSecondary,
            modifier = Modifier.size(16.dp)
        )
    }
}
