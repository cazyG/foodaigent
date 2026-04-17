package org.xg.project

import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import org.xg.project.Routes.Routes

@Composable
fun rememberNavBackStack(initialDestination: Routes): SnapshotStateList<Routes> {
    return rememberSaveable(saver = routesStackSaver(defaultRoot = initialDestination)) {
        mutableStateListOf(initialDestination)
    }
}

private fun routesStackSaver(defaultRoot: Routes) = listSaver<SnapshotStateList<Routes>, String>(
    save = { stack -> stack.map { it.id } },
    restore = { ids ->
        val restored = ids.mapNotNull { Routes.fromId(it) }
        mutableStateListOf<Routes>().apply {
            addAll(
                if (restored.isNotEmpty()) restored else listOf(defaultRoot)
            )
        }
    }
)

