# Start from the universal image
FROM mcr.microsoft.com/devcontainers/universal:2

# Install openbox
RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
    && apt-get -y install --no-install-recommends openbox

# Set openbox to start automatically
RUN echo 'openbox-session &' >> ~/.xinitrc

RUN mkdir -p /root/.fluxbox && \
    echo "#!/bin/bash" > /root/.fluxbox/startup && \
    echo "xrandr --output VNC-0 --mode 1920x1080" >> /root/.fluxbox/startup && \
    echo "exec fluxbox" >> /root/.fluxbox/startup && \
    chmod +x /root/.fluxbox/startup
