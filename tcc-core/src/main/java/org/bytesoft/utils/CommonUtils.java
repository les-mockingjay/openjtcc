package org.bytesoft.utils;

public class CommonUtils {
	public static void close(AutoCloseable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	public static boolean equals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		} else if (o1 == null || o2 == null) {
			return false;
		} else if (!o1.getClass().equals(o2.getClass())) {
			return false;
		}
		return o1.equals(o2);
	}
}
