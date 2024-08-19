import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*

// Define a map to store all credentials
def credentialsMap = [:]

def creds = CredentialsProvider.lookupCredentials(
    Credentials.class,
    Jenkins.instance,
    null,
    null
)

creds.each { c ->
    def credentialData = [:]
    credentialData['type'] = c.getClass().getSimpleName()

    if (c instanceof UsernamePasswordCredentialsImpl) {
        credentialData['username'] = c.username
        credentialData['password'] = Secret.toString(c.password)
    } else if (c instanceof StringCredentialsImpl) {
        credentialData['secret'] = Secret.toString(c.secret)
    } else if (c instanceof BasicSSHUserPrivateKey) {
        credentialData['username'] = c.username
        credentialData['privateKey'] = c.privateKey
        if (c.passphrase) {
            credentialData['passphrase'] = Secret.toString(c.passphrase)
        }
    } else if (c instanceof CertificateCredentialsImpl) {
        credentialData['alias'] = c.keyStoreSource.keyStoreAlias
        credentialData['password'] = Secret.toString(c.password)
        credentialData['certificate'] = "(binary content)" // Handle binary content appropriately
    } else if (c instanceof AWSCredentialsImpl) {
        credentialData['accessKey'] = c.accessKey
        credentialData['secretKey'] = Secret.toString(c.secretKey)
    } else if (c instanceof DockerServerCredentials) {
        credentialData['username'] = c.username
        credentialData['password'] = Secret.toString(c.password)
        credentialData['email'] = c.email
        credentialData['serverAddress'] = c.serverAddress
    } else if (c instanceof GoogleRobotPrivateKeyCredentials) {
        credentialData['accountId'] = c.accountId
        credentialData['privateKey'] = "(private key not displayed)"
    } else {
        credentialData['unsupportedType'] = true
    }

    credentialsMap[c.id] = credentialData
}

// Print the dictionary as a string
println(credentialsMap)
