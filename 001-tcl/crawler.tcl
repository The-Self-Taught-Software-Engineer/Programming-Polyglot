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

proc findAllHyperlinks {url} {
    variable htmlTree [getUrlHtml $url]

    variable hyperlinks {}
    $htmlTree walk [$htmlTree rootname] -type bfs node {
        variable type [$htmlTree get $node type]
        if {$type == "a"} {
            variable tags [$htmlTree get $node data]
            regexp -nocase {href=(['\"])([(http)].+?)\1} $tags fullMatch quote link
            if {[info exists link] == 1} {
                # if {[string match "/" $match2]} {
                #     variable match2 "$match2"
                # }
                lappend hyperlinks $link
            }
        }
    }
    return $hyperlinks
}

variable linksToVisit [struct::queue]
$linksToVisit put $seed
while {[$linksToVisit size] > 0} {
    variable visited [$linksToVisit get]
    puts "Visited: $visited"
     
    variable newLinks [findAllHyperlinks $visited]

    foreach link $newLinks {$linksToVisit put $link}
}
