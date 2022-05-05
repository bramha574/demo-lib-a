package example.com.pkg

import example.com.pkg.utils.Utilities

class Terraform {
    def dsl

    String awsAccount = "992247318733"
    String roleArn = "arn:aws:iam::992247318733:role/demo-admin-role"

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
        dsl.dir("rancher"){
            dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                utilities.shellCommand("""terraform plan ${getVarString()}""", "Run Terraform Plan")
            }
        }
    }

    void apply(){
        if(applyPlan()){
            dsl.dir("rancher"){
                dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                    utilities.shellCommand("""terraform apply ${getVarString()}""", "Run Terraform Apply")
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

    private Map userInput(){
        def userInfo = dsl.input(message: "Enter the below info to move on", parameters: [
                dsl.string(description: "Enter Terraform Vars. Eg: instance_type=t2.small,prefix=dev", name: "tfVars", trim:true)
        ])

        dsl.echo(userInfo)

        return userInfo
    }

    private String getVarString(){
        List varList = []
        List varSplitList = userInput().tfVars.toString().replace(" ", "").split(",")

        for (String arg in varSplitList){
            if(arg.length()>0){
                String item = """-var "${arg}" """
                varList.add(item)
            }
        }

        return varList.join(" ")

        //["""-var instance_type="t2.small" """, """-var prefix="dev" """]
    }
}
