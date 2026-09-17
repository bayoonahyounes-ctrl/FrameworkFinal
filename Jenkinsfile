pipeline {
    agent any

    tools {
        maven 'Maven3'
    }

    parameters {
        choice(
            name: 'SUITE',
            choices: [
                'testng-smoke.xml',
                'testng.xml',
                'testng-parallel.xml'
            ],
            description: 'Which TestNG suite file to run'
        )

        booleanParam(
            name: 'HEADLESS',
            defaultValue: true,
            description: 'Run Chrome headless'
        )
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                bat 'mvn -B -DskipTests clean compile'
            }
        }

        stage('Test') {
            steps {
                bat "mvn -B test -DsuiteXmlFile=${params.SUITE} -Dheadless=${params.HEADLESS}"
            }

            post {
                always { 
                    echo 'Checking Allure results...' 
                    bat 'if exist target\\allure-results dir /s /b target\\allure-results'                    
                }
            }
        }
    }

    post {
        always {
            echo 'Publishing test results...'

            junit(
                testResults: 'target/surefire-reports/*.xml',
                allowEmptyResults: true
            )

            allure(
                includeProperties: false,
                jdk: '',
                results: [
                    [path: 'target/allure-results']
                ]
            )

            archiveArtifacts(
                artifacts: 'target/screenshots/**',
                allowEmptyArchive: true
            )
        }
    }
}
