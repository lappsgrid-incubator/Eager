package org.lappsgrid.eager.query

/**
 * Manages a List of words that are the stop words for some specific domain
 */
class StopWords {
    Set<String> words = new HashSet<>()

    public _StopWords() {
//        this('/stopwords.txt')
    }

    public StopWords() {
//        InputStream stream = this.class.getResourceAsStream("/stopwords.txt")
//        words = new HashSet<>()
//        stream.eachLine { words.add(it.trim().toLowerCase()) }
        initialize("/stopwords.txt")
    }

    public StopWords(String resource) {
        initialize(resource)
    }

    public StopWords(InputStream stream) {
        initialize(stream)
    }

    private void initialize(String resource) {
        initialize(this.class.getResourceAsStream(resource))
    }

    private void initialize(InputStream stream) {
        words = new HashSet<>()
        stream.eachLine { words.add(it.trim().toLowerCase()) }
    }

    /**
     * Initialize the set of stop words from an arbitrary collection type.
     * @param set
     */
//    public StopWords(Collection<String> set) {
//        set.each { String word -> this.words.add(word) }
//    }

    boolean contains(String word) {
        return words.contains(word.toLowerCase())
    }
}
