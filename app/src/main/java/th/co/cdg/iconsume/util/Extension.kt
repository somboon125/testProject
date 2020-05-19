package th.co.cdg.iconsume.util

import androidx.fragment.app.Fragment
import th.co.cdg.iconsume.MainActivity

fun Fragment.showLoading() {
    (activity as? MainActivity)?.showLoading()
}

fun Fragment.hideLoading() {
    (activity as? MainActivity)?.hideLoading()
}

fun Fragment.hideKeyboard() {
    (activity as? MainActivity)?.hideKeyboard()
}