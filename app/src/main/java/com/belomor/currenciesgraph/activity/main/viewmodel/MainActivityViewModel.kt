package com.belomor.currenciesgraph.activity.main.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.graphics.Color
import android.util.ArrayMap
import androidx.lifecycle.MutableLiveData
import com.belomor.currenciesgraph.base.viewmodel.BaseAndroidViewModel
import com.belomor.currenciesgraph.graph.data.Details
import com.belomor.currenciesgraph.graph.data.Values
import com.belomor.currenciesgraph.network.api.frankfurter.API
import com.google.gson.internal.LinkedTreeMap
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.belomor.currenciesgraph.graph.data.ChartValuesArray


class MainActivityViewModel(application: Application, val api: API) :
    BaseAndroidViewModel(application) {

    val chartLiveData = MutableLiveData<ChartValuesArray>()

    private val arrayMapRandomColors = ArrayMap<String, Int>()

    @SuppressLint("CheckResult")
    fun getHistoryCurrencies(range: String) {
        addDisposable(api.getHistory(range)
            .subscribeOn(Schedulers.io())
            .map { t ->
                val sdf = SimpleDateFormat("yyyy-mm-dd", Locale.US)
                val values = ChartValuesArray()
                val currenciesArrayMap = ArrayMap<String, ArrayList<Details>>()

                val currenciesMap =
                    t.body()?.rates as LinkedTreeMap<String, LinkedTreeMap<String, Double>>

                for ((key, value) in currenciesMap) {
                    val date = sdf.parse(key).time
                    for ((key, value) in value) {
                        if (currenciesArrayMap[key] == null) {
                            currenciesArrayMap[key] = ArrayList<Details>()
                            val rnd = Random()
                            val color = Color.argb(
                                255,
                                rnd.nextInt(256),
                                rnd.nextInt(256),
                                rnd.nextInt(256)
                            )

                            values.add(Values(key, color, currenciesArrayMap[key]!!))
                        }

                        currenciesArrayMap[key]?.add(Details(date, value.toString().toFloat()))
                    }
                }

                values
            }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(object : DisposableSingleObserver<ChartValuesArray>() {
                override fun onSuccess(t: ChartValuesArray) {
                    chartLiveData.postValue(t)
                }

                override fun onError(e: Throwable) {

                }

            })
        )
    }

}