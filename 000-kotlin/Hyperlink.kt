data class Hyperlink(
    val url: String,
    val protocol: Protocol,
    val subdomains: Set<String>,
    val domain: String,
    val topLevelDomain: String,
    val fullDomain: String = "${formatSubdomains(subdomains)}${domain}.${topLevelDomain}",
    val rootDomain: String = "${domain}.${topLevelDomain}",
) {
    companion object {
        fun formatSubdomains(subdomains: Set<String>): String {
            return if (subdomains.isNotEmpty()) {
                subdomains.joinToString(separator = ".", postfix = ".")
            } else ""
        }
    }
}
