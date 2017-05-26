(ns johan-sports.busboy
  (:require
   [johan-sports.busboy.utils :refer [js->clj-kw]]
   [cljs.core.async :refer [timeout chan put! <! >!]]
   [clojure.data])
  (:require-macros
   [johan-sports.busboy.async :refer [go-when]]
   [cljs.core.async.macros    :refer [go-loop]]))

(def ^:private subdevil (js/require "subdevil"))

(when (nil? subdevil)
  (throw (js/Error. "Expected module subdevil to be defined, got nil")))

(defn- device-list
  "Return a list of `Device` objects for the USB devices
  attached to the system."
  ([] (device-list (chan)))
  ([out]
   (.then (.poll subdevil)
          (fn [devs] (put! out devs)))

   out))

(defn- apply-poll
  "Poll constantly with a delay of `ms` milliseconds and call `f` with the `out` channel."
  ([ms f] (apply-poll (chan) ms f))
  ([out ms f]
   (go-loop []
     (<! (timeout ms)) ;; Wait MS milliseconds until trying again
     (f out)
     (recur))
   out))

(defn- apply-diff
  "Apply a function `f` to the diffed values between `a` and `b` and
  remove any nil values."
  [f a b]
  (let [diff (clojure.data/diff a b)]
    (remove nil? (f diff))))

(defn added-devices
  "Compare the `old` and `new` device maps and return the items
  that were not present previously."
  [old new]
  (apply-diff second old new))

(defn removed-devices
  "Compare the `old` and `new` device maps and return the items that
  were present previously in `old`, but are no longer present in `new`."
  [old new]
  (apply-diff first old new))

(defn- make-device
  "Create a new device from the device `dev` received from subdevil"
  [dev] (js->clj-kw dev))

(defn- create-device-state
  "Create initial state atom for devices."
  [] (atom #{}))

(defn- put-devices!
  "Pass all `devs` as messages along the `out` channel. These messages
  are represented as [`type` `dev`]. Allowed types are `:attach` and `:detach`."
  [out type devs]
  {:pre [(#{:attach :detach} type)]}
  (doseq [d devs] (put! out [type d])))

;; TODO: Don't expose a core.async API
(defn create-usb-listener
  "Stream attach and detach events through <out>, every <ms> milliseconds.
  An attach event will be represented as a pair of [:attach <dev>] and
  a detach event will be represented as [:detach <dev>]."
  ([ms] (create-usb-listener (chan) ms))
  ([out ms]
   (let [poll (apply-poll ms device-list)
         ;; Keep device state local in the case that a second
         ;; USB listener is created, states won't overwrite eachother.
         devices (create-device-state)]
     (go-when [devs (<! poll)]
       (let [new (set (map make-device devs))
             old @devices]
         (put-devices! out :attach (added-devices old new))
         (put-devices! out :detach (removed-devices old new))
         ;; Update new devices
         (reset! devices new)))
     out)))
