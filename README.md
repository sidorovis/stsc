Simulated Trading Strategies Creator
====================================

stst.algorithms
---------------

This module would store all eod and stock algorithms.

stsc.common
-----------

Core (smallest) elements for strategy generating: Day, Stock, FromToPeriod, Prices, TimeTracker, UnitedFormatStock and etc.

stsc.signals
------------

Signals base (signal is a result when algorithm process time element (day, minute...)).

stsc.general
------------

Core functionality for strategy simulating.


stsc.integration.tests
----------------------

Module for helping other modules to test themself's.

stsc.performance
----------------

Generate performance testing results (for example how much time will it take to simulate X strategies from Y stocks with N days in period).
Performance store runnable classes that calculate different time/quality for different processes:
 * performance checker for time for grid search;
 * performance checker for searching ideal genetic algorithm parameters (population size, iteration size, percent amount for mutation and for crossover);
 * performance checker that calculate how much time we need for generating N StrategySettings.

stsc.signals
------------

Store types of signals (basic: Boolean, Double, Integer, Side) and series (basic:Common, Limits).
 * CommonSignalsSerie: store signals for all history at HashMap for getting signal by Date and ArrayList for getting signal by index.
 * LimitSignalsSerie: store signals like CommonSignalsSerie do but have limit and restrict amount of elements at serie.


stsc.storage
------------

Have all storages for Algorithms, Executions, Stocks.

stsc.yahoo
----------
Yahoo related core classes - Yahoo StockStorage realisation, Yahoo stock storage multithread EOD stock data reader.

stsc.yahoo.downloader
---------------------

Download EOD Yahoo market data.

stsc.yahoo.liquidator
---------------------

Filter liquid EOD market data from Yahoo.

