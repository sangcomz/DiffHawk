package io.github.sangcomz.diffhawk.renderer

data class DiffData(
    val branch: String,
    val filesChanged: Int,
    val insertions: Int,
    val deletions: Int,
    val total: Int,
    val limitCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val remainingLines: Int
        get() = if (limitCount > 0) {
            (limitCount - total).coerceAtLeast(0)
        } else {
            -1 // 무제한
        }
    
    val limitProgress: Float
        get() = if (limitCount > 0) {
            (total.toFloat() / limitCount.toFloat()).coerceAtMost(1f)
        } else {
            0f
        }
}
