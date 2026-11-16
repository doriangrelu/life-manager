# Image de développement : Ubuntu + GraalVM 25 (Java et native-image) + Bun.
FROM ghcr.io/graalvm/native-image-community:25 AS graalvm
FROM oven/bun:1.3 AS bun

FROM mcr.microsoft.com/devcontainers/base:ubuntu-24.04

# Outils C nécessaires à la compilation native GraalVM.
RUN apt-get update \
    && DEBIAN_FRONTEND=noninteractive apt-get install -y --no-install-recommends build-essential zlib1g-dev \
    && rm -rf /var/lib/apt/lists/*

ENV JAVA_HOME=/opt/graalvm
ENV PATH="${JAVA_HOME}/bin:${PATH}"
COPY --from=graalvm /usr/lib64/graalvm/graalvm-community-java25 ${JAVA_HOME}
COPY --from=bun /usr/local/bin/bun /usr/local/bin/bun
RUN ln -s /usr/local/bin/bun /usr/local/bin/bunx

# Cache Maven persistant (volume monté par devcontainer.json), possédé par l'utilisateur vscode.
RUN mkdir -p /home/vscode/.m2 && chown vscode:vscode /home/vscode/.m2
