package org.lappsgrid.eager.mining.web.db

import org.springframework.data.repository.Repository

//import org.springframework.stereotype.Repository

/**
 *
 */
interface RatingRepository extends Repository<Rating,String> {
    Rating findByUuid(String id)
    List<Rating> findAll()

    Rating save(Rating rating)
    void delete(Rating rating)
    void deleteByUuid(String uuid)
}