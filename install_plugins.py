import json
import requests
from requests.auth import HTTPBasicAuth

# Jenkins credentials
jenkins_url = "http://your-jenkins-url"
username = "your-username"
api_token = "your-api-token"

# Path to the JSON file with plugin information
json_file_path = "plugins.json"

# Load the JSON file
with open(json_file_path, "r") as file:
    plugins = json.load(file)

# Start a session
session = requests.Session()
session.auth = HTTPBasicAuth(username, api_token)

# Function to get a CSRF crumb from Jenkins
def get_crumb():
    crumb_url = f"{jenkins_url}/crumbIssuer/api/json"
    response = session.get(crumb_url)

    if response.status_code == 200:
        crumb_data = response.json()
        return {crumb_data['crumbRequestField']: crumb_data['crumb']}
    else:
        raise Exception(f"Failed to get crumb: Status Code: {response.status_code}, Response: {response.text}")

# Function to install a single plugin
def install_plugin(plugin_name, plugin_version, crumb):
    # The payload format expected by Jenkins
    payload = {
        "dynamicLoad": True,
        "plugins": [{
            "name": plugin_name,
            "version": plugin_version
        }]
    }

    headers = {
        "Content-Type": "application/json",
        **crumb  # Include the crumb in the headers
    }

    # Jenkins API endpoint for installing plugins
    install_url = f"{jenkins_url}/pluginManager/installNecessaryPlugins"

    # Send the POST request to install the plugin
    response = session.post(install_url, headers=headers, json=payload)

    if response.status_code == 200:
        print(f"Plugin {plugin_name} version {plugin_version} installed successfully.")
    else:
        print(f"Failed to install plugin {plugin_name} version {plugin_version}.")
        print(f"Status Code: {response.status_code}, Response: {response.text}")

# Main script execution
try:
    crumb = get_crumb()
    print("Successfully retrieved crumb.")
    
    # Install each plugin from the JSON file
    for plugin_name, plugin_version in plugins.items():
        install_plugin(plugin_name, plugin_version, crumb)
    
    print("All plugins installation requests have been sent.")

except Exception as e:
    print(f"An error occurred: {e}")
