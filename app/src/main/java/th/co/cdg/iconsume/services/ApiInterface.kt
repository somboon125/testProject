package th.co.cdg.iconsume.services

import com.google.gson.JsonObject
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ApiInterface {
    @FormUrlEncoded
    @POST("https://oryor.com/oryor2015/ajax-check-product.php")
    fun getDataFromURL(@Field("number_src") number_src: String, @Field("type") type: Int): Observable<JsonObject>
}