import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*

// Fetch all credentials stored in Jenkins
def creds = com.cloudbees.plugins.credentials.CredentialsProvider.lookupCredentials(
    com.cloudbees.plugins.credentials.Credentials.class,
    Jenkins.instance,
    null,
    null
)

// Iterate over each credential and print relevant information
creds.each { c ->
    if (c instanceof UsernamePasswordCredentialsImpl) {
        println("ID: ${c.id} - Type: Username/Password")
        println("  Username: ${c.username}")
        println("  Password: ${Secret.toString(c.password)}")
    } else if (c instanceof StringCredentialsImpl) {
        println("ID: ${c.id} - Type: Secret Text")
        println("  Secret: ${Secret.toString(c.secret)}")
    } else if (c instanceof BasicSSHUserPrivateKey) {
        println("ID: ${c.id} - Type: SSH Username with Private Key")
        println("  Username: ${c.username}")
        println("  Private Key: ${c.privateKey}")
        if (c.passphrase) {
            println("  Passphrase: ${Secret.toString(c.passphrase)}")
        }
    } else if (c instanceof CertificateCredentialsImpl) {
        println("ID: ${c.id} - Type: Certificate")
        println("  Alias: ${c.keyStoreSource.keyStoreAlias}")
        println("  Password: ${Secret.toString(c.password)}")
        // Additional fields for certificate credentials could be added here if needed
    } else if (c instanceof AWSCredentialsImpl) {
        println("ID: ${c.id} - Type: AWS Credentials")
        println("  Access Key: ${c.accessKey}")
        println("  Secret Key: ${Secret.toString(c.secretKey)}")
    } else if (c instanceof DockerServerCredentials) {
        println("ID: ${c.id} - Type: Docker Server Credentials")
        println("  Username: ${c.username}")
        println("  Password: ${Secret.toString(c.password)}")
        println("  Email: ${c.email}")
        println("  Server Address: ${c.serverAddress}")
    } else if (c instanceof GoogleRobotPrivateKeyCredentials) {
        println("ID: ${c.id} - Type: Google Service Account")
        println("  Account ID: ${c.accountId}")
        // The private key is typically stored as a JSON key file, not directly viewable as text
        println("  Private Key: (not displayed)")
    } else {
        println("ID: ${c.id} - Type: ${c.getClass().getSimpleName()} (Unsupported credential type)")
        // You can extend this to cover more credential types as needed
    }
    println("---------------------------------------------------")
}
