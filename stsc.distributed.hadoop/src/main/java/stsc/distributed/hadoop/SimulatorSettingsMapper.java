package stsc.distributed.hadoop;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.mapreduce.Mapper;

import stsc.distributed.hadoop.types.SimulatorSettingsWritable;
import stsc.distributed.hadoop.types.StatisticsWritable;

public class SimulatorSettingsMapper extends Mapper<LongWritable, SimulatorSettingsWritable, LongWritable, StatisticsWritable> {
	
}
