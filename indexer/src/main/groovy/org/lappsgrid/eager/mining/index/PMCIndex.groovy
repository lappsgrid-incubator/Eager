package org.lappsgrid.eager.mining.index

/**
 *
 */
class PMCIndex {

    List<String> list
    Map<String,String> index

    PMCIndex() {
        InputStream stream = this.class.getResourceAsStream("/pmc-index.txt")
        init(stream)
    }

    PMCIndex(File indexFile) {
        init(new FileInputStream(indexFile))
    }

    PMCIndex(InputStream stream) {
        init(stream)
    }

    int size() {
        return list.size()
    }

    String get(int index) {
        return list[index]
    }

    String get(String id) {
        return index[id]
    }

    // Allow array style access
    String getAt(int index) {
        return list[index]
    }

    String getAt(String id) {
        return index[id]
    }


    private void init(InputStream stream) {
        list = new ArrayList<>()
        index = new HashMap<>()
        stream.eachLine { String line ->
            list.add(line)
            String id = parseID(line)
            if (id != null) {
                index[id] = line
            }
        }
    }

    private String parseID(String path) {
        int index = path.indexOf("PMC")
        if (index < 0) {
            return null
        }
        if (index + 6 >= path.length()) {
            return null
        }
        return path[index..-6]
    }
}
