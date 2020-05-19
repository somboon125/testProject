package th.co.cdg.iconsume.services

import okhttp3.CertificatePinner
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class BaseService {
    companion object Factory {

        private const val TIMEOUT = 10000

        fun create(): ApiInterface {
            val retrofit = Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl("https://oryor.com/oryor2015/")
                    .client(getClient())
                    .build()
            return retrofit.create(ApiInterface::class.java)
        }

        private fun getClient(): OkHttpClient {
            val okHttpClient = UnsafeOkHttpClient.getUnsafeOkHttpClient()
//            val okHttpClient = OkHttpClient.Builder()
            return okHttpClient
//                    .certificatePinner(getDefaultCertificatePinner())
                    .readTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                    .connectTimeout(TIMEOUT.toLong(), TimeUnit.MILLISECONDS)
                    .build()
        }

        private fun getDefaultCertificatePinner(): CertificatePinner {
            return CertificatePinner.Builder().build()
        }

    }

}