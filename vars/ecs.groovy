def toECS()
{
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: "ng-aws-cred"],
    //accessKeyVariable: 'AWS_ACCESS_KEY_ID',
    //secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'],
    file(credentialsId: "ng-env-file", variable: 'env_files')])
        {
            docker.withRegistry("https://${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/${ECR_NAME}", "ecr:${AWS_REGION}:${AWS_ACCOUNT_ID}") {
                def yaml = libraryResource 'compose.yaml'
                
                writeFile file: '.env', text: readFile(env_files)
                writeFile file: "docker-compose.yaml", text: yaml
                
                sh '''
                    aws ecs list-clusters
                    docker context create ecs ecs --from-env
                    docker context use ecs
                    docker compose -p "aline-ng-ecs-new" --env-file .env up
                '''
            }
        }
}