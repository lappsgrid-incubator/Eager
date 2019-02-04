package org.lappsgrid.eager.mining.web.db

import org.springframework.data.repository.Repository


/**
 *
 */
//@Repository
interface QuestionRepository extends Repository<Question, String> {
    List<Question> findAll()
    Question findByUuid(String id)

    Question save(Question question)
    void delete(Question question)
    void deleteByUuid(String uuid)
}