package stsc.news.feedzilla;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import stsc.common.storage.FeedStorage;
import stsc.news.feedzilla.filedata.FeedzillaFileArticle;
import stsc.news.feedzilla.filedata.FeedzillaFileCategory;
import stsc.news.feedzilla.filedata.FeedzillaFileSubcategory;

public class FeedzillaFileStorage implements FeedStorage {

	public static final String FILE_EXTENSION = ".fz";
	public static final String FILE_ARTICLE_EXTENSION = ".article.fz";

	private final String feedFolder;

	private final Map<Integer, FeedzillaFileCategory> categories = new ConcurrentHashMap<>();
	private final Map<Integer, FeedzillaFileSubcategory> subcategories = new ConcurrentHashMap<>();
	private final Map<Integer, FeedzillaFileArticle> articlesById = new ConcurrentHashMap<>();
	private final Map<Date, List<FeedzillaFileArticle>> articlesByDate = new ConcurrentHashMap<>();

	public FeedzillaFileStorage(String feedFolder) throws FileNotFoundException, IOException {
		this.feedFolder = feedFolder;
		readCategories();
		readSubcategories();
		readArticles();
	}

	public static void saveCategories(String feedFolder, Map<String, FeedzillaFileCategory> categories) throws FileNotFoundException,
			IOException {
		try (DataOutputStream f = new DataOutputStream(new FileOutputStream(feedFolder + "/" + "_categories" + FILE_EXTENSION))) {
			f.writeLong(categories.size());
			for (Entry<String, FeedzillaFileCategory> c : categories.entrySet()) {
				c.getValue().saveTo(f);
			}
		}
	}

	private void readCategories() throws FileNotFoundException, IOException {
		try (DataInputStream f = new DataInputStream(new FileInputStream(feedFolder + "/" + "_categories" + FILE_EXTENSION))) {
			final long sizeOfCategories = f.readLong();
			for (long i = 0; i < sizeOfCategories; ++i) {
				final FeedzillaFileCategory category = new FeedzillaFileCategory(f);
				categories.put(category.getId(), category);
			}
		}
	}

	public static void saveSubcategories(String feedFolder, Map<String, FeedzillaFileSubcategory> subcategories)
			throws FileNotFoundException, IOException {
		try (DataOutputStream f = new DataOutputStream(new FileOutputStream(feedFolder + "/" + "_subcategories" + FILE_EXTENSION))) {
			f.writeLong(subcategories.size());
			for (Entry<String, FeedzillaFileSubcategory> s : subcategories.entrySet()) {
				s.getValue().saveTo(f);
			}
		}
	}

	private void readSubcategories() throws FileNotFoundException, IOException {
		try (DataInputStream f = new DataInputStream(new FileInputStream(feedFolder + "/" + "_subcategories" + FILE_EXTENSION))) {
			final long sizeOfSubcategories = f.readLong();
			for (long i = 0; i < sizeOfSubcategories; ++i) {
				final FeedzillaFileSubcategory subcategory = new FeedzillaFileSubcategory(f, categories);
				subcategories.put(subcategory.getId(), subcategory);
			}
		}
	}

	public static void saveArticles(String feedFolder, List<FeedzillaFileArticle> articles) throws FileNotFoundException, IOException {
		final String timestamp = "a_" + String.valueOf(System.nanoTime());
		try (DataOutputStream f = new DataOutputStream(new FileOutputStream(feedFolder + "/" + timestamp + FILE_ARTICLE_EXTENSION))) {
			f.writeLong(articles.size());
			for (FeedzillaFileArticle a : articles) {
				a.saveTo(f);
			}
		}
	}

	private void readArticles() throws FileNotFoundException, IOException {
		final List<String> articleNames = readFileList(feedFolder);
		for (String articleName : articleNames) {
			final String filePath = feedFolder + "/" + articleName + FILE_ARTICLE_EXTENSION;
			try (DataInputStream f = new DataInputStream(new FileInputStream(filePath))) {
				final long sizeOfArticles = f.readLong();
				for (long i = 0; i < sizeOfArticles; ++i) {
					final FeedzillaFileArticle article = new FeedzillaFileArticle(f, subcategories);
					articlesById.put(article.getId(), article);
					final List<FeedzillaFileArticle> list = articlesByDate.get(article.getPublishDate());
					if (list != null) {
						list.add(article);
					} else {
						final List<FeedzillaFileArticle> newList = Collections.synchronizedList(new ArrayList<>());
						newList.add(article);
						articlesByDate.put(article.getPublishDate(), newList);
					}
				}
			}
		}
	}

	public static List<String> readFileList(String feedFolder) {
		final File folder = new File(feedFolder);
		final File[] listOfFiles = folder.listFiles();
		final List<String> fileNames = new ArrayList<>();
		for (File file : listOfFiles) {
			String filename = file.getName();
			if (file.isFile() && filename.endsWith(FILE_ARTICLE_EXTENSION)) {
				fileNames.add(filename.substring(0, filename.length() - FILE_ARTICLE_EXTENSION.length()));
			}
		}
		return fileNames;
	}

	@Override
	public Collection<FeedzillaFileCategory> getCategories() {
		return categories.values();
	}

	@Override
	public Collection<FeedzillaFileSubcategory> getSubcategories() {
		return subcategories.values();
	}

	@Override
	public Collection<FeedzillaFileArticle> getArticles() {
		return articlesById.values();
	}

	@Override
	public List<FeedzillaFileArticle> getArticles(Date publishDate) {
		final List<FeedzillaFileArticle> result = articlesByDate.get(publishDate);
		if (result == null) {
			return Collections.emptyList();
		}
		return result;
	}

}
