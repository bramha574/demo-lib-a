package example.com.pkg

class Terraform {
    def dsl

    String awsAccount = "992247318733"
    String roleArn = "arn:aws:iam::992247318733:role/demo-admin-role"

    Terraform(def dsl){
        this.dsl = dsl
    }

    void init(){
        dsl.dir("dir name"){
            dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                dsl.bat """terraform init """
            }
        }
    }

    void plan(){
        dsl.dir("dir name"){
            dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                dsl.bat """terraform plan ${getVarString()}"""
            }
        }
    }

    void apply(){
        if(applyPlan()){
            dsl.dir("dir name"){
                dsl.withAWS(roleAccount:awsAccount, role:roleArn) {
                    dsl.bat """terraform apply ${getVarString()}"""
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
        Map userInfo = dsl.input(message: "Enter the below info to move on", parameters: [
                dsl.string(description: "Enter Terraform Vars. Eg: instance_type=t2.small,prefix=dev", name: "tfVars", trim:true)
        ])

        return userInfo
    }

    private String getVarString(){
        List varList = []
        List varSplitList = userInput().tfVars.replace(" ", "").split(",")

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
