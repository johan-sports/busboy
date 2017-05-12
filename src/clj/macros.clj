(ns busboy.macros)

(defmacro go-when
  [bindings & body]
  `(cljs.core.async.macros/go-loop []
     (when-let ~bindings
       ~@body
       (recur))))
