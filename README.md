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

To use subprojects please add next to pom file:
```
	<repositories>
		<repository>
			<id>stsc-bom-repository</id>
			<url>https://raw.github.com/sidorovis/stsc/stsc.bom/</url>
		</repository>
	    <repository>
    	  	<id>stsc-common-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.common/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-signals-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.signals/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-algorithms-common-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.algorithms.common/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-algorithms-common-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.algorithms.fundamental.analysis/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-algorithms-equity-test-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.algorithms.equity.test/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-storage-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.storage/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-general-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.general/</url>
   		</repository>
	    <repository>
    	  	<id>feedzilla-java-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/feedzilla-java/feedzilla-java/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-news-feedzilla-storage-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.news.feedzilla.storage/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-news-feedzilla-downloader-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.news.feedzilla.downloader/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-stocks-yahoo-downloader-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.stocks.yahoo.downloader/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-stocks-yahoo-core-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.stocks.yahoo.core/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-stocks-yahoo-liquidator-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.stocks.yahoo.liquidator/</url>
   		</repository>
	    <repository>
    	  	<id>stsc-database-storage-mvn-repo</id>
     		<url>https://raw.github.com/sidorovis/stsc/stsc.database.storage/</url>
   		</repository>
	</repositories>
```