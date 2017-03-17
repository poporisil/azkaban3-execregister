#!/usr/bin/env bash

RELEASE_VERSION=$(git describe --tags $(git rev-list --tags --max-count=1))
echo RELEASE_VERSION=${RELEASE_VERSION}

echo "# push azkaban3-execregister image..."
docker push poporisil/azkaban3-execregister:${RELEASE_VERSION}
docker tag poporisil/azkaban3-execregister:${RELEASE_VERSION} poporisil/azkaban3-execregister:latest
docker push poporisil/azkaban3-execregister:latest
