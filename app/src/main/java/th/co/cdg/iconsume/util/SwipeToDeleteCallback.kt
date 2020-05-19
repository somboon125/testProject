package th.co.cdg.iconsume.util

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import th.co.cdg.iconsume.R
import th.co.cdg.iconsume.ui.HistoryAdapter
import kotlin.math.roundToInt

class SwipeToDeleteCallback(adapter: HistoryAdapter) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val historyAdapter: HistoryAdapter = adapter
    private val icon = ContextCompat.getDrawable(historyAdapter.context, R.drawable.ic_delete_white_36)
    private val background = ColorDrawable(Color.RED)

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        historyAdapter.deleteItem(position)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        val itemView = viewHolder.itemView
        val backgroundCornerOffset = 20

        val iconMargin = (itemView.height - icon!!.intrinsicHeight) / 2
        val iconTop = itemView.top + (itemView.height - icon.intrinsicHeight) / 2
        val iconBottom = iconTop + icon.intrinsicHeight

        when {
            dX > 0 -> { // Swiping to the right
//                val iconLeft = itemView.left + iconMargin + icon.intrinsicWidth
//                val iconRight = itemView.left + iconMargin
//                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)
//
//                background.setBounds(
//                    itemView.left, itemView.top, itemView.left + dX.roundToInt() + backgroundCornerOffset, itemView.bottom
//                )
            }
            dX < 0 -> { // Swiping to the left
                val iconLeft = itemView.right - iconMargin - icon.intrinsicWidth
                val iconRight = itemView.right - iconMargin
                icon.setBounds(iconLeft, iconTop, iconRight, iconBottom)

                background.setBounds(
                    itemView.right + dX.roundToInt() - backgroundCornerOffset,
                    itemView.top, itemView.right, itemView.bottom
                )
            }
            else -> // view is unSwiped
                background.setBounds(0, 0, 0, 0)
        }

        background.draw(c)
        icon.draw(c)
    }

}