package stsc.database.service.settings;

import java.util.Date;

import stsc.common.service.YahooDownloaderSettings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "yahoo_downloader_datafeed_settings")
public class OrmliteYahooDownloaderSettings implements YahooDownloaderSettings {

	public final static String settingColumnName = "setting_name";

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(columnName = settingColumnName, canBeNull = false, unique = true)
	private String settingName;

	@DatabaseField(columnName = "thread_amount", canBeNull = false)
	private int threadAmount;

	@DatabaseField(columnName = "download_only_existed", canBeNull = false)
	private boolean downloadOnlyExisted;

	@DatabaseField(columnName = "download_by_pattern", canBeNull = false)
	private boolean downloadByPattern;

	@DatabaseField(columnName = "stock_name_from", canBeNull = false)
	private int stockNameFrom;

	@DatabaseField(columnName = "stock_name_to", canBeNull = false)
	private int stockNameTo;

	@DatabaseField(columnName = "pattern_name_from", canBeNull = false)
	private String patternNameFrom;

	@DatabaseField(columnName = "pattern_name_to", canBeNull = false)
	private String patternNameTo;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private OrmliteYahooDownloaderSettings() {
		// for ormlite
	}

	public OrmliteYahooDownloaderSettings(final String settingName) {
		this.settingName = settingName;
		this.threadAmount = 1;
		this.downloadOnlyExisted = true;
		this.downloadByPattern = true;
		this.stockNameFrom = 1;
		this.stockNameTo = 2;
		this.patternNameFrom = "A";
		this.patternNameTo = "Z";
	}

	public Integer getId() {
		return id;
	}

	public void setThreadAmount(int threadAmount) {
		this.threadAmount = threadAmount;
	}

	@Override
	public int threadAmount() {
		return threadAmount;
	}

	public void setDownloadOnlyExisted(boolean downloadOnlyExisted) {
		this.downloadOnlyExisted = downloadOnlyExisted;
	}

	@Override
	public boolean downloadOnlyExisted() {
		return downloadOnlyExisted;
	}

	public void setDownloadByPattern(boolean downloadByPattern) {
		this.downloadByPattern = downloadByPattern;
	}

	@Override
	public boolean downloadByPattern() {
		return downloadByPattern;
	}

	public void setStockNameFrom(int stockNameFrom) {
		this.stockNameFrom = stockNameFrom;
	}

	@Override
	public int stockNameFrom() {
		return stockNameFrom;
	}

	public void setStockNameTo(int stockNameTo) {
		this.stockNameTo = stockNameTo;
	}

	@Override
	public int stockNameTo() {
		return stockNameTo;
	}

	public void setPatternNameFrom(String patternNameFrom) {
		this.patternNameFrom = patternNameFrom;
	}

	@Override
	public String patternNameFrom() {
		return patternNameFrom;
	}

	public void setPatternNameTo(String patternNameTo) {
		this.patternNameTo = patternNameTo;
	}

	@Override
	public String patternNameTo() {
		return patternNameTo;
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
