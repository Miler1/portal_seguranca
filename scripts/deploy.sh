#!/bin/bash

source "play-shell-utils/play-shell-utils.sh"

### Configurações
config_project_name="portal-seguranca-amapa"
config_play_version="1.4.5"
config_backend_app_conf_include_path="ambientes/<env>.conf"

### Deploy

dp_execute_deploy_procedure "$1"
