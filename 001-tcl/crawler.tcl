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
