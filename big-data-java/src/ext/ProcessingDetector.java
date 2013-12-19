package ext;

public class ProcessingDetector {
	final public static Class pappletClass = getPappletClass();

	public static boolean inProcessing() {
		return pappletClass != null;
	}

	private static Class getPappletClass() {
		try {
			return Class.forName("processing.core.PApplet");
		} catch (ClassNotFoundException e) {
			return null;
		}
	}
}
