#!/bin/bash


git pull

echo "Successfully pulled the latest changes."

# Checkout the specified branch
git checkout master

echo "Successfully checked out master."

# update config
cd ./config/vpp-load_prediction-cfgs

# Pull the latest changes from the remote config repository
git pull

echo "Successfully pulled the latest changes."

# Checkout the specified branch
git checkout master

echo "Successfully checked out master and updated the config."
