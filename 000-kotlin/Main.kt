fun main(args: Array<String>) {
    val wikipediaGame = WikipediaGame()
    wikipediaGame.countHops(
        args.elementAtOrNull(0) ?: error("Please provide starting page"),
        args.elementAtOrNull(1) ?: error("Please provide ending page"),
    )
}
