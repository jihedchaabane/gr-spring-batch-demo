package com.chj.gr.utilities;

import com.chj.gr.entity.BusinessObjectSkipped;

public class ExtractUtilities {

	public ExtractUtilities() {
		throw new RuntimeException("Instanciation de la classe static ExtractUtilities est interdite!");
	}

	public static BusinessObjectSkipped extractInformations(String rawData, String errorLevel) {
		BusinessObjectSkipped businessObjectSkipped = new BusinessObjectSkipped();
		businessObjectSkipped.setErrorLevel(errorLevel);
		businessObjectSkipped.setRawData(rawData);
        return businessObjectSkipped;
    }
}
