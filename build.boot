(set-env!
 :source-paths #{"src"}
 :dependencies '[[org.clojure/clojure         "1.8.0"     :scope "test"]
                 [org.clojure/clojurescript   "1.9.542"]
                 [org.clojure/core.async      "0.2.385"]
                 [adzerk/boot-cljs            "1.7.228-1" :scope "test"]
                 [crisptrutski/boot-cljs-test "0.3.1"     :scope "test"]
                 [degree9/boot-npm            "1.4.0"     :scope "test"]]
 :repositories #(conj % ["clojars" {:url "https://clojars.org/repo/"
                                    :username (System/getenv "CLOJARS_USER")
                                    :password (System/getenv "CLOJARS_PASS")}]))

(require '[adzerk.boot-cljs :refer [cljs]]
         '[crisptrutski.boot-cljs-test :refer [test-cljs]]
         '[degree9.boot-npm :refer [npm]])

(task-options!
 pom {:project 'johan-sports/busboy
      :version "0.1.0"
      :description "Clojurescript USB monitoring for Node.js"
      :url "https://github.com/johan-sports/busboy"
      :license {"MIT" "https://github.com/johan-sports/busboy/blob/master/LICENSE"}}
 push {:tag true
       :ensure-branch "master"
       :ensure-release true
       :ensure-clean true
       :repo "clojars"})

(deftask testing []
  (merge-env! :source-paths #{"test"})
  identity)

(deftask test []
  (comp
   (testing)
   (test-cljs)))

(deftask build
  "Install dependencies and compile to a JS file."
  []
  (comp
   (npm :install {:subdevil "0.1.0"})
   (cljs :optimizations :advanced
         :compiler-options {:target :nodejs
                            :infer-externs true})
   (target)))

(deftask package
  "Package in to a JAR file."
  []
  (comp
   (build)
   (pom)
   (jar)))
