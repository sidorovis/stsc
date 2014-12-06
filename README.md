<img src="https://travis-ci.org/sidorovis/stsc.svg?branch=master" />

Simulated Trading Strategies Creator
====================================

stst.algorithms
---------------

This module would store all eod and stock algorithms.

Algorithms tree:
<ol>
  <li>
<h5>stsc.algorithms</h5> (common)<br/>
  </li>
  <li>
<h5>stsc.algorithms.eod.primitive</h5>
  </li>
  <li>
<h5>stsc.algorithms.stock.factors.primitive</h5>
  </li>
</ol>

stsc.common
-----------

Core (smallest) elements for strategy generating: Day, Stock, FromToPeriod, Prices, TimeTracker, UnitedFormatStock and etc.

stsc.distributed.hadoop
-----------

Distributed hadoop - is a realisation for hadoop distributed calculation.


stsc.frontend.zozka
------------

Zozka is a simple frontend that have end-user interface with datafeed's on-stock signals, on-eod signals, equity curve's and statistics for trading strategies in different ways:
For settings:
<ol>
  <li>settings tables (parameters (from, to, step or domen for string values));</li>
  <li>big multi-parameter sets (multiplication of parameters);</li>
</ol>
For datafeed:
<ol>
  <li>candles, colored lines for prices;</li>
  <li>colored bars for values;</li>
</ol>
For on-stock signals / values:
<ol>
  <li>colored marks (dots/crosses etc.);</li>
  <li>colored lines (straight lines between dots);</li>
  <li>colored lines (Bezier like);</li>
</ol>
For on-eod signals:
<ol>
  <li>colored marks;</li>
  <li>labels with amounts/wasted amounts/values;</li>
</ol>
For equity curve's:
<ol>
 * colored lines;
 * colored lines (Bezier like);
 * statistics tables.
</ol>
For datafeed:
<ol>
 * list of stocks;
 * comparing dialog with charts and adjective closes;
 * possibility to simple redownload.
</ol>

Also this project have module to fix problems on Datafeed (when datafeed have stocks with big gap ot provide possibility to redownload data). Works only for Yahoo EOD datafeed currently.

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

