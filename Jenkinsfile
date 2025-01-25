pipeline {
    agent any

    stages {
        stage('package'){
            steps {
                git 'https://github.com/LibreczBernadett-Magnet/employees'

                sh './mvnw clean package'
            }
        }
        stage('test') {
            step{
                sh './mvnw  verify'
            }
        }
    }

}