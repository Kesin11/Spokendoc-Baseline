import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.lucene.queryparser.classic.ParseException;

import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;


public class Main {

	public static void main(String[] args) throws IOException, ParseException, InterruptedException, ParserConfigurationException, TransformerException {
		ArgumentParser parser = ArgumentParsers.newArgumentParser("SpokendocBaseline")
		    .description("Baseline retrieval system for SpokenDoc");
		Subparsers subparsers = parser.addSubparsers();

		// "index" sub command
		Subparser indexSubparser = subparsers.addParser("index")
			.setDefault("subcommand", "index")
		    .help("Make index");
		indexSubparser.addArgument("properties")
		    .required(true)
		    .help("Properties file path");

		// "search" sub command
		Subparser searchSubparser = subparsers.addParser("search")
			.setDefault("subcommand", "search")
		    .help("Search related document with query");
		searchSubparser.addArgument("properties")
		    .required(true)
		    .help("Properties file path");
		searchSubparser.addArgument("query")
		    .required(true)
		    .help("Input query sentence OR queries file path");
		try {
			Namespace ns = parser.parseArgs(args);
//			Namespace ns = parser.parseArgs(new String[]{"search", "lm.properties", "queries.txt"});
//			Namespace ns = parser.parseArgs(new String[]{"index", "lm.properties"});
			// サブコマンド分岐
		    if (ns.getString("subcommand").equals("index")) {
		    	Index.indexing(ns.getString("properties"));
		    }
		    else if (ns.getString("subcommand").equals("search")) {
		    	Search.searching(ns.getString("properties"), ns.getString("query"));
	            System.out.println("Done searching!");
		    }
			
		} catch (ArgumentParserException e) {
			parser.handleError(e);
			System.exit(1);
		}
	}
}
