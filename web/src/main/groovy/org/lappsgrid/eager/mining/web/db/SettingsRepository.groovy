package org.lappsgrid.eager.mining.web.db

import org.springframework.data.repository.Repository

/**
 *
 */
interface SettingsRepository extends Repository<Settings,Long> {
    List<Settings> findAll()
    Settings findById(Long id)
    List<Settings> findByQuestion(String uuid)

    void delete(Settings)
    void deleteByQuestion(String uuid)

    Settings save(Settings settings)
}