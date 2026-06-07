pipeline {
    agent { label 'docker' }

    environment {
        COMPOSE_FILE = 'docker-compose.test.yml'
        CI = 'true'
        BASE_URL = 'http://localhost'
        API_BASE_URL = 'http://localhost:8080'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Prepare environment') {
            steps {
                sh 'cp .env.example .env'
                sh 'chmod +x backend/mvnw scripts/wait-for-services.sh'
            }
        }

        stage('Build and start stack') {
            steps {
                sh 'docker compose -f docker-compose.test.yml up -d --build'
                sh './scripts/wait-for-services.sh'
            }
            post {
                failure {
                    sh 'docker compose -f docker-compose.test.yml logs --tail=100 backend frontend postgres || true'
                }
            }
        }

        stage('Tests') {
            parallel {
                stage('API tests - Karate') {
                    steps {
                        dir('backend') {
                            sh './mvnw verify -Ptest-api -Dkarate.env=docker'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'backend/target/failsafe-reports/*.xml'
                            publishHTML(target: [
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'backend/target/karate-reports',
                                reportFiles: 'karate-summary.html',
                                reportName: 'Karate Report'
                            ])
                            archiveArtifacts artifacts: 'backend/target/karate-reports/**/*', allowEmptyArchive: true
                        }
                    }
                }

                stage('E2E tests - Playwright') {
                    steps {
                        dir('e2e') {
                            sh 'npm ci'
                            sh 'npx playwright install --with-deps chromium'
                            sh 'npm run test:ci'
                        }
                    }
                    post {
                        always {
                            junit allowEmptyResults: true, testResults: 'e2e/reports/junit.xml'
                            publishHTML(target: [
                                allowMissing: true,
                                alwaysLinkToLastBuild: true,
                                keepAll: true,
                                reportDir: 'e2e/playwright-report',
                                reportFiles: 'index.html',
                                reportName: 'Playwright Report'
                            ])
                            archiveArtifacts artifacts: '''
                                e2e/test-results/**/*
                                e2e/playwright-report/**/*
                            ''', allowEmptyArchive: true, fingerprint: true
                        }
                    }
                }
            }
        }
    }

    post {
        always {
            sh 'docker compose -f docker-compose.test.yml down -v || true'
        }
        failure {
            archiveArtifacts artifacts: '''
                e2e/test-results/**/*
                backend/target/karate-reports/**/*
            ''', allowEmptyArchive: true
        }
    }
}
