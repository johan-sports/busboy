# Busboy

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
[:attached ]
nil
cljs.user> ;; device is detached
cljs.user> (go (println (<! dev-ch)))
[:detached ]
nil
cljs.user> (close! dev-ch) ;; stop listening
```

There are plans in the future to remove the dependecy on `core.async` and either
pass a callback directly or use something like `manifold`.

## Contributors

* Antonis Kalou

## License

Licensed under MIT. See the [LICENSE](LICENSE) file in the project root directory.
