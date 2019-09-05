package com.belomor.currenciesgraph.activity.main.di

import com.belomor.currenciesgraph.activity.main.viewmodel.MainActivityViewModel
import com.belomor.currenciesgraph.network.api.frankfurter.API
import org.koin.androidx.experimental.dsl.viewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val mainActivityModule = module {
    viewModel {MainActivityViewModel(get(), get())}
}