# clj-ldap-auth

A library that provides authentication via an LDAP server

## Installation

Add the following dependency to your `project.clj` file:

    [clj-ldap-auth "0.2.0"]

## Example

```clojure
(require '[clj-ldap-auth.ldap :as ldap])

(def config {:host "localhost" :port 389 :ssl? false
	     :bind-dn "cn=admin,dc=example,dc=com" :bind-pw "example"
	     :base-dn "ou=users,dc=example,dc=com"})

(if (ldap/bind? username password config)
  (do something-great)
  (unauthorised))
```

Optionally, you can also pass in a function that will be called with
the reason for any authentication failure (or exception):

```clojure
(let [reason (atom nil)]
  (if (ldap/bind? username password config #(reset! reason %1))
    (do something-great)
    (unauthorised @reason)))
```

Then start your app:

```
java my.program
```

### Configuration

All relevant configuration properties must be provided by the client code:

 * `host` - The hostname of your LDAP server.

 * `base-dn` - The base DN in which to search for user ids.

The following parameters are optional:

 * `port` - The port on which to connect to the LDAP server. Defaults to `636`.

 * `ssl` - Should the connection to the LDAP server use SSL. Defaults to `true`.

 * `bind-dn` - The DN with which to bind to the LDAP server to look up usernames.

 * `bind-pw` - The password for the `binddn`.


## Documentation

TODO

## History

### 0.2.0

* Use clojure.test instead of midje
* Remove build.sh
* Don't use System/getProperty but instead let user define within code
* Be able to specify filter for user search
* Bump clojure and unboundid ldapsdk version
* Provide a Vagrantfile to setup a VM with OpenLDAP for dev & testing

### 0.1.1

 * Initial release


## License

Copyright (C) 2013 REA Group Ltd.

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
