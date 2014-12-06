<img src="https://travis-ci.org/sidorovis/stsc.svg?branch=master" />

Simulated Trading Strategies Creator
====================================

stst.algorithms
---------------

This module would store all eod and stock algorithms.

On Stock Algorithms tree:
<ol>
  <li>
    <h5>stsc.algorithms(common)</h5>
<p>
Input<br/>
&ensp;&ensp;{ {no_input}, <br/>
&ensp;&ensp;e -> [String: open by default, high, low, close, value]} <br/>
&ensp;&ensp;-> DoubleSignal.
</p>
<p>
ListOfDoubleAdapter<br/>
&ensp;&ensp;{ {1: ListOfDoubleSignal}, <br/>
&ensp;&ensp;e -> [Integer: I - index of double into list 0 by default]} <br/>
&ensp;&ensp;-> DoubleSignal.
</p>
<p>
Output <br/>
&ensp;&ensp;{ {1: DoubleSignal or SideSignal or IntegerSignal}, <br/>
&ensp;&ensp;e -> [-SubAlgoName-]} <br/>
&ensp;&ensp;-> DoubleSignal.<br/>
</p>
  </li>
  <li>
    <h5>stsc.algorithms.eod.primitive</h5>
TODO End of day primitive (test-like) algortihms (with positions and auto monitoring).
  </li>
  <li>
    <h5>stsc.algorithms.stock.factors.primitive</h5>
  </li>
  <li>
    <h5>stsc.algorithms.stock.indices.adx</h5>
<p>
AdxAdx <br/>
&ensp;&ensp;{ {no_input}, <br/>
&ensp;&ensp;e -> [Integer: N (14 by default), nSma (14 by default), size (2 by default) ]} <br/>
&ensp;&ensp;-> DoubleSignal.
</p>
<p>
AdxDi <br/>
&ensp;&ensp;{ {no_input}, <br/>
&ensp;&ensp;e -> [Integer: size (2 by default) ]} <br/>
&ensp;&ensp;-> ListOfDoubleSignal (dmMinus/trueRange, dmPlus/trueRange).
</p>
<p>
AdxDm <br/>
&ensp;&ensp;{ {no_input}, <br/>
&ensp;&ensp;e -> [Integer: size (2 by default) ]} <br/>
&ensp;&ensp;-> ListOfDoubleSignal (dmMinus, dmPlus).
</p>
<p>
AdxDxi <br/>
&ensp;&ensp;{ {no_input}, <br/>
&ensp;&ensp;e -> [Integer: N (14 by default), size (2 by default) ]} <br/>
&ensp;&ensp;-> DoubleSignal.
</p>
<p>
AdxAdxr <br/>
&ensp;&ensp;{ {no_input}, <br/>
&ensp;&ensp;e -> [Integer: N (14 by default) ]} <br/>
&ensp;&ensp;-> DoubleSignal.
</p>
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
<b>For settings:</b>
<ol>
  <li>settings tables (parameters (from, to, step or domen for string values));</li>
  <li>big multi-parameter sets (multiplication of parameters);</li>
</ol>
<b>For datafeed:</b>
<ol>
  <li>candles, colored lines for prices;</li>
  <li>colored bars for values;</li>
</ol>
<b>For on-stock signals / values:</b>
<ol>
  <li>colored marks (dots/crosses etc.);</li>
  <li>colored lines (straight lines between dots);</li>
  <li>colored lines (Bezier like);</li>
</ol>
<b>For on-eod signals:</b>
<ol>
  <li>colored marks;</li>
  <li>labels with amounts/wasted amounts/values;</li>
</ol>
<b>For equity curve's:</b>
<ol>
  <li>colored lines;</li>
  <li>colored lines (Bezier like);</li>
  <li>statistics tables.</li>
</ol>
<b>For datafeed:</b>
<ol>
  <li>list of stocks;</li>
  <li>comparing dialog with charts and adjective closes;</li>
  <li>possibility to simple redownload.</li>
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

