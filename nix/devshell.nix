{
  kotlin-language-server,
  java-language-server,
  gradle_9,
  mkShell,
  openssl,
  jdk25,
  just,
  ... # capture inputs
}:
mkShell {
  buildInputs = [
    kotlin-language-server
    java-language-server
    gradle_9
    jdk25
    just
  ];

  nativeBuildInputs = [
    openssl
  ];
}
