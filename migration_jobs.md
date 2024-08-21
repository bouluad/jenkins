# Jenkins Job Migration from AKS to Private Kubernetes Cluster

This guide details how to migrate all Jenkins jobs, along with their build history, from a Jenkins instance running on Azure Kubernetes Service (AKS) to a Jenkins instance running in a private Kubernetes cluster.

## Step 1: Prepare the New Jenkins Instance in the Private Kubernetes Cluster

1. **Deploy Jenkins:**
   - Ensure that Jenkins is deployed and running in your private Kubernetes cluster. This can be done using a Helm chart or by manually configuring the deployment with YAML files.

2. **Stop Jenkins (Optional):**
   - Stop Jenkins temporarily on the private cluster to ensure a clean migration process:
     ```bash
     kubectl scale deployment <jenkins-deployment-name> --replicas=0 -n <private-jenkins-namespace>
     ```

## Step 2: Backup the Jobs and Their History on AKS

1. **Access the Jenkins Pod on AKS:**
   - Access the Jenkins pod running on AKS to prepare the backup:
     ```bash
     kubectl exec -it <jenkins-pod-name> -n <jenkins-namespace> -- bash
     ```

2. **Create a Tarball of All Jobs:**
   - Navigate to the jobs directory and create a tarball of the entire `jobs` directory, which includes all jobs and their build history:
     ```bash
     cd /var/jenkins_home
     tar -czf /tmp/jenkins_jobs_backup.tar.gz jobs
     ```

3. **Copy the Backup to Your Local Machine:**
   - Exit the pod and copy the tarball to your local machine:
     ```bash
     kubectl cp <jenkins-namespace>/<jenkins-pod-name>:/tmp/jenkins_jobs_backup.tar.gz ./jenkins_jobs_backup.tar.gz
     ```

## Step 3: Transfer the Jobs Backup to the Private Kubernetes Cluster

1. **Copy the Backup to the Jenkins Pod in the Private Cluster:**
   - Upload the tarball to the Jenkins pod running in your private Kubernetes cluster:
     ```bash
     kubectl cp ./jenkins_jobs_backup.tar.gz <private-jenkins-namespace>/<private-jenkins-pod-name>:/tmp/jenkins_jobs_backup.tar.gz
     ```

2. **Extract the Backup in the Jenkins Pod:**
   - Access the Jenkins pod on the private cluster and extract the tarball:
     ```bash
     kubectl exec -it <private-jenkins-pod-name> -n <private-jenkins-namespace> -- bash
     cd /var/jenkins_home
     tar -xzf /tmp/jenkins_jobs_backup.tar.gz
     exit
     ```

   - This will place all the job configurations and history in the correct location on the new Jenkins instance.

## Step 4: Set Correct Permissions and Start Jenkins

1. **Set Correct Permissions:**
   - Ensure that Jenkins has the correct ownership and permissions for the job files:
     ```bash
     kubectl exec -it <private-jenkins-pod-name> -n <private-jenkins-namespace> -- bash
     chown -R jenkins:jenkins /var/jenkins_home/jobs
     exit
     ```

2. **Start Jenkins:**
   - If you stopped Jenkins earlier, start it again:
     ```bash
     kubectl scale deployment <jenkins-deployment-name> --replicas=1 -n <private-jenkins-namespace>
     ```

## Step 5: Verify the Migration

1. **Check the Jenkins Dashboard:**
   - Access the Jenkins dashboard in the private Kubernetes cluster and verify that all jobs are present, along with their history.

2. **Run Some Jobs:**
   - Test a few jobs to ensure they run correctly and that their configurations and histories are intact.

## Notes

- **Persistent Volumes:** Ensure the private Kubernetes cluster has a sufficient and properly configured Persistent Volume (PV) or Persistent Volume Claim (PVC) to handle the migrated data.
- **Plugins and Configuration:** Ensure that the Jenkins instance in the private cluster has all necessary plugins installed and configurations set up to match those from the AKS instance.

By following this method, you can successfully migrate all Jenkins jobs along with their full history from an AKS-hosted Jenkins instance to a private Kubernetes cluster, focusing solely on the `jobs` directory.
