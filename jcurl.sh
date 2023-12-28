#!/usr/bin/env bash

set -euo pipefail

__BASEDIR="$(readlink -f "$(dirname "$0")")";if [[ -z "$__BASEDIR" ]]; then echo "__BASEDIR: undefined";exit 1;fi

_trustStoreLocations=(
  # Debian/Ubuntu/Gentoo etc.
  "/etc/ssl/certs/ca-certificates.crt"
  # Fedora/RHEL 6
  "/etc/pki/tls/certs/ca-bundle.crt"
  # OpenSUSE
  "/etc/ssl/ca-bundle.pem"
  # OpenELEC
  "/etc/pki/tls/cacert.pem"
  # CentOS/RHEL 7
  "/etc/pki/ca-trust/extracted/pem/tls-ca-bundle.pem"
  # SLES10/SLES11, https://golang.org/issue/12139
  "/etc/ssl/certs"
  # Android
  "/system/etc/security/cacerts"
  # FreeBSD
  "/usr/local/share/certs"
  # Fedora/RHEL
  "/etc/pki/tls/certs"
  # NetBSD
  "/etc/openssl/certs"
)
printSystemTrustStore() {
  for _trustStoreLocationToCheck in "${_trustStoreLocations[@]}"
  do
    if [[ -r "$_trustStoreLocationToCheck" ]]; then
      echo "$_trustStoreLocationToCheck"
    fi
  done
}

main(){
  # Setup with system properties for proxy and trust/key store
  local jcurlOpts=(${JCURL_OPTS:-})
  jcurlOpts+=(-Djava.net.useSystemProxies=true)

  local systemTrustStore=""
  systemTrustStore="$(printSystemTrustStore)"
  if [[ "${OS:-}" == Windows*  ]];then
    jcurlOpts+=(-Djavax.net.ssl.trustStoreType=Windows-ROOT)
    jcurlOpts+=(-Djavax.net.ssl.trustStore=NONE)
    jcurlOpts+=(-Djavax.net.ssl.keyStoreType=Windows-MY)
    jcurlOpts+=(-Djavax.net.ssl.keyStore=NONE)
  elif [[ "${OSTYPE:-}" == 'darwin'* ]]; then
    # //FIXME: trustStoreType=KeychainStore seems to not work
    #jcurlOpts+=(-Djavax.net.ssl.trustStoreType=KeychainStore)
    jcurlOpts+=(-Djavax.net.ssl.keyStoreType=KeychainStore)
  elif [[ ! -f "$systemTrustStore" ]]; then
    jcurlOpts+=(-Djavax.net.ssl.trustStoreType=PKCS12)
    jcurlOpts+=("-Djavax.net.ssl.trustStore=$systemTrustStore")
  fi

  local javaCmd="${JAVA_HOME:-}/bin/java"

  if [[ ! -x "${javaCmd}" ]]; then
    if which java &> /dev/null; then
      javaCmd="java"
    else
      echo "invalid JAVA_HOME"
      return 1
    fi
  fi

  local jarFile=""
  jarFile="$(find "$__BASEDIR/target" -name 'jcurl-*-bundle.jar' | sort | tail -1)"
  "${javaCmd}" "${jcurlOpts[@]}" -jar "$jarFile" "$@"
  return $?
}

main "$@"
exit $?
