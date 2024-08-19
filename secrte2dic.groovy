import jenkins.model.*
import hudson.util.Secret
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.*

// The dictionary of credentials to be imported
def credentialsMap = [
  'credential-1': [
    type: 'UsernamePasswordCredentialsImpl',
    username: 'user1',
    password: 'password1'
  ],
  'credential-2': [
    type: 'StringCredentialsImpl',
    secret: 'secretTextValue'
  ],
  'credential-3': [
    type: 'BasicSSHUserPrivateKey',
    username: 'sshUser',
    privateKey: 'privateKeyData',
    passphrase: 'optionalPassphrase'
  ],
  // ... more credentials
]

// Get the credentials store
def domain = Domain.global()
def store = Jenkins.instance.getExtensionList('com.cloudbees.plugins.credentials.SystemCredentialsProvider')[0]?.getStore()

// Iterate through the dictionary and create credentials in Jenkins
credentialsMap.each { id, data ->
    if (data.type == 'UsernamePasswordCredentialsImpl') {
        def credentials = new UsernamePasswordCredentialsImpl(CredentialsScope.GLOBAL, id, null, data.username, data.password)
        store.addCredentials(domain, credentials)
    } else if (data.type == 'StringCredentialsImpl') {
        def credentials = new StringCredentialsImpl(CredentialsScope.GLOBAL, id, null, Secret.fromString(data.secret))
        store.addCredentials(domain, credentials)
    } else if (data.type == 'BasicSSHUserPrivateKey') {
        def credentials = new BasicSSHUserPrivateKey(
            CredentialsScope.GLOBAL,
            id,
            data.username,
            new BasicSSHUserPrivateKey.DirectEntryPrivateKeySource(data.privateKey),
            data.passphrase ? Secret.fromString(data.passphrase) : null,
            null
        )
        store.addCredentials(domain, credentials)
    } else if (data.type == 'AWSCredentialsImpl') {
        def credentials = new AWSCredentialsImpl(CredentialsScope.GLOBAL, id, data.accessKey, data.secretKey, null)
        store.addCredentials(domain, credentials)
    } else if (data.type == 'DockerServerCredentials') {
        def credentials = new DockerServerCredentials(
            CredentialsScope.GLOBAL,
            id,
            data.username,
            Secret.fromString(data.password),
            data.email,
            data.serverAddress
        )
        store.addCredentials(domain, credentials)
    } else {
        println("Unsupported credential type for ID: ${id}")
    }
}

println("Credentials have been successfully imported into Jenkins.")
