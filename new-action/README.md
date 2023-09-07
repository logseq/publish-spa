## Description
This README.md file provides instructions for publishing your LogSeq Graph site in a shared <owner>.github.io location using a GitHub Action. It also includes information about setting up the required API key and adding an index.html file for easy navigation.

### Github Action
To publish your LogSeq Graph in a remote repository, you need to provide the GitHub Action with an API key called GH_TOKEN. Follow these steps to configure the key:

Create the GH_TOKEN secret in the <owner>.github.io repository, under "Settings > Secrets and variables > Action".
Name the secret GH_TOKEN and add the API key as its value.

Use the following YAML code as a template for the GitHub Action:
``` yaml
on: [push]

permissions:
  contents: write

jobs:
  test:
    runs-on: ubuntu-latest
    name: Publish Logseq graph
    steps:
      - uses: actions/checkout@v3
      - uses: logseq/publish-spa@v0.2.0
      - name: Add a nojekyll file 
        run: touch $GITHUB_WORKSPACE/www/.nojekyll
      - name: Getting Folder name for public
        run: |
          repo_full_name="${{ github.repository }}"
          repo_name="${repo_full_name%%/*}"
          repo_folder="${repo_full_name#*/}"
          repo_name_lowercase=$(echo "$repo_name" | tr '[:upper:]' '[:lower:]')
          echo "REPO_FOLDER=${repo_folder}" >> $GITHUB_ENV
          echo "REPO_REPO=${repo_name}/${repo_name_lowercase}.github.io" >> $GITHUB_ENV
      - name: Deploy 🚀
        uses: JamesIves/github-pages-deploy-action@v4
        with:
          folder: www
          token: ${{ secrets.GH_TOKEN }}
          repository-name: ${{ env.REPO_REPO }}
          branch: main
          target-folder: ${{ env.REPO_FOLDER }}
```
### Index
Since each Graph has its own subfolder, there is no basic index file for the site. Browsing directly into a subfolder will result in a 404 error. However, accessing newowner.github.io/LogSeq will work.

To provide easy navigation, I have included an index.html file that can be placed in the root of newowner.github.io. This file dynamically lists all the subfolders as buttons for accessing the graphs.

You need to replce <NewOwner/newowner.github.io> in fetch('https://api.github.com/repos/<NewOwner/newowner.github.io>/contents/') for this to work.
