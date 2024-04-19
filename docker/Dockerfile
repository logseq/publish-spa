ARG BB_VERSION=1.2.174
ARG CLOJURE_VERSION=temurin-11-tools-deps-1.11.1.1208-bullseye-slim

FROM babashka/babashka:${BB_VERSION} AS BB
FROM clojure:${CLOJURE_VERSION}

ARG LOGSEQ_PUBSPA_VERSION=0.3.1
ARG LOGSEQ_VERSION=0.10.6

WORKDIR /opt

# Install dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    ca-certificates \
    apt-transport-https \
    gpg \
    build-essential libcairo2-dev libpango1.0-dev libjpeg-dev libgif-dev librsvg2-dev \
    && rm -rf /var/lib/apt/lists/* \
    && mkdir -p /usr/local/bin

# Install babashka
COPY --from=BB /opt/babashka-metabom.jar /opt/babashka-metabom.jar
COPY --from=BB /usr/local/bin/bb /usr/local/bin/bb

# install NodeJS & yarn
RUN curl -sL https://deb.nodesource.com/setup_18.x | bash -

RUN curl -sS https://dl.yarnpkg.com/debian/pubkey.gpg | gpg --dearmor | \
    tee /etc/apt/trusted.gpg.d/yarn.gpg && \
    echo "deb https://dl.yarnpkg.com/debian/ stable main" | \
    tee /etc/apt/sources.list.d/yarn.list && \
    apt-get update && apt-get install -y nodejs yarn wget

# Initialize logseq-publish-spa
RUN mkdir -p /opt/logseq-publish-spa && \
    wget https://github.com/logseq/publish-spa/archive/refs/tags/v${LOGSEQ_PUBSPA_VERSION}.tar.gz && \
    tar -axvf *.tar.gz --directory /opt/logseq-publish-spa --strip-components=1 && \
    rm *.tar.gz && \
    cd /opt/logseq-publish-spa && yarn install --frozen-lockfile

# Initialize logseq
RUN mkdir -p /opt/logseq-logseq && \
    wget https://github.com/logseq/logseq/archive/refs/tags/${LOGSEQ_VERSION}.tar.gz && \
    tar -axvf *.tar.gz --directory /opt/logseq-logseq --strip-components=1 && \
    rm *.tar.gz && \
    cd /opt/logseq-logseq && yarn install --frozen-lockfile && \
    yarn gulp:build && clojure -M:cljs release publishing

# Fetching nbb deps
RUN cd /opt/logseq-publish-spa && \
    yarn nbb-logseq -e ':fetching-deps'

# Default Environments
ENV PUB_OUT_DIR=/out
ENV PUB_GRAPH_DIR=/graph

# Entrypoint
COPY entrypoint.sh .
RUN chmod +x entrypoint.sh
ENTRYPOINT ["/opt/entrypoint.sh"]
