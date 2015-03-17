package stsc.news.feedzilla.separator;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import stsc.news.feedzilla.FeedzillaFileSaver;
import stsc.news.feedzilla.FeedzillaFileStorage;
import stsc.news.feedzilla.FeedzillaHashStorage;
import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;

public class FeedzillaByDateSeparator {

	private String feedFolder = "./feed_data";
	private String byDateFeedFolder = "./feed_data_by_date";
	private boolean byMonth = true;

	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy_MM_dd");

	private FeedzillaByDateSeparator() throws FileNotFoundException, IOException {
		readProperties();
		final FeedzillaHashStorage hashStorage = new FeedzillaHashStorage(feedFolder);
		final FeedzillaFileStorage storage = hashStorage.readFeedDataAndStore(LocalDateTime.of(1990, 1, 1, 0, 0));
		storeData(storage, hashStorage);
	}

	private void readProperties() throws FileNotFoundException, IOException {
		try (DataInputStream inputStream = new DataInputStream(new FileInputStream("./config/feedzilla_developer.properties"))) {
			final Properties properties = new Properties();
			properties.load(inputStream);
			feedFolder = properties.getProperty("cleaned.feed.folder");
			byDateFeedFolder = properties.getProperty("by.date.feed.folder");
			byMonth = Boolean.valueOf(properties.getProperty("by.month", "true"));
		}
	}

	private void storeData(FeedzillaFileStorage storage, FeedzillaHashStorage hashStorage) throws FileNotFoundException, IOException {
		FeedzillaFileSaver.saveCategories(byDateFeedFolder, hashStorage.getHashCategories());
		FeedzillaFileSaver.saveSubcategories(byDateFeedFolder, hashStorage.getHashSubcategories());
		final Map<LocalDateTime, List<FeedzillaFileArticle>> articles = storage.getArticlesByDate();
		final List<FeedzillaFileArticle> articlesToSwap = new ArrayList<FeedzillaFileArticle>();
		LocalDate swappingDate = LocalDate.now().withDayOfMonth(1);
		for (Entry<LocalDateTime, List<FeedzillaFileArticle>> v : articles.entrySet()) {
			LocalDate keyDate = v.getKey().toLocalDate();
			if (byMonth) {
				keyDate = keyDate.withDayOfMonth(1);
			}
			if (!keyDate.equals(swappingDate)) {
				storeArticles(articlesToSwap, swappingDate);
				swappingDate = keyDate;
			}
			articlesToSwap.addAll(v.getValue());
		}
		storeArticles(articlesToSwap, swappingDate);
	}

	private void storeArticles(List<FeedzillaFileArticle> articlesToSwap, LocalDate swappingDate) throws FileNotFoundException, IOException {
		if (!articlesToSwap.isEmpty()) {
			FeedzillaFileSaver.saveArticles(byDateFeedFolder, articlesToSwap, swappingDate.format(formatter));
			articlesToSwap.clear();
		}
	}

	public static void main(String[] args) {
		try {
			new FeedzillaByDateSeparator();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
