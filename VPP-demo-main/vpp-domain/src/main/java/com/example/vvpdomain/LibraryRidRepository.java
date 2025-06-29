package com.example.vvpdomain;

import com.example.vvpdomain.entity.LibraryRid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryRidRepository extends JpaRepository<LibraryRid, String>, JpaSpecificationExecutor<LibraryRid> {
    @Query(value = "select * from library_rid where dn_id = :dnID", nativeQuery = true)
    List<LibraryRid> findAllByDnID(@Param("dnID") String dnID);

    @Query(value = "select distinct rid from library_rid", nativeQuery = true)
    List<Integer> findRid();
}
