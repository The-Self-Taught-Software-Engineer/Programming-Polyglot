import java.net.URL

class Crawler(private val seedUrl: String) {
    fun start() {
        println(retrieveWebsiteContents(seedUrl))
    }

    private fun retrieveWebsiteContents(url: String): String {
        return URL(url).readText()
    }
}

fun main(args: Array<String>) {
    println("Starting...")

    val crawler = Crawler("http://kotlinlang.org/")
    crawler.start()

    println("Exiting...")
}
