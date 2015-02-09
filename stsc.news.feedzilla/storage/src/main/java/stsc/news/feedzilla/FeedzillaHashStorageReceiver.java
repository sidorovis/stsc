package stsc.news.feedzilla;

import stsc.news.feedzilla.file.schema.FeedzillaFileCategory;
import stsc.news.feedzilla.file.schema.FeedzillaFileSubcategory;

public interface FeedzillaHashStorageReceiver extends FeedzillaFileStorageReceiver {

	public void addCategory(FeedzillaFileCategory category);

	public void addSubCategory(FeedzillaFileSubcategory subcategory);

}
