// Pipeline: BPMN -> tabla de flujos + codigo de pruebas -> ejecucion Selenium/Cucumber -> reportes
pipeline {
    agent any

    // Descomenta si en "Manage Jenkins > Tools" configuraste un JDK 17 y un Maven con estos nombres.
    // Si no, el agente debe tener java y mvn en el PATH.
    // tools {
    //     jdk 'JDK17'
    //     maven 'Maven3'
    // }

    options {
        timeout(time: 30, unit: 'MINUTES')
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Generar tabla de flujos y features') {
            steps {
                mvn 'compile exec:java'
            }
        }

        stage('Pruebas Selenium + Cucumber') {
            steps {
                // Los escenarios de zip invalido fallan a proposito (defecto real de SauceDemo):
                // no detenemos el pipeline; el paso junit marca el build como UNSTABLE.
                mvn 'test -Dheadless=true -Dmaven.test.failure.ignore=true'
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true, testResults: 'target/surefire-reports/*.xml'
            archiveArtifacts artifacts: 'target/cucumber-report.html, output/flow_table.csv',
                             allowEmptyArchive: true, fingerprint: true
        }
    }
}

// Ejecuta Maven en agentes Linux o Windows.
def mvn(String args) {
    if (isUnix()) {
        sh "mvn -B ${args}"
    } else {
        bat "mvn -B ${args}"
    }
}
