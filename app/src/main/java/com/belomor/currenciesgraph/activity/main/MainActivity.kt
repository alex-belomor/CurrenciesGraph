package com.belomor.currenciesgraph.activity.main

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import com.belomor.currenciesgraph.R
import com.belomor.currenciesgraph.activity.main.viewmodel.MainActivityViewModel
import com.belomor.currenciesgraph.data.CurrencyFrankfurtHistoryData
import com.belomor.currenciesgraph.network.api.frankfurter.API
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    val viewModel : MainActivityViewModel by inject()

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        listenToChartData()

        viewModel.getHistoryCurrencies("2019-01-01..2019-09-04")
    }

    private fun listenToChartData() {
        viewModel.chartLiveData.observe(this, Observer {
            chart.setData(it)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.onDestroy()
    }
}
