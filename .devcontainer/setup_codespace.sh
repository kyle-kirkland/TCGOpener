#!/bin/bash

# Check if running in Codespace
if [ -n "$CODESPACES" ]; then
    echo "Running in Codespace. Setting up custom resolution."
    xrandr --output VNC-0 --mode 1920x1080
    # Additional Codespace-specific configurations can go here
else
    echo "Not in Codespace. Skipping Codespace-specific setup."
fi
