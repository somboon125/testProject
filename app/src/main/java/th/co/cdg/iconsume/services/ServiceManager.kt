package th.co.cdg.iconsume.services

import com.google.gson.JsonObject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

object ServiceManager {
    private fun instance(): ApiInterface {
        return BaseService.create()
    }

    fun checkOryor(text: String): Observable<JsonObject> {
        return instance().getDataFromURL(text, 0)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}