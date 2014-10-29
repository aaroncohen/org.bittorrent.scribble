(ns org.bittorrent.scribble-test
  (:require [clojure.test :refer :all]
            [org.bittorrent.scribble :refer :all]))

(def host-map-1  {:host "graphite.bittorrent.com"})

(def host-map-2  {:scheme "https"
                  :user "user"
                  :passwd "passwd"
                  :host "graphite.bittorrent.com"
                  :port 1234})

(def bad-map-1  {:scheme "https"
                 :passwd "passwd"
                  :host "graphite.bittorrent.com"
                  :port 1234})

(deftest host-test
  (testing ""
    (is (= (build-host host-map-1)
           "http://graphite.bittorrent.com/render"))
    (is (= (build-host host-map-2)
           "https://user:passwd@graphite.bittorrent.com:1234/render"))
    (is (thrown? Exception (build-host bad-map-1)))
    (is (thrown? Exception (build-host {})))
    ))


(def single-param {:from "-2hours"})

;; Use array map to preserve ordering
(def multiple-params (array-map :from "-2hours"
                                :height 20
                                :until "now"))

(def repeated-params (array-map :from "-2hours"
                                :target ["some.stats.one" "some.stats.two"]))

(def encodable-params (array-map :from "-2 hours"
                                 :target ["hitcount(some.stats.one, '1 min')" "hitcount(some.stats.two, '3 hours')"]))

(deftest build-params-for-key-test
  (testing ""
    (is (= (build-params-for-key (first {:target (:target repeated-params)}))
           ["target=some.stats.one" "target=some.stats.two"]))
    (is (= (build-params-for-key (first {:target (:target encodable-params)}))
           ["target=hitcount%28some.stats.one%2C+%271+min%27%29"
            "target=hitcount%28some.stats.two%2C+%273+hours%27%29"]))))

(deftest build-params-test
  (testing ""
    (is (= (build-params {})
           ""))
    (is (= (build-params single-param)
           "?from=-2hours"))
    (is (= (build-params multiple-params)
           "?from=-2hours&height=20&until=now"))
    (is (= (build-params repeated-params)
           "?from=-2hours&target=some.stats.one&target=some.stats.two"))
    ))

(deftest build-url-test
  (testing ""
    (is (= (build-url host-map-1 repeated-params)
           "http://graphite.bittorrent.com/render?from=-2hours&target=some.stats.one&target=some.stats.two"))))
