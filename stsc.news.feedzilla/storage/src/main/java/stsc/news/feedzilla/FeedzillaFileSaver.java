package stsc.news.feedzilla;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;

import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;
import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public final class FeedzillaFileSaver {

	public static final String FILE_EXTENSION = ".fz";
	public static final String FILE_ARTICLE_EXTENSION = ".article.fz";

	private FeedzillaFileSaver() {

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

	public static void saveSubcategories(String feedFolder, Map<String, FeedzillaFileSubcategory> subcategories)
			throws FileNotFoundException, IOException {
		try (DataOutputStream f = new DataOutputStream(new FileOutputStream(feedFolder + "/" + "_subcategories" + FILE_EXTENSION))) {
			f.writeLong(subcategories.size());
			for (Entry<String, FeedzillaFileSubcategory> s : subcategories.entrySet()) {
				s.getValue().saveTo(f);
			}
		}
	}

	public static void saveArticles(String feedFolder, Collection<FeedzillaFileArticle> articles) throws FileNotFoundException, IOException {
		final String timestamp = "a_" + String.valueOf(System.nanoTime());
		saveArticles(feedFolder, articles, timestamp);
	}

	public static void saveArticles(String feedFolder, Collection<FeedzillaFileArticle> articles, String namePostfix)
			throws FileNotFoundException, IOException {
		if (articles.isEmpty()) {
			return;
		}
		try (DataOutputStream f = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(feedFolder + "/" + namePostfix
				+ FILE_ARTICLE_EXTENSION)))) {
			f.writeLong(articles.size());
			for (FeedzillaFileArticle a : articles) {
				a.saveTo(f);
			}
		}
	}

}
