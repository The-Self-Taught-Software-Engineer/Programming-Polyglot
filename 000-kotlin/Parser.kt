import kotlin.text.Regex

class HtmlParser {
    fun findUrls(html: String): Set<String> {
        return A_TAG_PATTERN.findAll(html)
            .mapNotNull { it.groups.get(URL_GROUP_INDEX)?.value }
            .toSet()
    }

    companion object {
        // Play around with this pattern: https://regex101.com/r/N9hj8H/1
        val A_TAG_PATTERN: Regex = Regex("<a(?:[^>]*)href=(['\\\"])(http.+?)\\1")
        // The group that matches the actual URL in the a-tag
        const val URL_GROUP_INDEX: Int = 2
    }
}
