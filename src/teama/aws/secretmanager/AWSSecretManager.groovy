package teama.aws.secretmanager

import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.CreateBucketRequest
import software.amazon.awssdk.services.s3.model.CreateBucketResponse
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.CreateSecretResponse
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.DeleteSecretResponse
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretRequest
import software.amazon.awssdk.services.secretsmanager.model.DescribeSecretResponse
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueRequest
import software.amazon.awssdk.services.secretsmanager.model.PutSecretValueResponse


class AWSSecretManager implements Serializable{

    AWSSecretManager(){

    }

    SecretsManagerClient secretManagerClient() {
        Region region = Region.US_EAST_1
        SecretsManagerClient localClient = SecretsManagerClient.builder().region(region).build()

        return localClient
    }

    String createSecret(String secretName, String secretValue){
        try {
            DescribeSecretRequest describeSecretRequest = DescribeSecretRequest.builder().
                    secretId(secretName)
                    .build()
            DescribeSecretResponse describeSecretResponse = secretManagerClient().describeSecret(describeSecretRequest)

            //update secret value
            // create putsecretvalue request
            // call putSecretValue method
            // print response

            return describeSecretResponse.name()
        }
        catch (Exception E) {
            CreateSecretRequest secretRequest = CreateSecretRequest.builder()
                    .name(secretName)
                    .description("This secret was created by the Jenkins")
                    .secretString(secretValue)
                    .build()

            CreateSecretResponse secretResponse = secretManagerClient().createSecret(secretRequest)

            return secretResponse.name()
        }
    }

    String getSecret(String secretName) {
        //return secretValue
    }

}
