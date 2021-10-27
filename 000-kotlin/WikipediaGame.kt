class WikipediaGame {
    private val htmlParser = HtmlParser()
    private val crawler = Crawler()

    fun countHops(fromUrl: String, toUrl: String) {
        validateUrl(fromUrl)
        validateUrl(toUrl)

        val visitedHyperlinks: LinkedHashSet<Hyperlink> = crawler.start(
            seedUrl = fromUrl,
            allowedFullDomains = setOf("en.wikipedia.org"),
            allowedPatterns = setOf(Regex("\\/wiki\\/")),
            maxVisited = 1000,
        )

        println("Took ${visitedHyperlinks.map { it.url }.indexOfFirst { it == toUrl }} iterations from '$fromUrl' to '$toUrl'")
    }

    private fun validateUrl(url: String) {
        require(url.contains("wikipedia")) { "Wikipedia link is required as URL" }
    }
}
