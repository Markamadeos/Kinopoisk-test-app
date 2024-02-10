package com.example.kinopoisk_test_app.di

import com.example.kinopoisk_test_app.presentation.viewModels.DetailsViewModel
import com.example.kinopoisk_test_app.presentation.viewModels.FavoriteViewModel
import com.example.kinopoisk_test_app.presentation.viewModels.PopularViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel {
        PopularViewModel(searchInteractor = get())
    }

    viewModel {
        DetailsViewModel(searchInteractor = get())
    }

    viewModel {
        FavoriteViewModel(favoriteInteractor = get())
    }
}