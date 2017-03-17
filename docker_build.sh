#!/usr/bin/env bash

RELEASE_VERSION=$(git describe --tags $(git rev-list --tags --max-count=1))
echo RELEASE_VERSION=${RELEASE_VERSION}

echo "# build azkaban3-execregister image..."
docker build --rm=false -t poporisil/azkaban3-execregister:${RELEASE_VERSION} .
