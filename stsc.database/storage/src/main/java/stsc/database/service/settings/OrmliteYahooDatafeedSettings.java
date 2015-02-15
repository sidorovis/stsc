package stsc.database.service.settings;

import java.util.Date;

import org.apache.commons.lang3.tuple.Pair;

import stsc.common.service.settings.YahooDatafeedSettings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "yahoo_downloader_datafeed_settings")
public class OrmliteYahooDatafeedSettings implements YahooDatafeedSettings {

	public final static String settingColumnName = "setting_name";

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(columnName = settingColumnName, canBeNull = false, unique = true)
	private String settingName;

	@DatabaseField(columnName = "thread_amount", canBeNull = false)
	private int threadAmount;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private OrmliteYahooDatafeedSettings() {
		// for ormlite
	}

	public OrmliteYahooDatafeedSettings(final String settingName) {
		this.settingName = settingName;
		this.threadAmount = 0;
	}

	public Integer getId() {
		return id;
	}

	public OrmliteYahooDatafeedSettings setThreadAmount(int threadAmount) {
		this.threadAmount = threadAmount;
		return this;
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

	public void setCreatedAt() {
		if (getId() == null) {
			this.createdAt = new Date();
		}
	}

	public void setUpdatedAt() {
		this.updatedAt = new Date();
	}
}
