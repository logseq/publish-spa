## Description

This is a [github action](https://github.com/features/actions) to publish a
Logseq graph as a [publishing](https://docs.logseq.com/#/page/publishing) Single
Page Application (SPA). This action can also be run as a [CLI](#cli).

## Usage

### Github Action

To setup this action, [enable github pages on your
repository](https://docs.github.com/en/pages/quickstart) and add the file
`.github/workflows/publish.yml` to your graph's github repository with the
content:

``` yaml
on: [push]

permissions:
  contents: write
jobs:
  test:
    runs-on: ubuntu-latest
    name: Publish Logseq graph
    steps:
      - uses: actions/checkout@v4
      - uses: logseq/publish-spa@v0.3.0
      - name: Add a nojekyll file # to make sure asset paths are correctly identified
        run: touch $GITHUB_WORKSPACE/www/.nojekyll
      - name: Deploy 🚀
        uses: JamesIves/github-pages-deploy-action@v4
        with:
          folder: www
```

That's it! Your graph will publish on future git pushes by pushing to the
`gh-pages` branch. Don't forget to [configure your
graph](https://docs.logseq.com/#/page/publishing/block/configuration) if you're
not seeing the pages you expect to see.

NOTE: The above example may not have the latest version of this action. See
[CHANGELOG.md](CHANGELOG.md) for released versions. If you'd prefer to always be
on the latest version of this action, use `logseq/publish-spa@main` but be
careful as there could be breaking changes.

#### Action Inputs

This action takes the following inputs:

```yaml
- uses: logseq/publish-spa@main
  with:
    graph-directory: my-logseq-notes
    output-directory: out
    version: nightly
```

This action has the following inputs:

##### `graph-directory`

**Required:** Root of the graph directory. Defaults to `.`.

##### `output-directory`

**Required:** Directory where graph is published. Defaults to `www`.

##### `version`

**Required:** Specifies the version of Logseq to build the frontend. This can be
a git tag (version) or a specific git SHA. Defaults to `0.10.6`.

Note: A version before 0.9.2 is not supported as Logseq started supporting this
action with 0.9.2.

##### `theme-mode`

Optional: Theme mode for frontend. Can be "dark" or "light". Defaults to "light".

##### `accent-color`

Optional: Accent color for frontend. Can be one of "tomato", "red", "crimson", "pink", "plum", "purple", "violet", "indigo", "blue", "cyan", "teal", "green", "grass", "orange", "brown". Defaults to "blue".

### CLI

To use this as a CLI locally, first install
[babashka](https://github.com/babashka/babashka#installation) and
[clojure](https://clojure.org/guides/install_clojure). Then:

```sh
$ git clone https://github.com/logseq/publish-spa
# If you have yarn 1.X:
$ cd publish-spa && yarn install
$ yarn global add $PWD

# Otherwise use npm:
$ cd publish-spa && npm i -g
```

This CLI depends on Logseq being checked out locally in order to build the
static directory for it. If you haven't built the static directory, you'll need
to do it once (takes some time):

```sh
$ git clone https://github.com/logseq/logseq && cd logseq
# Switch to a stable version
$ git checkout 0.10.6
# Install deps and build static directory
$ yarn install --frozen-lockfile && yarn gulp:build && clojure -M:cljs release publishing
```

Then use the CLI from any logseq graph directory!
```sh
$ logseq-publish-spa out
Parsing 306 files...
Export public pages and publish assets to out successfully 🎉
```

## Development

This github action use [nbb-logseq](https://github.com/logseq/nbb-logseq) and
[nbb compatible libraries](https://github.com/logseq/logseq/tree/master/deps) to build up a
Logseq database and then generate html for the publishing app.

When viewing a published graph directory, it's highly recommended to use a local http server as multiple features are known to not work (e.g. assets and slides) when viewed as a static file. For example, to view the published graph `out` from the above example:
```sh
$ python3 -m http.server 8080 -d out
```

## LICENSE
See LICENSE.md

## Additional Links
* https://github.com/pengx17/logseq-publish - Used to be the most popular action for publishing Logseq graphs. Now archived. Thanks to @pengx17 for his great work with this. :heart:
* https://github.com/L-Trump/logseq-publish-docker - Docker image of this repository. This
  repository [can be used with GitLab](https://github.com/logseq/publish-spa/issues/18#issuecomment-2100932405).
* https://github.com/logseq/graph-validator - Github action that this one is modeled after
* https://github.com/logseq/docs - Logseq graph that uses this action
