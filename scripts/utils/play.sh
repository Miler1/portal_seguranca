#!/bin/bash

PLAY_PATH_OPTIONS=(
	"/opt/play/play-1.4.4/play"
	"/opt/play-1.4.4/play"
	"/opt/playframework/play-1.4.4"
	"/opt/play-1.4.3/play-1.4.4")

for path in "${PLAY_PATH_OPTIONS[@]}"
do
	if [ -f "$path" ]; then
		PLAY_PATH="$path"
		return
	fi
done