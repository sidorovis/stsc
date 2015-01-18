package stsc.common.feeds;

public class FeedStorageHelper {

	public static String createHashCode(FeedCategory c) {
		return s(c.getDisplayCategoryName()).hashCode() + " " + s(c.getEnglishCategoryName()).hashCode() + " "
				+ s(c.getUrlCategoryName()).hashCode();
	}

	public static String createHashCode(FeedSubcategory c) {
		return s(c.getDisplaySubcategoryName()).hashCode() + " " + s(c.getEnglishSubcategoryName()).hashCode() + " "
				+ s(c.getUrlSubcategoryName()).hashCode();
	}

	public static String createHashCode(FeedArticle a) {
		return s(a.getAuthor()).hashCode() + " " + s(a.getTitle()).hashCode() + " " + s(a.getPublishDate()) + s(a.getUrl()).hashCode()
				+ " " + s(a.getSummary()).hashCode();
	}

	private static <T> String s(T v) {
		if (v == null) {
			return "null";
		}
		return v.toString();
	}
}
