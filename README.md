![Travis](https://travis-ci.org/sidorovis/stsc.svg?branch=master)

# Simulated Trading Strategies Creator

## stst.algorithms

This module would store all eod and stock algorithms.

You could find algorithms list at the wiki: [algorithms list](https://github.com/sidorovis/stsc/wiki/Algorithms-Tree)

## stsc.common

Core (smallest) elements for strategy generating: Day, Stock, FromToPeriod, Prices, TimeTracker, UnitedFormatStock and etc.

## stsc.distributed.hadoop

Distributed hadoop - is a realisation for hadoop distributed calculation.

## stsc.frontend.zozka

Zozka is a simple frontend that have end-user interface with datafeed's on-stock signals, on-eod signals, 
equity curve's and statistics for trading strategies in different ways:

#### For settings:
1. settings tables (parameters (from, to, step or domen for string values));
2. big multi-parameter sets (multiplication of parameters);

#### For datafeed:
1. candles, colored lines for prices;
2. colored bars for values;

#### For on-stock signals / values:
1. colored marks (dots/crosses etc.);
2. colored lines (straight lines between dots);
3. colored lines (Bezier like);

#### For on-eod signals:
1. colored marks;
2. labels with amounts/wasted amounts/values;

#### For equity curve's:
1. colored lines;
2. colored lines (Bezier like);
3. statistics tables.

#### For datafeed:
1. list of stocks;
2. comparing dialog with charts and adjective closes;
3. possibility to simple redownload.

Also this project have module to fix problems on Datafeed (when datafeed have stocks with big gap ot provide possibility to redownload data). 

Works only for Yahoo EOD datafeed currently.

## stsc.general

Core functionality for strategy simulating.

## stsc.integration.tests

Module for helping other modules to test themself's.

Especially for algorithms to work with all rest modules (datafeed and etc).

## stsc.news.feedzilla.downloader

This module download feedzilla news feed from feedzilla server. Using hashes (to not store already stored data) and stsc.news.feedzilla.storage as a storage.

It use feedzilla-java project (see https://github.com/sidorovis/feedzilla-java.git for more details).

## stsc.news.feedzilla.sqlite.migrations

This module store mechanism to migrate database (currently we use sqlite) with liquibase migrate tool to "work" state.

## stsc.news.feedzilla.storage

This module store and provide access to feedzilla data. Feed divided onto three tables (Categories, Subcategories, Articles).

Access guaranteed using Ormlite.

## stsc.performance

Generate performance testing results (for example how much time will it take to simulate X strategies from Y stocks with N days in period).

Performance store runnable classes that calculate different time/quality for different processes:
 * performance checker for time for grid search;
 * performance checker for searching ideal genetic algorithm parameters (population size, iteration size, percent amount for mutation and for crossover);
 * performance checker that calculate how much time we need for generating N StrategySettings.

## stsc.signals

Store types of signals (basic: Boolean, Double, Integer, Side) and series (basic:Common, Limits).
 * CommonSignalsSerie: store signals for all history at HashMap for getting signal by Date and ArrayList for getting signal by index.
 * LimitSignalsSerie: store signals like CommonSignalsSerie do but have limit and restrict amount of elements at serie.

## stsc.stocks.yahoo.core

Yahoo related core classes - Yahoo StockStorage realisation, Yahoo stock storage multithread EOD stock data reader.

## stsc.stocks.yahoo.downloader

Download EOD Yahoo market data.

## stsc.stocks.yahoo.liquidator

Filter liquid EOD market data from Yahoo.

## stsc.storage

Have all storages for Algorithms, Executions, Stocks.

