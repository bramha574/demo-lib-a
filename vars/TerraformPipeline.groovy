import example.com.pkg.Terraform

def call(body){
    def config = [:]
    body.resolveStrategy = Closure.DELEGATE_FIRST
    body.delegate = config
    body()

    Terraform terraform = new Terraform(this)

    pipeline{
        agent any

        stages{
            stage("Checkout"){
                steps{
                    script{
                        git (branch: 'main', credentialsId: 'github-creds', url: 'git@github.com:nguyenstrai/learn_terraform.git')
                    }
                }
            }
            stage("Init"){
                steps{
                    script{
                        terraform.init()
                    }
                }
            }
            stage("Plan"){
                steps{
                    script{
                        terraform.plan()
                    }
                }
            }
            stage("Apply"){
                steps{
                    script{
                       terraform.apply()
                    }
                }
            }
        }

        post{
            always{
                script{
                    echo "post step"
                }
            }
        }
    }
}