def terraformInit(folder) {
  dir(folder) {
    sh 'terraform init'
  }
}

def terraformPlan(folder) {
  dir(folder) {
    sh 'terraform plan -out=plan.tfplan'
    def inputMessage = "Ready to apply the Terraform changes? Click 'Approve' to proceed, or 'Deny' to cancel."
    def inputId = "terraform-plan-approval"
    def inputType = "Approval"
    def inputParams = [
      [$class: "BooleanParameterDefinition", defaultValue: false, description: "", name: "Apply"],
      [$class: "StringParameterDefinition", defaultValue: "", description: "Username of the user who approved or denied the Terraform plan", name: "SubmittedBy"]
    ]

    def userInput = input(message: inputMessage, id: inputId, submitter: "users", parameters: inputParams, inputType: inputType)

    if (!userInput.Apply) {
      currentBuild.result = "ABORTED"
      error("Terraform plan approval denied by ${userInput.SubmittedBy}")
      //mail to: env.TERRAFORM_EMAIL_ADDRESS, subject: "Terraform plan approval denied by ${userInput.SubmittedBy}", body: "The Terraform plan was denied by ${userInput.SubmittedBy}."
    } else {
      echo "Terraform plan approved by ${userInput.SubmittedBy}"
      //mail to: env.TERRAFORM_EMAIL_ADDRESS, subject: "Terraform plan approved by ${userInput.SubmittedBy}", body: "The Terraform plan was approved by ${userInput.SubmittedBy}."
    }
  }
}

def terraformApply(folder) {
  dir(folder) {
    try {
      sh 'terraform apply -auto-approve plan.tfplan'
    } catch (err) {
      currentBuild.result = 'FAILURE'
      throw err
    }
  }
}