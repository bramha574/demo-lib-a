package example.com.pkg.utils

class Utilities implements Serializable{

    private def dsl

    Utilities(def dsl){
        this.dsl = dsl
    }

    void shellCommand(String command, String label){
        if(dsl.isUnix()){
            dsl.sh(script: command, label: label)
        }
        else{
            dsl.sh(script: command, label: label)
        }
    }

}
