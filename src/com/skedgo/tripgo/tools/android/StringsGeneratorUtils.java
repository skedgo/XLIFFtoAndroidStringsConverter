package com.skedgo.tripgo.tools.android;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import com.skedgo.tools.InputCreatorListener;
import com.skedgo.tools.model.StringsStructure;
import com.skedgo.tools.platform.android.AndroidOutputStrategy;
import com.skedgo.tools.platform.xliff.XLIFFInputStrategy;

public class StringsGeneratorUtils {

	private static StringsGeneratorUtils instance;

	public static final String DEFAULT_LANG = "en";

	
	private StringsGeneratorUtils() {
	}

	public static StringsGeneratorUtils getInstance() {
		if (instance == null) {
			instance = new StringsGeneratorUtils();
		}
		return instance;
	}

	public void transformAllStrings(String androidStringPath, String translationsPath,
			String androidSpecificStringsFile, List<String> xliffStringsList, List<String> langs) {

		for (int i = 0; i < xliffStringsList.size(); i++) {
			transformAllStrings(xliffStringsList.get(i) + ".xml", androidStringPath, translationsPath,
					xliffStringsList.get(i), langs);
		}

	}

	public void transformAllStrings(final String androidFileName, final String destAndroidStringPath, String translationsPath,
			String xliffStringFileName, List<String> langs) {

		try (DirectoryStream<Path> directoryStream = Files
				.newDirectoryStream(FileSystems.getDefault().getPath(translationsPath), new DirectoriesFilter())) {

			for (Path path : directoryStream) {

				String lang = path.getFileName().toString();

				if (skipLang(lang, langs)) {
					continue;
				}

				final String androidLangDir = getAndroidLangDir(lang);
				
				if (!Files.exists(Paths.get(translationsPath + "/" + lang + "/" + xliffStringFileName))) {
					continue;
				}

				InputStream input = readFile(translationsPath + "/" + lang + "/" + xliffStringFileName);

				XLIFFInputStrategy inputStrategy = XLIFFInputStrategy.getInstance();
				final AndroidOutputStrategy outputStrategy = AndroidOutputStrategy.getInstance();

				inputStrategy.createInputValues(input, new InputCreatorListener() {
					
					@Override
					public void didFinishInputCreation(StringsStructure structure) {
						structure = outputStrategy.preprocessInputNames(structure);

						String output = outputStrategy.generateOutput(structure);

						try {
							writeFile(destAndroidStringPath + "/" + androidLangDir + "/", androidFileName, output);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}	
					}
				});
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	private String getAndroidLangDir(String lang) {

		// iOS like dir
		String androidLangDir = lang.replace("-", "-r").replace("Hans", "CN").replace("Hant", "TW");

		if (androidLangDir.equals("") || lang.startsWith(DEFAULT_LANG)) { // default
			// res
			androidLangDir = "values";
		} else {
			androidLangDir = "values-" + androidLangDir;
		}
		return androidLangDir;
	}

	private boolean skipLang(String langToCheck, List<String> langs) {

		for (String lang : langs) {
			if (langToCheck.contains(lang)) {
				return false;
			}
		}
		return true;
	}

	private InputStream readFile(String path) throws IOException {
		File file = new File(path);
		return new FileInputStream(file);
	}

	private void writeFile(String dirPath, String fileName, String content) throws IOException {

		Path parentDir = Paths.get(dirPath);
		Path filePath = Paths.get(dirPath + fileName);

		if (!Files.exists(parentDir))
			Files.createDirectories(parentDir);

		if (Files.exists(filePath)) {
			new PrintWriter(dirPath + fileName).close();
		}

		Files.write(filePath, content.getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE);
	}

	public static class DirectoriesFilter implements Filter<Path> {
		@Override
		public boolean accept(Path entry) throws IOException {
			return Files.isDirectory(entry);
		}
	}

}
