import requests
import time
import json
from requests.auth import HTTPBasicAuth

JENKINS_URL = "http://your-jenkins-url"
USER = "your-username"
API_TOKEN = "your-api-token"
PLUGIN_FILE = "plugins.json"  # Path to your JSON file
STATUS_CHECK_INTERVAL = 5     # Time in seconds between checks
MAX_ATTEMPTS = 20             # Maximum number of attempts to check installation status

# Load plugins from JSON file
with open(PLUGIN_FILE, "r") as file:
    plugins = json.load(file)

# Iterate over each plugin in the JSON file
for plugin_name, version in plugins.items():
    print(f"Installing plugin {plugin_name} version {version}...")
    
    # Step 1: Initiate Plugin Installation
    install_data = f"<jenkins><install plugin='{plugin_name}@{version}' /></jenkins>"
    headers = {"Content-Type": "text/xml"}

    install_response = requests.post(
        f"{JENKINS_URL}/pluginManager/installNecessaryPlugins",
        data=install_data,
        headers=headers,
        auth=HTTPBasicAuth(USER, API_TOKEN)
    )

    if install_response.status_code == 200:
        print(f"Plugin {plugin_name} installation initiated successfully.")
    else:
        print(f"Failed to initiate plugin {plugin_name} installation: {install_response.status_code}")
        continue

    # Step 2: Verify Plugin Installation Status
    print(f"Verifying plugin {plugin_name} installation status...")

    for attempt in range(MAX_ATTEMPTS):
        response = requests.get(
            f"{JENKINS_URL}/pluginManager/api/json?depth=1",
            auth=HTTPBasicAuth(USER, API_TOKEN)
        )

        if response.status_code == 200:
            installed_plugins = response.json().get("plugins", [])
            plugin_info = next((plugin for plugin in installed_plugins if plugin["shortName"] == plugin_name), None)
            
            if plugin_info and plugin_info["version"] == version:
                print(f"Plugin {plugin_name} version {version} is installed.")
                break
            else:
                print(f"Plugin {plugin_name} is not yet installed. Checking again in {STATUS_CHECK_INTERVAL} seconds...")
        else:
            print(f"Failed to check plugin {plugin_name} status: {response.status_code}")

        time.sleep(STATUS_CHECK_INTERVAL)
    else:
        print(f"Plugin {plugin_name} installation could not be verified within the allowed time.")
