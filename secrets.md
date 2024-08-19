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
