package th.co.cdg.iconsume.ui

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.viewholder_history.view.*
import th.co.cdg.iconsume.R
import th.co.cdg.iconsume.model.SearchData

class HistoryAdapter(val context: Context, var item: MutableList<SearchData>) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    var callback: HistoryOnClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        return HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.viewholder_history, parent, false))
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        if (item[position].isFound) {
            holder.ivCanSearch.setImageResource(R.drawable.ic_check_circle)
        } else {
            holder.ivCanSearch.setImageResource(R.drawable.ic_menu_cancel)
        }

        holder.tvFDACode.text = item[position].numberSearch
        holder.tvProductName.text = item[position].data?.productha ?: ""
        holder.loHistory.setOnClickListener {
            callback?.onHistoryClick(item[position])
        }
    }

    fun deleteItem(position: Int) {
        AlertDialog.Builder(context)
            .setTitle("ยืนยันการลบข้อมูลนี้ ?")
            .setPositiveButton("ยืนยัน") { _, _ ->
                callback?.onHistoryRemove(item[position])
                item.removeAt(position)
                notifyItemRemoved(position)
            }
            .setNegativeButton("ยกเลิก") { _, _ ->
                notifyDataSetChanged()
            }
            .setOnDismissListener {
                notifyDataSetChanged()
            }
            .create()
            .show()
    }

    class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal val ivCanSearch = itemView.ivCanSearch
        internal val ivProduct = itemView.ivProduct
        internal val tvFDACode = itemView.tvFDACode
        internal val tvProductName = itemView.tvProductName
        internal val loHistory = itemView.loHistory
    }

    interface HistoryOnClickListener {
        fun onHistoryClick(data: SearchData)
        fun onHistoryRemove(data: SearchData)
    }

}
