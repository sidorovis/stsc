package stsc.MarketDataDownloader;

import java.io.IOException;

import junit.framework.TestCase;

public class MarketDataContextTest extends TestCase {
    public void testConstructionAndSave() throws IOException
    {
    	MarketDataContext marketDataContext = new MarketDataContext();
    	marketDataContext.dataFolder = "test";
    	assertEquals( marketDataContext.getTask(), null );
    	marketDataContext.addTask( "a" );
    	assertEquals( marketDataContext.getTask(), "a" );
    	assertEquals( marketDataContext.getTask(), null );	
    }

}
