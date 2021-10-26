import java.net.URL
import java.util.Queue
import java.util.ArrayDeque

class Crawler(private val seedUrl: String) {
    private val htmlParser = HtmlParser()

    fun start() {
        val seedHyperlink: Hyperlink = htmlParser.buildHyperlink(seedUrl)

        val visitedRootDomains: MutableSet<String> = mutableSetOf()

        val linkQueue: Queue<Hyperlink> = ArrayDeque(setOf(seedHyperlink))
        while (linkQueue.isNotEmpty()) {
            if (visitedRootDomains.count() > 10) break

            val link: Hyperlink = linkQueue.remove()

            val html: String? = retrieveWebsiteContents(link.url)
            if (html == null) continue

            val urls: Set<Hyperlink> = htmlParser.retrieveHyperlinks(html).toSet()
            urls.forEach { linkQueue.add(it) }

            visitedRootDomains.add(link.rootDomain)

            println("Visited ${link.url}")
        }
    }

    private fun retrieveWebsiteContents(url: String): String? {
        return try {
            URL(url).readText()
        } catch(exception: Exception) {
            println("Could not retrieve contents of '$url': ${exception.message}")
            null
        }
    }
}

fun main(args: Array<String>) {
    println("Starting...\n\n")

    val crawler = Crawler("https://kotlinlang.org/")
    crawler.start()

    println("\n\nExiting...")
}
