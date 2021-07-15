# Templates of `release_droid_upload_github_release_assets.yml`

If the programming language of your project doesn't have an advanced support (default language), it's your responsibility to prepare a build compatible with your language.
Please refer to the existing examples and use the similar steps: Checkout -> Setup environment -> Assembly -> Upload assets to the GitHub release draft

## For Java Maven Project

See [an example from this project](../../../.github/workflows/release_droid_upload_github_release_assets.yml).

## For Scala Sbt Project

```
name: Release Droid - Upload GitHub Release Assets

on:
  workflow_dispatch:
    inputs:
      upload_url:
        description: 'Upload URL'
        required: true

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
      - name: Upload assets to the GitHub release draft
        uses: shogo82148/actions-upload-release-asset@v1
        with:
          upload_url: ${{ github.event.inputs.upload_url }}
          asset_path: target/scala*/stripped/*.jar
```