# Templates of `release_droid_prepare_original_checksum.yml`

If the programming language of your project doesn't have an advanced support (default language), it's your responsibility to prepare a build compatible with your language.
Please refer to the existing examples and use the similar steps: Checkout -> Setup environment -> Assembly -> Running tests -> Prepare checksum -> Upload checksum to the artifactory

## For Java Maven Project

See [an example from this project](../../../.github/workflows/release_droid_prepare_original_checksum.yml).

## For Scala Sbt Project

```
name: Release Droid - Prepare Original Checksum

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
      - name: Assembly with SBT
        run: sbt assembly
      - name: Running tests
        run: sbt test it:test
      - name: Prepare checksum
        run: find target/scala*/stripped -name *.jar -exec sha256sum "{}" + > original_checksum
      - name: Upload checksum to the artifactory
        uses: actions/upload-artifact@v2
        with:
          name: original_checksum
          retention-days: 5
          path: original_checksum
```