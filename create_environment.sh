#!/bin/bash

date

if [ -d "./source" ]; then
    exit 0
fi

echo "Creating Sources..."
mkdir source
cd source

function cloneAndCreateProject() {
    echo Cloning and Creating $1 Project
    git clone https://github.com/sidorovis/$1.git
    cd $1
    mvn compile
    mvn eclipse:eclipse -DdownloadSources=true -DdownloadJavadocs=true
    cd ../
}

cloneAndCreateProject stsc.common
cloneAndCreateProject stsc.algorithms
cloneAndCreateProject stsc.storage
cloneAndCreateProject stsc.signals
cloneAndCreateProject stsc.stocks.yahoo
cloneAndCreateProject stsc.stocks.indexes
cloneAndCreateProject stsc.database
cloneAndCreateProject stsc.general
cloneAndCreateProject stsc.performance
cloneAndCreateProject stsc.integration.tests
cloneAndCreateProject stsc.fundamental.analysis
cloneAndCreateProject stsc.news.feedzilla
cloneAndCreateProject stsc.distributed.hadoop
cloneAndCreateProject stsc.frontend.zozka
cloneAndCreateProject stsc.as.service.feedzilla.downloader
cloneAndCreateProject stsc.as.service.yahoo.downloader

date

