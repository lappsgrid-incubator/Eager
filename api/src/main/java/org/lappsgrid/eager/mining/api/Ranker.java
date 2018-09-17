package org.lappsgrid.eager.mining.api;

import java.util.List;

/**
 *
 */
public interface Ranker
{
	List<Object> rank(List<Object> documents);
}
