(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojurescript "1.9.89"]
                 [org.clojure/core.async "0.2.385"]])

(deftask testing []
  (set-env! :source-paths #(conj % "tests"))
  identity)
