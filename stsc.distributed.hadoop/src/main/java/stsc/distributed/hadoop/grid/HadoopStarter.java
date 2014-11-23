package stsc.distributed.hadoop.grid;

import java.util.List;

import stsc.general.strategy.TradingStrategy;

public interface HadoopStarter {
	public List<TradingStrategy> searchOnHadoop() throws Exception;
}
