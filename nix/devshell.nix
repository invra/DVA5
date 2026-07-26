{
  kotlin-language-server,
  java-language-server,
  mkShell,
  gradle,
  jdk21,
  openssl,
  just,
  ... # capture inputs
}:
mkShell {
  buildInputs = [
    kotlin-language-server
    java-language-server
    gradle
    jdk21
    just
  ];

  nativeBuildInputs = [
    openssl
  ];
}
