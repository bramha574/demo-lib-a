package example.com.pkg

import example.com.pkg.utils.Utilities

class Terraform {
    def dsl

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

    private Map userInput(){
        def userInfo = dsl.input(message: "Enter the below info to move on", parameters: [
                dsl.string(description: "Enter Terraform Vars. Eg: instance_type=t2.small,prefix=dev", name: "tfVars", trim:true)
        ])

        dsl.echo(userInfo)

        return userInfo
    }

    private String setVarString(){
        if(!tfVars){
            List varList = []
            List varSplitList = userInput().toString().replace(" ", "").split(",")

            for (String arg in varSplitList){
                if(arg.length()>0){
                    String item = """-var "${arg}" """
                    varList.add(item)
                }
            }

            tfVars = varList.join(" ")
        }
    }
}
