package org.lappsgrid.eager.mining.web.db

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

/**
 * The Database class consolidates all database operations into a single class.
 */
@Component
class Database {
    @Autowired
    RatingRepository ratings

    @Autowired
    QuestionRepository questions

    @Autowired
    SettingsRepository settings

    Rating rate(String id, int value) {
        Rating rating = new Rating(id, value)
        ratings.save(rating)
        return rating
    }
    Settings saveSettings(String question, String algorithm, String weight) {
        saveSettings(question, algorithm, weight as Float)
    }

    Settings saveSettings(String question, String algorithm, float weight) {
        Settings s = new Settings(question, algorithm, weight)
        settings.save(s)
        return s
    }

    Rating save(Rating rating) {
        ratings.save(rating)
        return rating
    }
    Question save(Question question) {
        questions.save(question)
        return question
    }
    Settings save(Settings s) {
        settings.save(s)
        return s
    }


    List<Rating> ratings() {
        return ratings.findAll()
    }
    Rating rating(String uuid) {
        return ratings.findByUuid(uuid)
    }
    void delete(Rating rating) {
        ratings.delete(rating)
    }
    void deleteRating(String id) {
        ratings.deleteByUuid(id)
    }

    List<Question> questions() {
        return questions.findAll()
    }
    Question question(String id) {
        return questions.findByUuid(id)
    }
    void delete(Question question) {
        questions.delete(question)
    }
    void deleteQuestion(String id) {
        questions.deleteByUuid(id)
    }

    List<Settings> settings() {
        return settings.findAll()
    }
    List<Settings> settings(String id) {
        settings.findByQuestion(id)
    }
    Settings setting(Long id) {
        return settings.findById(id)
    }
    void delete(Settings set) {
        settings.delete(set)
    }
    void deleteSettings(String id) {
        settings.deleteByQuestion(id)
    }
}
