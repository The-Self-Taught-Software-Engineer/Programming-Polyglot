import java.net.URL
import java.util.Queue
import java.util.ArrayDeque

class Crawler {
    private val htmlParser = HtmlParser()

    fun start(
        seedUrl: String,
        allowedFullDomains: Set<String> = setOf(),
        allowedPatterns: Set<Regex> = setOf(Regex(".*")),
        maxVisited: Int = 100,
    ): LinkedHashSet<Hyperlink> {
        // TODO: Validate 'allowedFullDomains' to be legal domains

        val seedHyperlink: Hyperlink = htmlParser.buildHyperlink(seedUrl)

        val visitedHyperlinks: LinkedHashSet<Hyperlink> = LinkedHashSet()
        val visitedDomains: MutableSet<String> = mutableSetOf()

        val linkQueue: Queue<Hyperlink> = ArrayDeque(setOf(seedHyperlink))
        while (linkQueue.isNotEmpty()) {
            if (visitedHyperlinks.count() >= maxVisited) break

            val link: Hyperlink = linkQueue.remove()
            if (link in visitedHyperlinks) continue
            if (allowedFullDomains.isEmpty() && link.fullDomain in visitedDomains) continue

            val html: String? = retrieveWebsiteContents(link.url)
            if (html == null) continue

            val urls: Set<Hyperlink> = htmlParser.retrieveHyperlinks(link, html).toSet()
            urls
                .filter {
                    if (allowedFullDomains.isNotEmpty()) {
                        it.fullDomain in allowedFullDomains
                    } else true
                }
                .filter { hyperlink: Hyperlink ->
                    allowedPatterns.all { it.containsMatchIn(hyperlink.url) }
                }
                .forEach { linkQueue.add(it) }

            visitedHyperlinks.add(link)
            visitedDomains.add(link.fullDomain)

            println("Visited ${link.url}")
            println("Currently queued up ${linkQueue.count()} hyperlinks")
        }
        println("\n\n")

        val protocolStatistics: Map<String, Int> =
            buildOccurrenceStatistics(visitedHyperlinks) { it.protocol }
        val topLevelDomainStatistics: Map<String, Int> =
            buildOccurrenceStatistics(visitedHyperlinks) { it.topLevelDomain }
        val rootDomainStatistics: Map<String, Int> =
            buildOccurrenceStatistics(visitedHyperlinks) { it.rootDomain }

        println("Statistics:\n")
        println("Protocol statistics: ${protocolStatistics}")
        println("Top-level domain statistics: ${topLevelDomainStatistics}")
        println("Root domain statistics: ${rootDomainStatistics}")

        return visitedHyperlinks
    }

    private fun retrieveWebsiteContents(url: String): String? {
        return try {
            URL(url).readText()
        } catch(exception: Exception) {
            println("Could not retrieve contents of '$url': ${exception.message}")
            null
        }
    }

    /**
    * Returns a map of the selected property of [Hyperlink] as a [String],
    * with the occurrences of each as the values.
    * The map is sorted by the values descendingly.
    */
    private fun buildOccurrenceStatistics(
        hyperlinks: Set<Hyperlink>,
        keySelector: (Hyperlink) -> Any,
    ): Map<String, Int> {
        return hyperlinks
            .groupBy { keySelector(it).toString() }
            .mapValues { it.value.count() }
            .toList()
            .sortedByDescending { (_, value) -> value }
            .toMap()
    }
}

// fun run(args: Array<String>) {
//     println("Starting...\n\n")

//     val crawler = Crawler(args.firstOrNull() ?: "https://kotlinlang.org/")
//     crawler.start()

//     println("\n\nExiting...")
// }
