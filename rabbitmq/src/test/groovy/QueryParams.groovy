/**
 *
 */
class QueryParams {
    boolean tokenizer = true
    boolean tagger = true
    boolean parser = true
    String model = "english"

    String toString() {
        List parts = []
        if (tokenizer) {
            parts << "tokenizer="
        }
        if (tagger) {
            parts << "tagger="
        }
        if (parser) {
            parts << "parser="
        }
        if (model) {
            parts << "model=$model"
        }
        return parts.join("&")
    }

    String build(String data) {
        return this.toString() + "&data=" + data
    }
}
