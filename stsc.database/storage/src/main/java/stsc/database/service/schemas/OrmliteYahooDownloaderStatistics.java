package stsc.database.service.schemas;

import java.util.Date;

import stsc.common.service.statistics.StatisticType;
import stsc.common.service.statistics.YahooDownloaderStatistics;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "datafeed_statistics")
public class OrmliteYahooDownloaderStatistics implements YahooDownloaderStatistics {

	public final static String settingColumnName = "setting_name";
	public final static String processIdColumnName = "process_id";

	@DatabaseField(generatedId = true, columnName = "id", canBeNull = false)
	private Integer id;

	@DatabaseField(columnName = settingColumnName, canBeNull = false, unique = true)
	private String settingName;

	@DatabaseField(columnName = "start_date", dataType = DataType.DATE, canBeNull = false)
	private Date startDate;

	@DatabaseField(columnName = processIdColumnName, canBeNull = false)
	private int processId;

	@DatabaseField(columnName = "statistic_type", canBeNull = false, dataType = DataType.ENUM_STRING)
	private StatisticType statisticType;

	@DatabaseField(columnName = "message", canBeNull = false)
	private String message;

	@DatabaseField(columnName = "created_at", dataType = DataType.DATE)
	private Date createdAt;

	@DatabaseField(columnName = "updated_at", dataType = DataType.DATE)
	private Date updatedAt;

	@SuppressWarnings("unused")
	private OrmliteYahooDownloaderStatistics() {
		// for ormlite
	}

	public OrmliteYahooDownloaderStatistics(final String settingName) {
		this.settingName = settingName;

	}

	public Integer getId() {
		return id;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Override
	public Date startDate() {
		return startDate;
	}

	public void setProcessId(int processId) {
		this.processId = processId;
	}

	@Override
	public int processId() {
		return processId;
	}

	public void setStatisticType(StatisticType statisticType) {
		this.statisticType = statisticType;
	}

	@Override
	public StatisticType statisticType() {
		return statisticType;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public String message() {
		return message;
	}

	public String getSettingName() {
		return settingName;
	}

	public Date getStartDate() {
		return startDate;
	}

	public int getProcessId() {
		return processId;
	}

	public StatisticType getStatisticType() {
		return statisticType;
	}

	public String getMessage() {
		return message;
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
	public String toString() {
		return "[" + getStartDate() + "] " + getStatisticType() + " " + getMessage();
	}

}
