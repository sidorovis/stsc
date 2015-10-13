@echo off

IF EXIST ./source GOTO EXIT
echo "Creating Sources..."
mkdir source
cd source
call :cloneAndCreateProject stsc.common
call :cloneAndCreateProject stsc.algorithms
call :cloneAndCreateProject stsc.storage
call :cloneAndCreateProject stsc.signals
call :cloneAndCreateProject stsc.stocks.yahoo
call :cloneAndCreateProject stsc.stocks.indexes
call :cloneAndCreateProject stsc.database
call :cloneAndCreateProject stsc.general
call :cloneAndCreateProject stsc.performance
call :cloneAndCreateProject stsc.integration.tests
call :cloneAndCreateProject stsc.fundamental.analysis
call :cloneAndCreateProject stsc.distributed.hadoop
call :cloneAndCreateProject stsc.frontend.zozka
call :cloneAndCreateProject stsc.as.service.yahoo.downloader

GOTO :EXIT

:cloneAndCreateProject
    echo Cloning and Creating %~1 Project
    call git clone https://github.com/sidorovis/%~1.git
    cd %~1
    call mvn compile
	cd ../
goto:eof

:EXIT
