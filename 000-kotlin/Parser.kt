import kotlin.text.Regex

class HtmlParser {
    fun retrieveHyperlinks(html: String): Sequence<Hyperlink> {
        return findUrls(html).map { buildHyperlink(it) }
    }

    private fun findUrls(html: String): Sequence<String> {
        return A_TAG_PATTERN.findAll(html).mapNotNull { it.groups.get(URL_GROUP_INDEX)?.value }
    }

    private fun buildHyperlink(url: String): Hyperlink {
        val urlWithoutProtocol = url.substringAfter("://")
        return Hyperlink(
            url = url,
            protocol = Protocol.valueOf(url.substringBefore(":").uppercase()),
            subdomains = parseSubdomains(urlWithoutProtocol),
            domain = parseDomain(urlWithoutProtocol),
            topLevelDomain = urlWithoutProtocol.substringBefore("/").substringAfterLast("."),
        )
    }

    private fun parseDomain(urlWithoutProtocol: String): String {
        return if (hasSubdomain(urlWithoutProtocol)) {
            urlWithoutProtocol.substringAfter(".").substringBefore(".")
        } else urlWithoutProtocol.substringBefore(".")
    }

    private fun parseSubdomains(urlWithoutProtocol: String): Set<String> {
        return if (hasSubdomain(urlWithoutProtocol)) {
            setOf(urlWithoutProtocol.substringBefore("."))
        } else setOf()
    }

    private fun hasSubdomain(urlWithoutProtocol: String): Boolean {
        return urlWithoutProtocol.substringBefore("/").count { it == '.' } > 1
    }

    companion object {
        // Play around with this pattern: https://regex101.com/r/N9hj8H/1
        private val A_TAG_PATTERN: Regex = Regex("<a(?:[^>]*)href=(['\\\"])(http.+?)\\1")
        // The group that matches the actual URL in the a-tag
        private const val URL_GROUP_INDEX: Int = 2
    }
}
