JENKINS_URL="http://your-jenkins-url"
PLUGIN_NAME="your-plugin-id"
USER="your-username"
API_TOKEN="your-api-token"

# POST request to install the plugin
curl -X POST "$JENKINS_URL/pluginManager/installNecessaryPlugins" \
    --user $USER:$API_TOKEN \
    --data "<jenkins><install plugin='$PLUGIN_NAME@latest' /></jenkins>" \
    -H "Content-Type: text/xml"
