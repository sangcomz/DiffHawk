package io.github.sangcomz.diffhawk

object DisplayFormatter {
    
    fun formatDisplay(
        template: String,
        branch: String,
        filesChanged: Int,
        insertions: Int,
        deletions: Int,
        total: Int,
        limitCount: Int = 0
    ): String {
        val fileText = if (filesChanged == 1) "file" else "files"
        val fileChangedText = if (filesChanged == 1) "$filesChanged file changed" else "$filesChanged files changed"
        
        val remainingLines = if (limitCount > 0) {
            (limitCount - total).coerceAtLeast(0)
        } else {
            -1
        }
        
        val remainingLinesText = when {
            limitCount <= 0 -> "∞"
            remainingLines <= 0 -> "0"
            else -> remainingLines.toString()
        }
        
        return template
            .replace("{branch}", branch)
            .replace("{fileCount}", filesChanged.toString())
            .replace("{fileText}", fileText)
            .replace("{fileChangedText}", fileChangedText)
            .replace("{addedLine}", insertions.toString())
            .replace("{removedLine}", deletions.toString())
            .replace("{total}", total.toString())
            .replace("{limitCount}", if (limitCount > 0) limitCount.toString() else "∞")
            .replace("{remainingLines}", remainingLinesText)
    }
}
