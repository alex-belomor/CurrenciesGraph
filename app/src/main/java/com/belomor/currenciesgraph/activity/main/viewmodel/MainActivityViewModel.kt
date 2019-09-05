package com.belomor.currenciesgraph.activity.main.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.belomor.currenciesgraph.base.viewmodel.BaseAndroidViewModel
import com.belomor.currenciesgraph.graph.data.Dates
import com.belomor.currenciesgraph.graph.data.Values
import com.belomor.currenciesgraph.network.api.frankfurter.API
import com.google.gson.internal.LinkedTreeMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class MainActivityViewModel(application: Application, val api: API) :
    BaseAndroidViewModel(application) {

    val chartLiveData = MutableLiveData<ArrayList<Dates>>()

    @SuppressLint("CheckResult")
    fun getHistoryCurrencies(range: String) {
        addDisposable(api.getHistory(range)
            .subscribeOn(Schedulers.io())
            .map { t ->
                val sdf = SimpleDateFormat("yyyy-mm-dd", Locale.US)
                val dates = ArrayList<Dates>()
                val currenciesMap =
                    t.body()?.rates as LinkedTreeMap<String, LinkedTreeMap<String, Double>>
                for ((key, value) in currenciesMap) {
                    val values = ArrayList<Values>()
                    for ((key, value) in value) {
                        values.add(Values(key, value))
                    }
                    dates.add(Dates(sdf.parse(key).time, values))
                }
                dates
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ArrayList<Dates>>() {
                override fun onSuccess(t: ArrayList<Dates>) {
                    chartLiveData.postValue(t)
                }

                override fun onError(e: Throwable) {

                }

            })
        )
    }

}