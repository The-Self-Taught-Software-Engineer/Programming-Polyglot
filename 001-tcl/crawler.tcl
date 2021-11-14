package require http 2
package require tls 1.7
package require htmlparse
package require struct

http::register https 443 [list ::tls::socket -autoservername true]

# Returns a `struct::tree` of the HTML of the given `url`.
proc getUrlHtml {url} {
    variable html [struct::tree]
    try {
        variable token [http::geturl $url]
        variable body [http::data $token]

        htmlparse::2tree $body $html
        htmlparse::removeVisualFluff $html
        htmlparse::removeFormDefs $html
    }
    return $html
}

# Returns a `dict` with the keys `{url protocol rootDomain}` parsed from the given `url`.
proc parseHyperlink {url} {
    regexp -nocase {(https?):\/\/(.+?)\/} $url _ protocol rootDomain
    if {[info exists protocol] == 0} {
        return ""
    }
    return [dict create url $url protocol $protocol rootDomain $rootDomain]
}

# Returns all hyperlinks (i.e., `<a>` tags) appearing on the given `url`.
proc findAllHyperlinks {url} {
    variable htmlTree [getUrlHtml $url]

    variable hyperlinks {}

    if {[llength [$htmlTree nodes]] == 0} {return hyperlinks}

    $htmlTree walk [$htmlTree rootname] -type bfs node {
        variable type [$htmlTree get $node type]
        if {$type == "a"} {
            variable tags [$htmlTree get $node data]
            regexp -nocase {href=(['\"])([(http)].+?)\1} $tags _ quote link
            if {[info exists link] == 1} {
                lappend hyperlinks [parseHyperlink $link]
            }
        }
    }

    return $hyperlinks
}

# Builds a lambda expression out of the given arguments.
# Copied from: https://wiki.tcl-lang.org/page/Lambda+in+Tcl
proc lambda {argl body} {
    set name {}
    proc $name $argl $body
    return $name
}

# Returns how many elements in the given `collection` test positive to the given `predicate`.
proc count {collection predicate} {
    foreach element $collection {
        if ([$predicate $element]) {
            incr c
        }
    }
    return $c
}

# Starts the crawling process from the given seed `url`, printing results at the end.
# A maximum of `maxVisits` hyperlinks are visited before termination.
proc start {url maxVisits} {
    puts "Starting crawling\n"

    variable linksToVisit [struct::queue]
    $linksToVisit put [parseHyperlink $url]

    variable visitedLinks {}
    variable visitedRootDomains {}

    while {[$linksToVisit size] > 0} {
        variable link [$linksToVisit get]

        if {$link == ""} {continue}
        if {[llength $visitedLinks] >= $maxVisits} {break}

        if {[lsearch $visitedLinks $link] != -1} {continue}
        lappend visitedLinks $link

        variable rootDomain [dict get $link rootDomain]
        if {[lsearch $visitedRootDomains $rootDomain] != -1} {continue}
        lappend visitedRootDomains $rootDomain

        variable urlToVisit [dict get $link url]
        variable newLinks [findAllHyperlinks $urlToVisit]
        puts "Visited: $urlToVisit"

        foreach link $newLinks {$linksToVisit put $link}
    }

    puts "\nStatistics:"

    puts "Visited [llength $visitedLinks] hyperlinks\n"

    puts "Used protocols:"
    variable protocols [lmap link $visitedLinks {dict get $link protocol}]
    puts "HTTP: [count $protocols [lambda x {expr {$x == "http"}}]]"
    puts "HTTPS: [count $protocols [lambda x {expr {$x == "https"}}]]"

    puts "\nFinished crawling"
}

start https://www.tcl-lang.org/ 500
