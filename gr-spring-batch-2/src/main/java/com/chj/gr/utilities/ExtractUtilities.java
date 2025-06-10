package com.chj.gr.utilities;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

import com.chj.gr.entity.BusinessObjectSkipped;

public class ExtractUtilities {

	public ExtractUtilities() {
		throw new RuntimeException("Instanciation de la classe static ExtractUtilities est interdite!");
	}
	
	public static BusinessObjectSkipped buildBoSkipped(Object item, String errorLevel, String errorMessage) {
		String rawData = item instanceof String ? (String) item : reflected(item);
		
		BusinessObjectSkipped failedRecord = new BusinessObjectSkipped();
		failedRecord.setRawData(rawData);
		failedRecord.setErrorLevel(errorLevel);
        failedRecord.setErrorMessage(errorMessage);
        failedRecord.setErrorTimestamp(LocalDateTime.now());
        return failedRecord;
	}

	private static String reflected(Object item) {
		try {
			Method m = item.getClass().getMethod("toRawData");
			return (String) m.invoke(item);
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return item.toString();
	}
}
