#!/bin/sh -ex
#the script required the node semver command line to be installed
: ${2?"Usage: $0 <major|minor|patch> <major|minor|patch> : the first arg is for the release increase and the second is for the snashot increase"}

./mvnw scm:check-local-modification

current=$(git describe --abbrev=0)
release=$(semver -i $1 ${current})
next=$(semver -i $2 ${release})

# release
./mvnw versions:set -D newVersion=${release} -DgenerateBackupPoms=false
git add .
git commit -m "Release ${release}"
#./mvnw -U -V clean deploy
./mvnw scm:tag

# next development version
./mvnw versions:set -D newVersion=${next}-SNAPSHOT -DgenerateBackupPoms=false
git add .
git commit -m "Development ${next}-SNAPSHOT"

#git push
#git push --tags
