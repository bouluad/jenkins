import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardOpenOption

// Define the output CSV file path
def outputFile = '/path/to/jenkins_credentials_export.csv'  // Update this path as necessary

// Create or overwrite the output file
Files.write(Paths.get(outputFile), "ID,Type,Username/ID,Secret,Extra\n".getBytes(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)

def creds = CredentialsProvider.lookupCredentials(
    Credentials.class,
    Jenkins.instance,
    null,
    null
)

creds.each { c ->
    def id = c.id
    def type = c.getClass().getSimpleName()
    def usernameOrId = ""
    def secretValue = ""
    def extra = ""

    if (c instanceof UsernamePasswordCredentialsImpl) {
        usernameOrId = c.username
        secretValue = Secret.toString(c.password)
    } else if (c instanceof StringCredentialsImpl) {
        secretValue = Secret.toString(c.secret)
    } else if (c instanceof BasicSSHUserPrivateKey) {
        usernameOrId = c.username
        secretValue = c.privateKey
        if (c.passphrase) {
            extra = "Passphrase: ${Secret.toString(c.passphrase)}"
        }
    } else if (c instanceof CertificateCredentialsImpl) {
        usernameOrId = c.keyStoreSource.keyStoreAlias
        secretValue = "(binary content)"
        extra = "Password: ${Secret.toString(c.password)}"
    } else if (c instanceof AWSCredentialsImpl) {
        usernameOrId = c.accessKey
        secretValue = Secret.toString(c.secretKey)
    } else if (c instanceof DockerServerCredentials) {
        usernameOrId = c.username
        secretValue = Secret.toString(c.password)
        extra = "Email: ${c.email}, Server Address: ${c.serverAddress}"
    } else if (c instanceof GoogleRobotPrivateKeyCredentials) {
        usernameOrId = c.accountId
        secretValue = "(private key not displayed)"
    }

    def line = "${id},${type},${usernameOrId},${secretValue},${extra}\n"
    Files.write(Paths.get(outputFile), line.getBytes(), StandardOpenOption.APPEND)
}

println("Credentials have been exported to ${outputFile}")
