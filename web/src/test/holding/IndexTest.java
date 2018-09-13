package org.lappsgrid.pmc.solr;

import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 *
 */
public class IndexTest
{
	public IndexTest()
	{

	}

	@Ignore
	public void indexOneDocument() {
		File input = new File("src/main/resources/");
		assertTrue(input.exists());
		Indexer app = new Indexer();
		app.setDirectory(input);
		app.run();
	}

	@Ignore
	public void idtest() {
		String[] args = { "src/main/resources", "id" };
		Indexer.main(args);
	}

	@Ignore
	public void date() {
		long duration = 1561087;
		System.out.println(new SimpleDateFormat("HH:mm:ss:SSS").format(new Date(duration)));
	}
}
