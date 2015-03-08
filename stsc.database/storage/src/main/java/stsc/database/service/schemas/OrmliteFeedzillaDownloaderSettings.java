package stsc.database.service.schemas;

import java.util.Date;

import stsc.common.service.FeedzillaDownloaderSettings;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "datafeed_settings")
public class OrmliteFeedzillaDownloaderSettings implements FeedzillaDownloaderSettings {

	public final static String settingColumnName = "setting_name";

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(columnName = settingColumnName, canBeNull = false, unique = true)
	private String settingName;

	@DatabaseField(columnName = "days_back_download_from", canBeNull = false)
	private int daysBackDownloadFrom;

	@DatabaseField(columnName = "endless_cycle", canBeNull = false)
	private boolean endlessCycle;

	@DatabaseField(columnName = "articles_wait_time", canBeNull = false)
	private int articlesWaitTime;

	@DatabaseField(columnName = "feed_folder", canBeNull = false)
	private String feedFolder;

	@DatabaseField(columnName = "interval_between_executions", canBeNull = false)
	private int intervalBetweenExecutions;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private OrmliteFeedzillaDownloaderSettings() {
		// for ormlite
	}

	public OrmliteFeedzillaDownloaderSettings(final String settingName) {
		this.settingName = settingName;
		this.daysBackDownloadFrom = 2;
		this.endlessCycle = true;
		this.articlesWaitTime = 20;
		this.feedFolder = "./feed_data";
		this.intervalBetweenExecutions = 36000;
	}

	public Integer getId() {
		return id;
	}

	@Override
	public int intervalBetweenExecutions() {
		return intervalBetweenExecutions;
	}

	public void setCreatedAt() {
		if (getId() == null) {
			this.createdAt = new Date();
		}
	}

	public void setUpdatedAt() {
		this.updatedAt = new Date();
	}

	@Override
	public int daysBackDownloadFrom() {
		return daysBackDownloadFrom;
	}

	@Override
	public boolean endlessCycle() {
		return endlessCycle;
	}

	@Override
	public int articlesWaitTime() {
		return articlesWaitTime;
	}

	@Override
	public String feedFolder() {
		return feedFolder;
	}

	//

	public void setFeedFolder(String feedFolder) {
		this.feedFolder = feedFolder;
	}

}
