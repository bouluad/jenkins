import requests
import time
from requests.auth import HTTPBasicAuth

JENKINS_URL = "http://your-jenkins-url"
PLUGIN_NAME = "git" # Replace with your plugin's short name
USER = "your-username"
API_TOKEN = "your-api-token"

# Step 1: Initiate Plugin Installation
print(f"Installing plugin {PLUGIN_NAME}...")
install_data = f"<jenkins><install plugin='{PLUGIN_NAME}@latest' /></jenkins>"
headers = {"Content-Type": "text/xml"}

install_response = requests.post(
    f"{JENKINS_URL}/pluginManager/installNecessaryPlugins",
    data=install_data,
    headers=headers,
    auth=HTTPBasicAuth(USER, API_TOKEN)
)

if install_response.status_code == 200:
    print("Plugin installation initiated successfully.")
else:
    print(f"Failed to initiate plugin installation: {install_response.status_code}")
    exit(1)

# Step 2: Verify Plugin Installation Status
STATUS_CHECK_INTERVAL = 5  # Time in seconds between checks
MAX_ATTEMPTS = 20          # Maximum number of attempts to check installation status

print("Verifying plugin installation status...")

for attempt in range(MAX_ATTEMPTS):
    response = requests.get(
        f"{JENKINS_URL}/pluginManager/api/json?depth=1",
        auth=HTTPBasicAuth(USER, API_TOKEN)
    )

    if response.status_code == 200:
        plugins = response.json().get("plugins", [])
        plugin_status = next((plugin for plugin in plugins if plugin["shortName"] == PLUGIN_NAME), None)
        
        if plugin_status:
            if not plugin_status["hasUpdate"]:
                print(f"Plugin {PLUGIN_NAME} is installed.")
                break
            else:
                print(f"Plugin {PLUGIN_NAME} is installed but has an update.")
                break
        else:
            print(f"Plugin {PLUGIN_NAME} is not yet installed. Checking again in {STATUS_CHECK_INTERVAL} seconds...")
    else:
        print(f"Failed to check plugin status: {response.status_code}")

    time.sleep(STATUS_CHECK_INTERVAL)
else:
    print(f"Plugin {PLUGIN_NAME} installation could not be verified within the allowed time.")
    exit(1)
