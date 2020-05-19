package th.co.cdg.iconsume.model

import com.google.gson.annotations.SerializedName

data class ProductOutput(
    @SerializedName("IDA") val IDA: String,
    @SerializedName("typepro") val typepro: String,
    @SerializedName("typeallow") val typeallow: String,
    @SerializedName("lcnno") val lcnno: String,
    @SerializedName("productha") val productha: String,
    @SerializedName("produceng") val produceng: String,
    @SerializedName("licen") val licen: String,
    @SerializedName("thanm") val thanm: String,
    @SerializedName("NewCode") val NewCode: String,
    @SerializedName("cncnm") val cncnm: String,
    @SerializedName("Addr") val Addr: String,
    @SerializedName("URLs") val URLs: String,
    @SerializedName("type") val type: Int
)