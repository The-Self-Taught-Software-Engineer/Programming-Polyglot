package require http 2
package require tls 1.7
package require htmlparse
package require struct

http::register https 443 [list ::tls::socket -autoservername true]

variable seed "https://www.tcl-lang.org/"

proc getUrlHtml {url} {
    variable token [http::geturl $url]
    variable body [http::data $token]

    variable html [struct::tree]
    htmlparse::2tree $body $html
    htmlparse::removeVisualFluff $html
    htmlparse::removeFormDefs $html
    return $html
}

proc parseHyperlink {url} {
    regexp -nocase {(https?)://.+?\.(.+?)\/} $url _ protocol topLevelDomain
    if {[info exists protocol] == 0} {
        return ""
    }
    return [dict create url $url protocol $protocol topLevelDomain $topLevelDomain]
}

proc findAllHyperlinks {url} {
    variable htmlTree [getUrlHtml $url]

    variable hyperlinks {}
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

variable linksToVisit [struct::queue]
$linksToVisit put [parseHyperlink $seed]

variable visitedLinks {}

while {[$linksToVisit size] > 0} {
    variable hyperlink [$linksToVisit get]

    if {$hyperlink == ""} {continue}
    variable linkToVisit [dict get $hyperlink url]

    if {[lsearch $visitedLinks $linkToVisit] != -1} {continue}
    lappend visitedLinks $linkToVisit

    variable newLinks [findAllHyperlinks $linkToVisit]
    puts "Visited: $linkToVisit"

    foreach link $newLinks {$linksToVisit put $link}
}
