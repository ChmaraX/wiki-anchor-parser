#!/bin/bash

# Remove files (if exists) indexing meta files from data directory
[ -e data/dead_letter_queue ] && rm -rf data/dead_letter_queue
[ -e data/plugins ] && rm -rf data/plugins
[ -e data/queue ] && rm -rf data/queue
[ -e data/.lock ] && rm data/.lock
[ -e data/uuid ] && rm data/uuid

cd elk-docker

# Stop containers and remove volume (if exists)
docker-compose down -v

# Check if files to index exists
cd ../data/
[ -f "link_freq.csv" ] || { echo 'Link frequencies data does not exist'; exit 1;}
[ -f "text_freq.csv" ] || { echo 'Text frequencies data does not exist'; exit 1;}

# Start containers and index files on start
cd ../elk-docker/
docker-compose up -d
