# Busboy

[![Clojars Project](https://img.shields.io/clojars/v/johan-sports/busboy.svg)](https://clojars.org/johan-sports/busboy)

Clojurescript USB monitoring for Node.js

## Usage

Busboy only provides one function, `create-usb-listener`. This will return a
`core.async` channel (you can also pass a prexisting one) that returns events
of type `[:attached <dev>]` and `[:detached <dev>]`. For example:

```
cljs.user> (require '[johan-sports.busboy :as busboy])
nil
cljs.user> (def poll-interval 1000) ;; in milliseconds
nil
cljs.user> (def dev-ch (busboy/create-usb-listener poll-interval))
nil
cljs.user> ;; device is attached
cljs.user> (go (println (<! dev-ch)))
[:attached {:id "...", :manufacturer "Foobar Industries", :mount "/Volumes/Vol1"}]
nil
cljs.user> ;; device is detached
cljs.user> (go (println (<! dev-ch)))
[:attached {:id "...", :manufacturer "Foobar Industries"}]
nil
cljs.user> (close! dev-ch) ;; stop listening
```

There are plans in the future to remove the dependecy on `core.async` and either
pass a callback directly or use something like `manifold`.

### Devices

Devices are represented with a plain hash-map:

```clojure
{
  :id "0x22B3-0xEF23-IDQ21AS23AB"    ;; Unique ID
  :vendor-id     0x22B3              ;; USB vendor ID
  :product-id    0xEF23              ;; USB product ID
  :manufacturer  "Foobar Industries" ;; Name of manufacturer (if available)
  :product       "Time Traveler v3"  ;; Name of product (if available)
  :serial-number "IDQ21AS23AB"       ;; Serial number (if available)
  :mount         "/Volumes/Vol1"     ;; Path to mount point (if available)
}
```

## Contributors

* Antonis Kalou

## License

Licensed under MIT. See the [LICENSE](LICENSE) file in the project root directory.
