Simulated Trading Strategies Creator
====================================

 - **stst.algorithms**: this module would store all eod and stock algorithms
 - **stsc.common**: core (smallest) elements for strategy generating: Day, Stock, FromToPeriod, Prices, TimeTracker, UnitedFormatStock and etc.;
 - **stsc.signals**: signals base (signal is a result when algorithm process time element (day, minute...));
 - **stsc.general**: core functionality for strategy simulating;
 - **stsc.integration.tests**: module for helping other modules to test themself's
 - **stsc.performance**: generate performance testing results (for example how much time will it take to simulate X strategies from Y stocks with N days in period);

    Performance store runnable classes that calculate different time/quality for different processes:
    1) performance checker for time for grid search;
    2) performance checker for searching ideal genetic algorithm parameters (population size, iteration size, percent amount for mutation and for crossover);
    3) performance checker that calculate how much time we need for generating N StrategySettings.

 - **stsc.signals**: store types of signals (basic: Boolean, Double, Integer, Side) and series (basic:Common, Limits);

    1) CommonSignalsSerie: store signals for all history at HashMap for getting signal by Date and ArrayList for getting signal by index.
    2) LimitSignalsSerie: store signals like CommonSignalsSerie do but have limit and restrict amount of elements at serie.

 - **stsc.storage**: have all storages for Algorithms, Executions, Stocks.
 - **stsc.yahoo**: Yahoo related core classes - Yahoo StockStorage realisation, Yahoo stock storage multithread EOD stock data reader;
 - **stsc.yahoo.downloader**: download EOD Yahoo market data;
 - **stsc.yahoo.liquidator**: filter liquid EOD market data from Yahoo.

