def deployToEKSFrontend()
{
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: "ng-aws-cred"]
    ])
    {
        def file = libraryResource "frontend.yaml"
        writeFile file: "deployment.yaml", text: file
        sh "aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER_NAME}"
        sh "envsubst < deployment.yaml | kubectl apply -f -"
    }
}

def deployToEKSBackend()
{
    withCredentials([[$class: 'AmazonWebServicesCredentialsBinding',
    credentialsId: "ng-aws-cred"]
    ])
    {
        def file = libraryResource "backend.yaml"
        writeFile file: "deployment.yaml", text: file
        sh '''aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER_NAME}
        cat deployment.yaml
        envsubst < "deployment.yaml" > "deployment-subst.yaml"
        cat deployment-subst.yaml
        kubectl apply -f deployment-subst.yaml'''
    }
}