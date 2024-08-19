// Import necessary Jenkins classes
import jenkins.model.Jenkins
import hudson.util.Secret

// Import Credentials classes
import com.cloudbees.plugins.credentials.Credentials
import com.cloudbees.plugins.credentials.CredentialsScope
import com.cloudbees.plugins.credentials.domains.Domain
import com.cloudbees.plugins.credentials.SystemCredentialsProvider

// Import specific credential implementations
import com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl
import com.cloudbees.plugins.credentials.impl.StringCredentialsImpl
import com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl
import com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl.UploadedKeyStoreSource

// Import AWS Credentials if AWS Credentials Plugin is installed
import com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl

// Import Docker Credentials if Docker Plugin is installed
import com.nirima.jenkins.plugins.docker.utils.DockerServerCredentials

// Import Google OAuth Credentials if Google OAuth Plugin is installed
import com.google.jenkins.plugins.credentials.oauth.GoogleRobotPrivateKeyCredentials
import com.google.jenkins.plugins.credentials.oauth.PrivateKeyConfiguration
import com.google.jenkins.plugins.credentials.oauth.JsonServiceAccountConfig

// JSON Slurper to parse JSON string
import groovy.json.JsonSlurper

// Paste your JSON string here
def jsonString = '''{
    "credential-1": {
        "type": "com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl",
        "username": "user1",
        "password": "password1"
    },
    "credential-2": {
        "type": "com.cloudbees.plugins.credentials.impl.StringCredentialsImpl",
        "secret": "some-secret-text"
    },
    "credential-3": {
        "type": "com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey",
        "username": "sshUser",
        "privateKey": "private-key-content",
        "passphrase": "passphrase123"
    }
    // ... other credentials
}'''

// Parse the JSON string
def credentialsMap = new JsonSlurper().parseText(jsonString)

// Get the Jenkins credential store
def systemCredentialsProvider = SystemCredentialsProvider.getInstance()
def store = systemCredentialsProvider.getStore()
def domain = Domain.global()

credentialsMap.each { id, data ->
    def credentials = null

    switch(data.type) {
        case 'com.cloudbees.plugins.credentials.impl.UsernamePasswordCredentialsImpl':
            credentials = new UsernamePasswordCredentialsImpl(
                CredentialsScope.GLOBAL,
                id,
                null,
                data.username,
                data.password
            )
            break

        case 'com.cloudbees.plugins.credentials.impl.StringCredentialsImpl':
            credentials = new StringCredentialsImpl(
                CredentialsScope.GLOBAL,
                id,
                null,
                Secret.fromString(data.secret)
            )
            break

        case 'com.cloudbees.jenkins.plugins.sshcredentials.impl.BasicSSHUserPrivateKey':
            credentials = new BasicSSHUserPrivateKey(
                CredentialsScope.GLOBAL,
                id,
                data.username,
                new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(data.privateKey),
                data.passphrase ? data.passphrase : null,
                null
            )
            break

        case 'com.cloudbees.plugins.credentials.impl.CertificateCredentialsImpl':
            def keyStoreSource = new UploadedKeyStoreSource(data.certificate.getBytes())
            credentials = new CertificateCredentialsImpl(
                CredentialsScope.GLOBAL,
                id,
                null,
                data.password ? Secret.fromString(data.password) : null,
                keyStoreSource
            )
            break

        case 'com.cloudbees.jenkins.plugins.awscredentials.AWSCredentialsImpl':
            credentials = new AWSCredentialsImpl(
                CredentialsScope.GLOBAL,
                id,
                data.accessKey,
                data.secretKey,
                null
            )
            break

        case 'com.nirima.jenkins.plugins.docker.utils.DockerServerCredentials':
            credentials = new DockerServerCredentials(
                CredentialsScope.GLOBAL,
                id,
                data.username,
                data.password ? Secret.fromString(data.password) : null,
                data.email,
                data.serverAddress
            )
            break

        case 'com.google.jenkins.plugins.credentials.oauth.GoogleRobotPrivateKeyCredentials':
            def serviceAccountConfig = new JsonServiceAccountConfig(data.privateKey)
            credentials = new GoogleRobotPrivateKeyCredentials(
                data.accountId,
                serviceAccountConfig,
                null
            )
            break

        default:
            println "Unsupported credential type: ${data.type} for ID: ${id}"
            break
    }

    if (credentials != null) {
        store.addCredentials(domain, credentials)
        println "Imported credential ID: ${id}"
    }
}

println "All credentials have been imported successfully."
