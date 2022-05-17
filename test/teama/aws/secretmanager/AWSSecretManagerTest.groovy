package teama.aws.secretmanager

import org.junit.Before
import org.junit.Test

class AWSSecretManagerTest {

    AWSSecretManager awsSecretManager

    @Before
    void Before(){
        awsSecretManager = new AWSSecretManager()
    }

    @Test
    void testIfCreateSecretReturnsExpectedOutcome(){
        print (awsSecretManager.createSecret("demo/test/jenkins-rancher-secret", """{"username": "admin", "password":"password"}"""))
    }
}
