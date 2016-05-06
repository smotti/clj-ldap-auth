(ns clj-ldap-auth.ldap
  (:import [com.unboundid.ldap.sdk
            LDAPConnection
            LDAPException
            SearchScope]
           [com.unboundid.util.ssl
            SSLUtil
            TrustAllTrustManager]))

(def ssl-socket-factory (.createSSLSocketFactory (SSLUtil. (TrustAllTrustManager.))))

(defn- connect
  "Returns an authenticated connection to the LDAP server"
  [{:keys [host port ssl? bind-dn bind-pw]}]
  (let [connection (LDAPConnection.)]
    (when ssl?
      (.setSocketFactory connection ssl-socket-factory))
    (.connect connection host port)
    (when (not (nil? bind-dn))
      (.bind connection bind-dn bind-pw))
    connection))

(defn- results-empty?
  "Are there any results?"
  [results]
  (= 0 (.getEntryCount results)))

(defn- first-result
  "Returns the first result in results"
  [results]
  (.get (.getSearchEntries results) 0))

(defn dn
  "Returns the DN attribute from the first result in results.

   Returns nil if the DN looks empty (since bind with an empty string
   always succeeds)."
  [result]
  (let [dn (.getDN result)]
    (if (empty? (.trim dn))
      nil
      dn)))

(defn- search
  "Search for username using the supplied connection to the LDAP server"
  [connection username base-dn user-filter]
  (let [user-filter (str "(" user-filter username ")")]
    (.search connection base-dn SearchScope/SUB user-filter nil)))

(defn fail
  [sink message]
  (do
    (sink message)
    false))

(defn- try-bind
  [username password config sink]
  (try
    (connect (assoc config :bind-dn username :bind-pw password))
    true
    (catch LDAPException e
      (fail sink (str "Failed to authenticate '" username "': " (.getMessage e))))))

(defn bind?
  "Attempts to authenticated with the LDAP server using the supplied
   username and password. Returns true iff successful.

   Any exceptions that occur communicating with the LDAP server (e.g.
   invalid bind dn/password) are ignored and false is returned. You
   can optionally pass as a third argument a function that will be
   called with the reason for the failure, e.g:

     (bind? username password #(println (str \"Error: \" %1)))"
  ([username password config]
     (bind? username password config (fn [message] (println message))))
  ([username password config sink]
     (let [fail (partial fail sink)]
       (try
         (let [connection (connect config)
               results (search connection username (:base-dn config) (:filter config))]
           (if (results-empty? results)
             (fail (str "username '" username "' not found"))
             (if-let [dn (dn (first-result results))]
               (try-bind dn password config sink)
               (fail (str "Could not find a DN for username '" username "'")))))
         (catch Throwable e
           (fail (str "Error connecting to LDAP server '" (:host config) "': " (.getMessage e))))))))
