class WikipediaGame() {
    init {
        require(seedUrl.contains("wikipedia")) { "Wikipedia link is required as seed" }
    }

    private val htmlParser = HtmlParser()
    private val crawler = Crawler(seedUrl)

    fun countHops(fromUrl: String, toUrl: String): Int {
        val fromPage: Hyperlink = htmlParser.buildHyperlink(fromUrl)
        val toPage: Hyperlink = htmlParser.buildHyperlink(toUrl)
    }
}
