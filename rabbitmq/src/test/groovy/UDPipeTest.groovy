import groovy.json.JsonSlurper

/**
 *
 */
class UDPipeTest {

    boolean parse = false

    void run() {
        String url = "http://lindat.mff.cuni.cz/services/udpipe/api/process" //?tokenizer=&tagger=&parser=&model=english&data=${URLEncoder.encode("Karen flew to New York.")}"
        String result = post(url, "Karen flew to New York. Nancy flew to Blommington.")

        JsonSlurper parser = new JsonSlurper()
        Map object = parser.parse(result.chars)

        println object.result
//        println new URL(url).text
    }

    String post(String path, String data) {
        return post(path, true, data)
    }

    String post(String path, boolean parse, String data) {
        return post(path, parse, "english", data)
    }

    String post(String path, boolean parse, String model, String data) {

        QueryParams params = new QueryParams()
        params.parser = parse
        params.model = model
        String query = params.build(data)
        HttpURLConnection connection = new URL(path).openConnection()
        connection.setRequestMethod("POST")
        connection.doOutput = true
        connection.outputStream.withWriter { writer ->
            writer.write(query)
        }
        String result
        connection.inputStream.withReader { reader ->
            result = reader.readLines().join("\n");
        }
        return result
    }

    static void main(String[] args) {
        new UDPipeTest().run()
    }
}
