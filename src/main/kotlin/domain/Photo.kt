package domain

class Photo(val id: Int, val tags: Array<Int>, val isVertical: Boolean) {
    companion object {
        fun fromString(id: Int, s: String, tagDict: HashMap<String, Int>): Photo {
            var nextTagId = tagDict.maxByOrNull { it.value }?.value ?: 0
            val parts = s.split(" ")
            val (orientation, tagsCount) = parts
            val isVertical = orientation == "V"
            val tags = parts
                .slice(2..tagsCount.toInt() + 1)
                .map {
                    val tag = tagDict[it]
                    if (tag != null) {
                        tag
                    } else {
                        val res = nextTagId
                        tagDict[it] = nextTagId
                        nextTagId += 1
                        res
                    }
                }
            return Photo(id, tags.toTypedArray(), isVertical)
        }
    }
}