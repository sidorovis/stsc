package stsc.news.feedzilla;

import stsc.news.feedzilla.file.schema.FeedzillaFileArticle;

public interface FeedzillaFileStorageReceiver {

	public boolean addArticle(FeedzillaFileArticle article);

}
