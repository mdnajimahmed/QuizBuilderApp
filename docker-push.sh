GIT_HASH="$(git rev-parse HEAD)"
GIT_HASH=$(cut -c 1-6 <<< "$GIT_HASH" )
./gradlew clean build -x test
docker build -t ivplay4689/quizapp:$GIT_HASH .
docker push ivplay4689/quizapp:$GIT_HASH