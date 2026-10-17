package com.feauture.app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.entity.AppApplications;

public interface ApplicationRepository extends JpaRepository<AppApplications, Long> {

    @Modifying

    @Query("""
                UPDATE AppApplications a SET a.name = :name ,a.environment = :environment WHERE a.id = :id

            """)
    int updateApp(@Param("id") Long id, @Param("name") String name, @Param("environment") Integer environment);

    @Modifying
    @Query("""
                UPDATE AppApplications a SET a.status = :status WHERE a.id = :id
            """)
    int updateStatusApp(@Param("id") Long id, @Param("status") boolean status);

    @Modifying
    @Query("DELETE FROM AppApplications a WHERE a.id = :id")
    int deleteApp(@Param("id") Long id);
}
