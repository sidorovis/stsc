package stsc.news.feedzilla.file.schema;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

final class FileProcessHelper {

	public final static void writeNullableUTF(DataOutputStream dis, String line) throws IOException {
		dis.writeBoolean(line != null);
		if (line != null) {
			dis.writeUTF(line);
		}
	}

	/**
	 * could return null
	 */
	public final static String readNullableUTF(DataInputStream dis) throws IOException {
		final boolean stringIsNotNull = dis.readBoolean();
		if (stringIsNotNull) {
			return dis.readUTF();
		} else {
			final String resultIsNull = null;
			return resultIsNull;
		}
	}

}
