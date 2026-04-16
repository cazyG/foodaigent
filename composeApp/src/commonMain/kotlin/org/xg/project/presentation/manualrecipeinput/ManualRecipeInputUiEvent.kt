package org.xg.project.presentation.manualrecipeinput

sealed class ManualRecipeInputUiEvent {
    data object SaveSuccess : ManualRecipeInputUiEvent()
}
