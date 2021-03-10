# Templates of `github_release.yml`

Copy the content into a file `/.github/workflows/github_release.yml` in your project.

## For Java Maven Project

See [an example from this project](../../.github/workflows/github_release.yml).

## For Scala Sbt Project

```
name: GitHub Release

on:
 workflow_dispatch:
    inputs:
      upload_url:
        description: 'Upload URL'
        required: true
      asset_name:
        description: 'Asset file name'
        required: true
      asset_path:
        description: 'Asset file path'
        required: true

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      - name: Setup Scala
        uses: olafurpg/setup-scala@v10
        with:
          java-version: adopt@1.11
      - name: Assembly
        run: sbt assembly

      - name: Upload Release Asset
        uses: actions/upload-release-asset@v1
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
        with:
          upload_url: ${{ github.event.inputs.upload_url }}
          asset_path: ${{ github.event.inputs.asset_path }}
          asset_name: ${{ github.event.inputs.asset_name }}
          asset_content_type: application/java-archive
```