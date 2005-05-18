#!/bin/sh

cd $HOME/workspace/tcpsite/Jsp/WEB-INF

java \
-classpath classes:\
lib/junit.jar:\
../../lib/htmlunit-1.4.jar:\
../../lib/commons-httpclient-3.0-rc1.jar:\
lib/commons-logging.jar:\
../../lib/commons-codec-1.3.jar:\
../../lib/commons-io-1.0.jar:\
../../lib/js-1.6R1.jar:\
../lib/xmlParserAPIs-2.2.1.jar:\
../lib/xercesImpl-2.6.2.jar:\
 regress.webtest.TestRunner ../../Java/regress/webtest/tests.txt
