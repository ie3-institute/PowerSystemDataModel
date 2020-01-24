#!/usr/bin/env sh

chmod u+x gradlew
if [ "$TRAVIS_BRANCH" = "master" ] && [ "$TRAVIS_PULL_REQUEST" = "false" ];
then
    echo "Merge PR into master. Calling all tests + creating reports."
    ./gradlew --refresh-dependencies clean spotlessCheck pmdMain pmdTest spotbugsMain spotbugsTest allTests jacocoTestReport jacocoTestCoverageVerification
elif [ "$TRAVIS_PULL_REQUEST" != "false" ]
then
    echo "Building PR $TRAVIS_PULL_REQUEST."
    echo "Skipping integration tests in pull request builds."
    ./gradlew --refresh-dependencies clean spotlessCheck pmdMain pmdTest spotbugsMain spotbugsTest allTests jacocoTestReport jacocoTestCoverageVerification
else
    echo "Building branch $TRAVIS_BRANCH."
    echo "Skipping integration tests in pull request builds."
    ./gradlew --refresh-dependencies clean spotlessCheck pmdMain pmdTest spotbugsMain spotbugsTest allTests jacocoTestReport jacocoTestCoverageVerification
fi