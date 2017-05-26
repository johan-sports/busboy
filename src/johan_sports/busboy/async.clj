(ns johan-sports.busboy.async)

(defmacro go-when
  "Continue running go block as long as `bindings` return true."
  [bindings & body]
  `(cljs.core.async.macros/go-loop []
     (when-let ~bindings
       ~@body
       (recur))))
