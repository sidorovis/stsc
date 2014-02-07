MarketDataDownloader
====================

MarketDataDownloader is a bunch of classes that will download datafeed from Yahoo store it on HDD and share it to Memory Mapped to File.
MarketData will be stored to 'data' folder in binary format (see Stock class).

test_data folder - is a folder where test helper files stored
test folder - is a folder for class testing

*TODO*: 
 1) add configuration file;
 2) add inter process mutex;
 3) add inter process memory.

Content:
 * main/java/stsc/MarketDataDownloader/Prices.java - class store Open, High, Low, Close prices in double also read data from Yahoo CSV API line tokens.
 * main/java/stsc/MarketDataDownloader/Day.java - class store Market Data day data (Prices, Volume, Date, Adj Close).
 * main/java/stsc/MarketDataDownloader/Stock.java - class store Market Data for one stock (end of day data). Dates should be sorted from the oldiest one.
 * main/java/stsc/MarketDataDownloader/MarketDataContext.java - class store data folder (where application will store database), taskQueue is a queue wil names of stocks to process (multi-thread safe).
 * main/java/stsc/MarketDataDownloader/DownloadThread.java - class that store multi-thread download code algorithm (including partially download for allready stored stocks).
 * main/java/stsc/MarketDataDownloader/App.java - download starter.

