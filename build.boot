(def +version+ "0.1.0")

(set-env!
 :source-paths    #{"src/main"}
 :resource-paths  #{"resources"}
 :dependencies '[[org.clojure/clojure         "1.9.0-alpha15"  :scope "provided"]
                 [org.clojure/clojurescript   "1.9.494"        :scope "provided"]
                 [org.omcljs/om               "1.0.0-alpha48"  :scope "provided"]
                 [com.cognitect/transit-clj   "0.8.300"        :scope "test"]
                 [devcards                    "0.2.2"          :scope "test"]
                 [devcards-om-next            "0.3.0"          :scope "test"]
                 [com.cemerick/piggieback     "0.2.1"          :scope "test"]
                 [pandeiro/boot-http          "0.7.6"          :scope "test"]
                 [adzerk/boot-cljs            "2.0.0"          :scope "test"]
                 [adzerk/boot-cljs-repl       "0.3.3"          :scope "test"]
                 [crisptrutski/boot-cljs-test "0.3.0"          :scope "test"]
                 [adzerk/boot-reload          "0.5.1"          :scope "test"]
                 [adzerk/bootlaces            "0.1.13"         :scope "test"]
                 [org.clojure/tools.nrepl     "0.2.12"         :scope "test"]
                 [weasel                      "0.7.0"          :scope "test"]])

(require
 '[adzerk.boot-cljs      :refer [cljs]]
 '[adzerk.boot-cljs-repl :refer [cljs-repl-env start-repl]]
 '[adzerk.boot-reload    :refer [reload]]
 '[adzerk.bootlaces      :refer [bootlaces! push-release]]
 '[crisptrutski.boot-cljs-test :refer [test-cljs]]
 '[pandeiro.boot-http :refer [serve]]
 '[clojure.java.io :as io])

(bootlaces! +version+ :dont-modify-paths? true)

(task-options!
  pom {:project 'plomber
       :version +version+
       :description "Component instrumentation for Om Next"
       :url "http://github.com/anmonteiro/plomber"
       :scm {:url "https://github.com/anmonteiro/plomber"}
       :license {"name" "Eclipse Public License"
                 "url"  "http://www.eclipse.org/legal/epl-v10.html"}})

(deftask build-jar []
  (set-env! :resource-paths #{"src/main"})
  (adzerk.bootlaces/build-jar))

(deftask release-clojars! []
  (comp
    (build-jar)
    (push-release)))

(deftask deps [])

(deftask devcards []
  (set-env! :source-paths #(conj % "src/devcards"))
  (comp
    (serve)
    (watch)
    (cljs-repl-env)
    (reload)
    (speak)
    (cljs :source-map true
          :compiler-options {:devcards true
                             :parallel-build true}
          :ids #{"js/devcards"})
    (target :dir #{"target"})))

(deftask testing []
  (set-env! :source-paths #(conj % "src/test"))
  identity)

(ns-unmap *ns* 'test)

(deftask test []
  (comp
    (testing)
    (test-cljs
      :namespaces #{'plomber.tests}
      :ids ["output"]
      :js-env :node
      :cljs-opts {:parallel-build true})))
