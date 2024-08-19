Instructions:
Run the Export Script: In your existing Jenkins instance, run the first script to generate the dictionary of credentials.

Copy the Dictionary: Copy the output dictionary from the first script.

Paste the Dictionary: Replace the credentialsMap dictionary in the second script with the one you copied.

Run the Import Script: Execute the second script in the Jenkins Script Console of the new Jenkins instance to import the credentials.

Verify: Ensure that all credentials have been imported correctly by checking the Jenkins credentials store.

Security Considerations:
Sensitive Data: Since the dictionary contains sensitive data, handle it with care. Run these scripts in a secure environment with access controls.
Access Controls: Limit access to the Jenkins Script Console to authorized personnel.
Cleanup: Delete any temporary files or copies of the dictionary after the migration is complete.


----------------

Handling Plugin-Specific Credentials
Some credential types depend on specific Jenkins plugins:

AWS Credentials: Requires the AWS Credentials Plugin.
Docker Credentials: Requires the Docker Plugin.
Google OAuth Credentials: Requires the Google OAuth Plugin.
Ensure that these plugins are installed in your Jenkins instance. If not, the corresponding credential types will not be recognized, and you may need to install the necessary plugins or handle these credentials separately.

Running the Script
Access Jenkins Script Console:

Navigate to http://your-jenkins-instance/script.
Execute the Script:

Paste the above script into the console and execute it.
Copy the Output:

The script will output a JSON string representing all your credentials. Copy this output and save it securely.

---------------------
Instructions to Use the Import Script
Access Jenkins Script Console:

Navigate to http://your-new-jenkins-instance/script.
Paste the Script:

Copy and paste the above script into the console.
Insert JSON Data:

Replace the jsonString content with the JSON output you obtained from the export script.
Execute the Script:

Run the script. It will iterate through each credential in the JSON data and add it to the Jenkins credentials store.
Verify Import:

Go to Credentials in the Jenkins UI and verify that all credentials have been imported correctly.

