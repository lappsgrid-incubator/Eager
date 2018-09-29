package org.lappsgrid.eager.mining.api;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class Query
{
	private String question;
	private String query;
	private List<String> terms;

	public Query() { }

	public Query(String question, String query, List<String> terms) {
		this.question = question;
		this.query = query;
		this.terms = terms;
	}

	public Query question(String question) {
		this.question = question;
		return this;
	}

	public Query query(String query) {
		this.query = query;
		return this;
	}

	public Query terms(List<String> terms) {
		this.terms = terms;
		return this;
	}

	public Query term(String term) {
		if (terms == null) {
			terms = new ArrayList<>();
		}
		this.terms.add(term);
		return this;
	}


	public void setQuestion(String question)
	{
		this.question = question;
	}

	public void setQuery(String query)
	{
		this.query = query;
	}

	public void setTerms(List<String> terms)
	{
		this.terms = terms;
	}

	public String getQuestion()
	{
		return question;
	}

	public String getQuery()
	{
		return query;
	}

	public List<String> getTerms()
	{
		return terms;
	}
}
