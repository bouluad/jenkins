import requests
from requests.auth import HTTPBasicAuth

JENKINS_URL = "http://your-jenkins-url"
PLUGIN_NAME = "git"
USER = "your-username"
API_TOKEN = "your-api-token"

data = f"<jenkins><install plugin='{PLUGIN_NAME}@latest' /></jenkins>"
headers = {"Content-Type": "text/xml"}

response = requests.post(
    f"{JENKINS_URL}/pluginManager/installNecessaryPlugins",
    data=data,
    headers=headers,
    auth=HTTPBasicAuth(USER, API_TOKEN)
)

if response.status_code == 200:
    print("Plugin installation initiated successfully.")
else:
    print(f"Failed to initiate plugin installation: {response.status_code}")
