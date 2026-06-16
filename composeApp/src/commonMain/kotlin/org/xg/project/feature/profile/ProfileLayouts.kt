package org.xg.project.feature.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

private val HorizontalPadding = 16.dp
private val SectionSpacing = 20.dp

@Composable
fun ProfileCompactLayout(
    content: ProfileContentUi,
    onSettingsClick: () -> Unit,
    onInviteClick: () -> Unit,
    onManageMembersClick: () -> Unit,
    onViewAllAchievementsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(ProfileColors.PageBackground)
            .padding(horizontal = HorizontalPadding)
            .padding(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(SectionSpacing),
    ) {
        ProfileMobileHeader(
            userName = content.userName,
            avatarUrl = content.avatarUrl,
            onSettingsClick = onSettingsClick,
        )
        ProfileFamilyHeroCard(
            content = content,
            onInviteClick = onInviteClick,
            compact = true,
        )
        ProfileStatsRow(content = content, compact = true)
        ProfileMembersSection(
            members = content.members,
            onManageClick = onManageMembersClick,
        )
        ProfilePreferencesSection(preferences = content.preferences)
        ProfileAchievementsSection(
            achievements = content.achievements,
            onViewAllClick = onViewAllAchievementsClick,
        )
        ProfileSettingsSection(
            onNotificationsClick = {},
            onFamilySharingClick = {},
            onPrivacyClick = {},
            onHelpClick = {},
        )
        ProfileLogoutButton(onClick = onLogoutClick)
    }
}

@Composable
fun ProfileDesktopLayout(
    content: ProfileContentUi,
    onInviteClick: () -> Unit,
    onManageMembersClick: () -> Unit,
    onViewAllAchievementsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(ProfileColors.PageBackground)
            .padding(horizontal = 32.dp, vertical = 24.dp)
            .widthIn(max = 1200.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        ProfileDesktopHeroBanner(
            content = content,
            onInviteClick = onInviteClick,
        )
        ProfileStatsRow(content = content, compact = false)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            Column(
                modifier = Modifier.weight(2f),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                ProfileMembersSection(
                    members = content.members,
                    onManageClick = onManageMembersClick,
                    manageLabel = "编辑成员",
                )
                ProfilePreferencesSection(
                    preferences = content.preferences,
                    desktopGrid = true,
                )
            }
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(24.dp),
            ) {
                ProfileAchievementsSection(
                    achievements = content.achievements,
                    onViewAllClick = onViewAllAchievementsClick,
                    showViewAll = true,
                )
                ProfileSettingsSection(
                    onNotificationsClick = {},
                    onFamilySharingClick = {},
                    onPrivacyClick = {},
                    onHelpClick = {},
                    includeFamilySharing = false,
                )
                ProfileLogoutButton(onClick = onLogoutClick)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
    }
}
