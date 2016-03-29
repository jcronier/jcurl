@set CURRENT_SCRIPT_DIR=%~dp0%\.

@set VERSION=1-SNAPSHOT

@rem Setup with system properties for proxy and trust/key store

@set JCURL_OPTS=%JCURL_OPTS% ^
 -Djava.net.useSystemProxies=true ^
 -Djavax.net.ssl.trustStoreType=Windows-ROOT ^
 -Djavax.net.ssl.trustStore=NONE ^
 -Djavax.net.ssl.keyStoreType=Windows-MY ^
 -Djavax.net.ssl.keyStore=NONE
 

@call %JAVA_HOME%\bin\java.exe %JCURL_OPTS% -jar %CURRENT_SCRIPT_DIR%\target\jcurl-%VERSION%-BUNDLE.jar %*