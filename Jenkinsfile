node {
  stage 'Stage Checkout'
  checkout scm
  sh 'git submodule update --init'  
  sh './gradlew build'
}
