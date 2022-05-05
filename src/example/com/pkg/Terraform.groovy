package example.com.pkg

import example.com.pkg.utils.Utilities

class Terraform {
    static def dsl

    private String awsAccount = "992247318733"
    private String roleArn = "arn:aws:iam::992247318733:role/demo-admin-role"
    private static String tfVars = null

    private Utilities utilities = new Utilities(dsl)

    Terraform(def dsl){
        this.dsl = dsl
    }

    void init(){
        dsl.dir("rancher"){
            dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                utilities.shellCommand("""terraform init""", "Run Terraform Init")
            }
        }
    }

    void plan(){
        setVarString()
        dsl.dir("rancher"){
            dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                utilities.shellCommand("""terraform plan ${tfVars}""", "Run Terraform Plan")
            }
        }
    }

    void apply(){
        if(applyPlan()){
            dsl.dir("rancher"){
                dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                    utilities.shellCommand("""terraform apply ${tfVars} -auto-approve""", "Run Terraform Apply")
                }
            }
        }
        else {
            dsl.echo("User Did not approve the plan")
        }
    }

    private boolean applyPlan(){
        boolean approvalInput = false
        dsl.timeout(time: "5", unit: 'MINUTES') {
            approvalInput = dsl.input (message: "Want to run plan?", parameters: [
                    dsl.booleanParam(defaultValue: false, description: "Then Approve Here", name: 'UserAcceptance')])
        }
        return  approvalInput
    }

    private static def userInput(){
        def userInfo = dsl.input(message: "Enter the below info to move on", parameters: [
                dsl.choice(choices: ['dev', 'prod', 'sandbox'], description: "Select The Prefix", name: "prefix"),
                dsl.choice(choices: ['v1.21.11+k3s1', 'v1.22.11+k3s1'], description: "Select The Kubernetes Lite Version For Rancher", name: "kubernetesVersion"),
                dsl.password(defaultValue: '', description: 'Enter the RancherPassword. The password should be greater than 12', name: 'rancherPassword'),
                dsl.booleanParam(description: 'Check this if you want to add windows node', name: 'addWindowsNode')
        ])

        dsl.echo(userInfo.toString())

        return userInfo
    }

    private static void setVarString(){
        def userInfo = userInput()
        if(!tfVars) {
            tfVars = "-var prefix=${userInfo.prefix} -var kubernetes_version=${userInfo.kubernetesVersion} -var rancher_password=${userInfo.rancherPassword} -var add_windows_node=${userInfo.addWindowsNode}"
        }
    }

    void destroy(){
        dsl.dir("rancher"){
            dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                utilities.shellCommand("""terraform destroy ${tfVars} -auto-approve""", "Run Terraform Plan")
            }
        }
    }
}
