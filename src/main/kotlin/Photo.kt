class Photo(val id: Int, val tags: HashSet<String>, val isVertical: Boolean) {
    companion object {
        fun fromString(id: Int, s: String): Photo {
            val parts = s.split(" ")
            val (orientation, tagsCount) = parts
            val isVertical = orientation == "V"
            val tags = parts.slice(2..tagsCount.toInt() + 1)
            return Photo(id, tags.toHashSet(), isVertical)
        }
    }
}