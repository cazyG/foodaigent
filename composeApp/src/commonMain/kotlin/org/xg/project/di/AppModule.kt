package org.xg.project.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.xg.project.data.remote.UploadService
import org.xg.project.data.remote.httpClient
import org.xg.project.data.repository.FoodRepository
import org.xg.project.domain.usecase.CheckMealReviewEligibilityUseCase
import org.xg.project.presentation.manualrecipeinput.usecase.BuildRecipeDraftUseCase
import org.xg.project.presentation.manualrecipeinput.usecase.CreateRecipeUseCase
import org.xg.project.presentation.manualrecipeinput.usecase.UploadImageUseCase
import org.xg.project.presentation.history.HistoryViewModel
import org.xg.project.presentation.index.IndexViewModel
import org.xg.project.presentation.login.LoginViewModel
import org.xg.project.presentation.manualrecipeinput.ManualRecipeInputViewModel
import org.xg.project.presentation.profile.ProfileViewModel
import org.xg.project.presentation.recipes.RecipesViewModel

val appModule = module {
    // Network
    single { httpClient }
    single { UploadService(get()) }

    // Repository
    single { FoodRepository() }

    // UseCases
    factory { CheckMealReviewEligibilityUseCase() }
    factory { BuildRecipeDraftUseCase() }
    factory { CreateRecipeUseCase(get()) }
    factory { UploadImageUseCase(get()) }

    // ViewModels
    viewModelOf(::IndexViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::RecipesViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ManualRecipeInputViewModel)
    viewModelOf(::LoginViewModel)
}
