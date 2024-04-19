#!/bin/bash

# Check if logseq-publish-spa directory exists
if [ ! -d "/opt/logseq-publish-spa" ]; then
	echo "Error: The directory 'logseq-publish-spa' does not exist"
	exit 1
fi

# Check if logseq-logseq directory exists
if [ ! -d "/opt/logseq-logseq" ]; then
	echo "Error: The directory 'logseq-logseq' does not exist"
	exit 1
fi

# Get the options from the environment variable or set a default
PUB_OUT_DIR=${PUB_OUT_DIR:-/out}
PUB_GRAPH_DIR=${PUB_GRAPH_DIR:-/graph}
PUB_THEME=${PUB_THEME:-light}
PUB_ACCENT_COLOR=${PUB_ACCENT_COLOR:-blue}

if [ ! -d "$PUB_GRAPH_DIR" ]; then
	echo "Error: The graph directory does not exist"
	exit 1
fi

if [ ! -d "$PUB_OUT_DIR" ]; then
	mkdir -p "$PUB_OUT_DIR"
fi

cd logseq-publish-spa
node publish_spa.mjs $PUB_OUT_DIR --static-directory /opt/logseq-logseq/static \
	--directory $PUB_GRAPH_DIR --theme-mode $PUB_THEME --accent-color $PUB_ACCENT_COLOR
