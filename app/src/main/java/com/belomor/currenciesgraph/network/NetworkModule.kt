package com.belomor.currenciesgraph.network

import com.belomor.currenciesgraph.data.CurrencyFrankfurtHistoryData
import com.belomor.currenciesgraph.network.api.frankfurter.API
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

private val frankfurtBaseURL = "https://frankfurter.app/"

val networkModule = module {
    single<OkHttpClient>{ provideDefaultOkHttpClient()}
    single<Retrofit>{ provideITunesRetrofit(get())}
    single<API>{ provideITunesAPI(get())}
}

fun provideHttpLoggingInterceptor() : HttpLoggingInterceptor {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
    return loggingInterceptor
}

internal class GoogleAPIInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        return chain.proceed(chain.request())
    }
}

fun provideDefaultOkHttpClient() : OkHttpClient {
    return OkHttpClient.Builder()
        .addInterceptor(provideHttpLoggingInterceptor())
        .addInterceptor(GoogleAPIInterceptor())
        .build()
}

fun provideITunesRetrofit(client: OkHttpClient) : Retrofit {
    return Retrofit.Builder()
        .baseUrl(frankfurtBaseURL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
}

fun provideITunesAPI(retrofit: Retrofit) : API = retrofit.create(API::class.java)