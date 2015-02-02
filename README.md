![Travis](https://travis-ci.org/sidorovis/stsc.svg?branch=master)

# Simulated Trading Strategies Creator

A platform for simulate in history trading strategies using stock price information (EOD) news feed.
Platform provide possibility to generate a lot of strategies, select best of them.

Datafeed example that used into platform: yahoo eod.
 * also there is a GUI application to define and if possible correct (redownload) broken/not liquid stocks. [ZozkaDatafeedChecker](https://github.com/sidorovis/stsc/wiki/ZozkaDatafeedChecker-Examples).
Newsfeed example that used into platform: feedzilla.
Mechanisms to distribute calculation: hadoop.

"Fancy" GUI is based on javafx and JFreeChart. 
Actually you could draw 
 * charts with stock data (OHLC); 
 * on stock algorithms results (curves);
 * final equity curve;

Also you could observe 'news' feed (currently from feedzilla).
 * [ZozkaFeedzillaVisualizer](https://github.com/sidorovis/stsc/wiki/ZozkaFeedzillaVisualizer-Examples)

Also code include examples of application with different search algorithms and providing results in the very convinient way.

 * [ZozkaStrategySelector](https://github.com/sidorovis/stsc/wiki/ZozkaStrategySelector-Examples)
 * [ZozkaStrategyVisualizer](https://github.com/sidorovis/stsc/wiki/ZozkaStrategyVisualizer-Examples)

##### Please feel free to read some information about [modules list](https://github.com/sidorovis/stsc/wiki/Modules-List)

##### Some information about working Stsc available at: [Stsc As Service](https://github.com/sidorovis/stsc/wiki/StscAsService)
