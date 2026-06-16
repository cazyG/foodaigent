package org.xg.project.data.session

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.xg.project.domain.model.User

data class UserAccount(
    val id: Int? = null,
    val displayName: String,
    val tierLabel: String = "",
    val avatarUrl: String? = null,
)

class UserSessionRepository {
    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    fun setLoggedInUser(user: User) {
        _currentUser.value = UserAccount(
            id = user.id,
            displayName = formatDisplayName(user.username),
            tierLabel = "",
        )
    }

    fun setLoggedInUser(username: String) {
        _currentUser.value = UserAccount(
            displayName = formatDisplayName(username),
            tierLabel = "",
        )
    }

    fun clearSession() {
        _currentUser.value = null
    }

    private fun formatDisplayName(username: String): String {
        val trimmed = username.trim()
        if (trimmed.isBlank()) return "黄小厨"
        return trimmed.replaceFirstChar { char ->
            if (char.isLowerCase()) char.titlecaseChar() else char
        }
    }
}
