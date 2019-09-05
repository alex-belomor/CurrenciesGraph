package com.belomor.currenciesgraph.base.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseAndroidViewModel(application: Application) : AndroidViewModel(application) {

    private val disposables = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        disposables.add(disposable)
    }

    fun onDestroy() {
        if (!disposables.isDisposed)
            disposables.dispose()
    }
}