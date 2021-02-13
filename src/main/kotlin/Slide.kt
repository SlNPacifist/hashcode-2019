class Slide {
    lateinit var photos: List<Photo>
    lateinit var tags: HashSet<String>

    constructor(photos: List<Photo>) {
        this.photos = photos
        this.tags = photos.flatMap { it.tags }.toHashSet()
    }
}