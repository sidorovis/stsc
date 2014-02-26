package stsc.yahoofetcher;

public class StringUtils {
	public static int comparePatterns(String l, String r) {
		if (l.length() > r.length())
			return l.length() - r.length();
		
		return l.compareTo(r);
	}

	public static String nextPermutation(String f) {
		boolean onlyZ = true;
		for (int i = 0; i < f.length(); ++i)
			if (f.charAt(i) != 'z')
				onlyZ = false;
		if (onlyZ) {
			String s = "";
			for (int i = 0; i <= f.length(); ++i)
				s = s + 'a';
			return s;
		}
		for (int i = f.length() - 1; i > -1; --i)
			if (f.charAt(i) != 'z') {
				if (i == f.length() - 1) {
					char last = (char) (f.charAt(i) + 1);
					return f.substring(0, f.length() - 1) + new Character(last).toString();
				} else {
					char symb = (char) (f.charAt(i) + 1);
					return f.substring(0, i) + new Character(symb).toString() + "a" + f.substring(i + 2, f.length());
				}
			}
		return f;
	}

}
