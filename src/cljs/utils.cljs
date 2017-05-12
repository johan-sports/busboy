(ns busboy.utils
  (:require [clojure.string :as string]
            [clojure.walk :as walk]))

(defn camel-case->kebab-case
  "Convert a string S that is in camel case format (likeThis)
  to a string in kebab case (like-this)."
  [s]
  (-> (name s)
      ;; Replace capitals with -, so AKey will be a-key
      (string/replace #"[A-Z]" #(str "-" (string/lower-case %)))
      ;; Replace underscores with dashes
      (string/replace #"_" "-")))

(defn js->clj-kw
  "Recursively convert JS objects to clojure objects, converting
  any camel case keys to standard clojure kebab case.

  WARNING: This is not tail recursive, so it may crash using large objects."
  [js-obj]
  (let [obj (js->clj js-obj)
        ks (map camel-case->kebab-case (keys obj))]
    (walk/keywordize-keys
     (zipmap ks (map (fn [obj]
                       (if (map? obj)
                         (js->clj-kw obj)
                         obj))
                     (vals obj))))))
