# Templates of `release_droid_print_quick_checksum.yml`

If the programming language of your project doesn't have an advanced support (default language), it's your responsibility to prepare a build compatible with your language.
Please refer to the existing examples and use the similar steps: Checkout -> Setup environment -> Assembly -> Running tests -> Print checksum

## For Java Maven Project

See [an example from this project](../../../.github/workflows/release_droid_print_quick_checksum.yml).

## For Scala Sbt Project

```
name: Release Droid - Print Quick Checksum

on:
  workflow_dispatch:

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - name: Checkout the repository
        uses: actions/checkout@v2
      - name: Setup Scala
        uses: olafurpg/setup-scala@v10
        with:
          java-version: adopt@1.11
      - name: Assembly with SBT skipping tests
        run: sbt assembly
      - name: Prepare checksum
        run: echo 'checksum_start==';find target/scala*/stripped -name *.jar -exec sha256sum "{}" + | xargs;echo '==checksum_end'
```