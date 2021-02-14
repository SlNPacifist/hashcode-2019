package domain

class Slide {
    lateinit var photos: List<Photo>
    lateinit var tags: Array<Int>

    constructor(tagDict: HashMap<String, Int>, photos: List<Photo>) {
        this.photos = photos
        this.tags = photos.flatMap { it.tags }.toHashSet().map { tagDict[it]?: throw Error("Tag not found") }.toTypedArray()
    }
}