JENKINS_URL="http://your-jenkins-url"
PLUGIN_NAME="git" # Replace with your plugin's short name
USER="your-username"
API_TOKEN="your-api-token"

# Step 1: Initiate Plugin Installation
echo "Installing plugin $PLUGIN_NAME..."
curl -X POST "$JENKINS_URL/pluginManager/installNecessaryPlugins" \
    --user $USER:$API_TOKEN \
    --data "<jenkins><install plugin='$PLUGIN_NAME@latest' /></jenkins>" \
    -H "Content-Type: text/xml"

# Step 2: Verify Plugin Installation Status
STATUS_CHECK_INTERVAL=5 # Time in seconds between checks
MAX_ATTEMPTS=20         # Maximum number of attempts to check installation status

echo "Verifying plugin installation status..."
for ((i=1; i<=MAX_ATTEMPTS; i++))
do
    INSTALLED=$(curl -s "$JENKINS_URL/pluginManager/api/json?depth=1" \
                --user $USER:$API_TOKEN | \
                jq -r ".plugins[] | select(.shortName==\"$PLUGIN_NAME\") | .hasUpdate")

    if [ "$INSTALLED" == "false" ]; then
        echo "Plugin $PLUGIN_NAME is installed."
        exit 0
    elif [ "$INSTALLED" == "true" ]; then
        echo "Plugin $PLUGIN_NAME is installed but has an update."
        exit 0
    else
        echo "Plugin $PLUGIN_NAME is not yet installed. Checking again in $STATUS_CHECK_INTERVAL seconds..."
    fi

    sleep $STATUS_CHECK_INTERVAL
done

echo "Plugin $PLUGIN_NAME installation could not be verified within the allowed time."
exit 1
