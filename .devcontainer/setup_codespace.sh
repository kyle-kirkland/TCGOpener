#!/bin/bash

# Check if running in Codespace
if [ -n "$CODESPACES" ]; then
    echo "Running in Codespace. Starting fluxbox and setting up custom resolution."
    # Set the screen resolution to 1920x1080 using xrandr
    xrandr --output VNC-0 --mode 1920x1080
    # Set the DISPLAY environment variable to direct GUI apps to the correct display
    export DISPLAY=:1
    # Start Fluxbox in the background
    fluxbox &
else
    echo "Not in Codespace. Skipping Codespace-specific setup."
fi
