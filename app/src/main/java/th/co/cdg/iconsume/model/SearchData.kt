package th.co.cdg.iconsume.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "History")
data class SearchData(
    val numberSearch: String,
    val data: ProductOutput? = null,
    val isFound: Boolean = false,
    val imgPath: String? = null
) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}