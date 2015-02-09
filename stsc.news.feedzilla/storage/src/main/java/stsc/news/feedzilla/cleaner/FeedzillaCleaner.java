package stsc.news.feedzilla.cleaner;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Properties;

import stsc.news.feedzilla.FeedzillaFileSaver;
import stsc.news.feedzilla.FeedzillaFileStorage;
import stsc.news.feedzilla.FeedzillaHashStorage;

public class FeedzillaCleaner {

	private String feedFolder = "./feed_data";
	private String cleanedFeedFolder = "./cleaned_feed_data";

	private FeedzillaCleaner() throws FileNotFoundException, IOException {
		readProperties();
		final FeedzillaHashStorage hashStorage = new FeedzillaHashStorage(feedFolder);
		final FeedzillaFileStorage storage = hashStorage.readFeedDataAndStore(LocalDateTime.of(1990, 1, 1, 0, 0));
		storeData(storage, hashStorage);
	}

	private void readProperties() throws FileNotFoundException, IOException {
		try (DataInputStream inputStream = new DataInputStream(new FileInputStream("./config/feedzilla_developer.properties"))) {
			final Properties properties = new Properties();
			properties.load(inputStream);
			feedFolder = properties.getProperty("feed.folder");
			cleanedFeedFolder = properties.getProperty("cleaned.feed.folder");
		}
	}

	private void storeData(FeedzillaFileStorage storage, FeedzillaHashStorage hashStorage) throws FileNotFoundException, IOException {
		FeedzillaFileSaver.saveCategories(cleanedFeedFolder, hashStorage.getHashCategories());
		FeedzillaFileSaver.saveSubcategories(cleanedFeedFolder, hashStorage.getHashSubcategories());
		FeedzillaFileSaver.saveArticles(cleanedFeedFolder, storage.getArticlesById());
	}

	public static void main(String[] args) {
		try {
			new FeedzillaCleaner();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
