package org.xg.project.core.di

import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.xg.project.data.remote.UploadService
import org.xg.project.data.remote.httpClient
import org.xg.project.data.repository.FoodRepository
import org.xg.project.data.repository.UserRepository
import org.xg.project.data.session.UserSessionRepository
import org.xg.project.core.navigation.AppNavigationCoordinator
import org.xg.project.domain.usecase.CheckMealReviewEligibilityUseCase
import org.xg.project.feature.manualrecipe.usecase.BuildRecipeDraftUseCase
import org.xg.project.feature.manualrecipe.usecase.CreateRecipeUseCase
import org.xg.project.feature.manualrecipe.usecase.UploadImageUseCase
import org.xg.project.feature.history.HistoryViewModel
import org.xg.project.feature.home.HomeViewModel
import org.xg.project.feature.auth.AuthViewModel
import org.xg.project.feature.manualrecipe.ManualRecipeInputViewModel
import org.xg.project.feature.profile.ProfileViewModel
import org.xg.project.feature.recipedetail.RecipeDetailViewModel
import org.xg.project.feature.recipes.RecipesViewModel

val appModule = module {
    // Network
    single { httpClient }
    single { UploadService(get()) }

    // Repository
    single { FoodRepository() }
    single { UserRepository() }
    single { UserSessionRepository() }
    single { AppNavigationCoordinator() }

    // UseCases
    factory { CheckMealReviewEligibilityUseCase() }
    factory { BuildRecipeDraftUseCase() }
    factory { CreateRecipeUseCase(get()) }
    factory { UploadImageUseCase(get()) }

    // ViewModels
    viewModelOf(::HomeViewModel)
    viewModelOf(::HistoryViewModel)
    viewModelOf(::RecipesViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::ManualRecipeInputViewModel)
    viewModelOf(::AuthViewModel)
    viewModelOf(::RecipeDetailViewModel)
}
