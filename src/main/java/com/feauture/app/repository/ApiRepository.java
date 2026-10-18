package com.feauture.app.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.feauture.app.entity.AppApiKeys;
import com.feauture.app.entity.AppApplications;

public interface ApiRepository extends JpaRepository<AppApiKeys, Long>, JpaSpecificationExecutor<AppApiKeys> {

    @Modifying
    @Query("""
                DELETE FROM AppApiKeys a WHERE a.id = :id
            """)
    int deleteApi(@Param("id") Long id);

    @Modifying
    @Query("""
                UPDATE AppApiKeys a SET a.name = :name , a.app = :app , a.updated_at = :updated_at  WHERE a.id = :id
            """)
    int updateApp(@Param("id") Long id, @Param("name") String name, @Param("app") AppApplications app,
            @Param("updated_at") Instant updated_at);

    @Modifying
    @Query("""
                UPDATE AppApiKeys a SET a.status = NOT a.status , a.updated_at = :updated_at WHERE a.id = :id
            """)
    int updateStatusApi(@Param("id") Long id, @Param("updated_at") Instant updated_at);

}
