package org.lappsgrid.eager.mining.api;

/**
 * Interface for classes that transform the free-form natural language text entered by
 * the user into a query suitable for the search engine (Solr, ElasticSearch)
 */
public interface QueryProcessor {
	/**
	 * Transforms the question from the user into a valid query string.
	 *
	 * @param question a free form question from the user.
	 * @return A query string suitable to be sent to the underlying
	 *         search engine (Solr, ElasticSearch, etc).
	 */
	public Query transform(final String question);
}

