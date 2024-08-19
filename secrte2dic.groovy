// Import necessary Jenkins classes
import jenkins.model.Jenkins
import hudson.util.Secret

// Import Credentials classes
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsProvider
import com.cloudbees.plugins.credentials.domains.Domain

// Import specific credential implementations
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.impl.StringCredentialsImpl
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.impl.BaseStandardCredentials

// Import AWS Credentials if AWS Credentials Plugin is installed
import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl

// Import Docker Credentials if Docker Plugin is installed
import com.nirima.jenkins.plugins.docker.utils.DockerServerCredentials

// Import Google OAuth Credentials if Google OAuth Plugin is installed
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotPrivateKeyCredentials

// Define a map to store all credentials
def credentialsMap = [:]

// Fetch all credentials from Jenkins
def creds = CredentialsProvider.lookupCredentials(
    Credentials.class,
    Jenkins.instance,
    null,
    null
)

creds.each { c ->
    def credentialData = [:]
    credentialData['type'] = c.getClass().getName()

    switch(c) {
        case { it instanceof UsernamePasswordCredentialsImpl }:
            credentialData['username'] = c.username
            credentialData['password'] = Secret.toString(c.password)
            break

        case { it instanceof StringCredentialsImpl }:
            credentialData['secret'] = Secret.toString(c.secret)
            break

        case { it instanceof BasicSSHUserPrivateKey }:
            credentialData['username'] = c.username
            credentialData['privateKey'] = c.privateKey
            credentialData['passphrase'] = c.passphrase ? Secret.toString(c.passphrase) : null
            break

        case { it instanceof CertificateCredentialsImpl }:
            credentialData['alias'] = c.keyStoreSource?.keyStoreAlias
            credentialData['password'] = Secret.toString(c.password)
            credentialData['certificate'] = c.keyStoreSource?.getKeyStore()?.toString() ?: "(binary content)"
            break

        case { it instanceof AWSCredentialsImpl }:
            credentialData['accessKey'] = c.accessKey
            credentialData['secretKey'] = Secret.toString(c.secretKey)
            break

        case { it instanceof DockerServerCredentials }:
            credentialData['username'] = c.username
            credentialData['password'] = Secret.toString(c.password)
            credentialData['email'] = c.email
            credentialData['serverAddress'] = c.serverAddress
            break

        case { it instanceof GoogleRobotPrivateKeyCredentials }:
            credentialData['accountId'] = c.accountId
            credentialData['privateKey'] = c.getPrivateKey()
            break

        default:
            credentialData['unsupportedType'] = true
            break
    }

    credentialsMap[c.id] = credentialData
}

// Convert the credentials map to JSON for easy readability and transfer
def jsonOutput = groovy.json.JsonOutput.toJson(credentialsMap)

// Print the JSON output
println(jsonOutput)
