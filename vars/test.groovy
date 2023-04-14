#!/usr/bin/env groovy

def nodeTest()
{
    sh 'sudo npm i --legacy-peer-deps'
    sh 'npm run test --coverage'
}

def sonarqubeNode()
{
    withSonarQubeEnv('sonarqube')
    {
      sh 'npm install sonarqube-scanner -g'
      sh 'sonar-scanner'
    }
}