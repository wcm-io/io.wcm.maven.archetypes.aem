#!/bin/bash

if [[ $0 == *":\\"* ]]; then
  DISPLAY_PAUSE_MESSAGE=true
fi

mvn clean verify -Plocal

if [ "$DISPLAY_PAUSE_MESSAGE" = true ]; then
  echo ""
  read -n1 -r -p "Press any key to continue..."
fi
