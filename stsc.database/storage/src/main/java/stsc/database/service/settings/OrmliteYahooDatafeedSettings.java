package stsc.database.service.settings;

import org.apache.commons.lang3.tuple.Pair;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import stsc.common.service.settings.YahooDatafeedSettings;

@DatabaseTable(tableName = "yahoo_downloader_datafeed_settings")
public class OrmliteYahooDatafeedSettings implements YahooDatafeedSettings {

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(columnName = "setting_name", canBeNull = false, unique = true)
	private String settingName;

	@DatabaseField(columnName = "thread_amount", canBeNull = false)
	private Integer threadAmount;

	@SuppressWarnings("unused")
	private OrmliteYahooDatafeedSettings() {
		// for ormlite
	}

	public OrmliteYahooDatafeedSettings(final String settingName) {
		this.settingName = settingName;
		this.threadAmount = 0;
	}

	@Override
	public int threadAmount() {
		return threadAmount;
	}

	@Override
	public boolean downloadOnlyExisted() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean downloadByPattern() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Pair<Integer, Integer> stockNameSizePair() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pair<String, String> patternNamePair() {
		// TODO Auto-generated method stub
		return null;
	}

}
