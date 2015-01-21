kill -9 `cat dropwizard.pid`

tokill=`ps -ef | grep java | grep 'kafka-http.yml' | awk '{print $2}'`
kill -9 $tokill
