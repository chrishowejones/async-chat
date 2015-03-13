(ns async-chat.core
       (:require [clojure.core.async :refer [<!! >! go ]]
                [net.async.tcp :refer :all])
       (:gen-class))


(defn echo-server []
  (let [acceptor (accept (event-loop) {:port 8999})]
    (loop []
      (when-let [server (<!! (:accept-chan acceptor))]
        (go
          (loop []
            (when-let [msg (<! (:read-chan server))]
              (when-not (keyword? msg)
                (println "msg from client = " (String. msg))
                (>! (:write-chan server) (.getBytes (str "ECHO/" (String. msg)))))
              (recur))))
        (recur)))))


(defn echo-client []
  (let [client (connect (event-loop) {:host "127.0.0.1" :port 8999})]
    (loop []
      (go (>! (:write-chan client) (.getBytes (str (rand-int 100000)))))
      (loop []
        (let [read (<!! (:read-chan client))]
          (if (not (keyword? read))
            (println "Echo from server: " (String. read)))
          (when (and (keyword? read)
                     (not= :connected read))
            (recur))))
      (Thread/sleep (rand-int 3000))
      (recur))))

(defn -main
  "I don't do a whole lot ... yet."
  [& args]
  (println "Hello, World!"))
