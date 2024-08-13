#!/bin/bash

JENKINS_URL="http://your-jenkins-url"
USER="your-username"
API_TOKEN="your-api-token"
PLUGIN_FILE="plugins.json"  # Path to your JSON file
STATUS_CHECK_INTERVAL=5     # Time in seconds between checks
MAX_ATTEMPTS=20             # Maximum number of attempts to check installation status

# Install jq if it's not already installed
if ! command -v jq &> /dev/null
then
    echo "jq could not be found, please install jq."
    exit 1
fi

# Iterate over each plugin in the JSON file
for PLUGIN in $(jq -r 'keys[]' $PLUGIN_FILE)
do
    VERSION=$(jq -r --arg PLUGIN "$PLUGIN" '.[$PLUGIN]' $PLUGIN_FILE)
    echo "Installing plugin $PLUGIN version $VERSION..."
    
    # Step 1: Initiate Plugin Installation
    curl -X POST "$JENKINS_URL/pluginManager/installNecessaryPlugins" \
        --user $USER:$API_TOKEN \
        --data "<jenkins><install plugin='$PLUGIN@$VERSION' /></jenkins>" \
        -H "Content-Type: text/xml"

    # Step 2: Verify Plugin Installation Status
    echo "Verifying plugin $PLUGIN installation status..."
    for ((i=1; i<=MAX_ATTEMPTS; i++))
    do
        INSTALLED=$(curl -s "$JENKINS_URL/pluginManager/api/json?depth=1" \
                    --user $USER:$API_TOKEN | \
                    jq -r ".plugins[] | select(.shortName==\"$PLUGIN\") | .version")

        if [ "$INSTALLED" == "$VERSION" ]; then
            echo "Plugin $PLUGIN version $VERSION is installed."
            break
        else
            echo "Plugin $PLUGIN is not yet installed. Checking again in $STATUS_CHECK_INTERVAL seconds..."
        fi

        sleep $STATUS_CHECK_INTERVAL
    done

    if [ "$INSTALLED" != "$VERSION" ]; then
        echo "Plugin $PLUGIN installation could not be verified within the allowed time."
        exit 1
    fi
done

echo "All plugins have been installed and verified."
