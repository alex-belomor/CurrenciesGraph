package com.belomor.currenciesgraph.network.api.frankfurter

import com.belomor.currenciesgraph.data.CurrencyFrankfurtHistoryData
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.*

interface API {

    @GET("{part}")
    fun getHistory(@Path("part") range : String) : Single<Response<CurrencyFrankfurtHistoryData>>
}