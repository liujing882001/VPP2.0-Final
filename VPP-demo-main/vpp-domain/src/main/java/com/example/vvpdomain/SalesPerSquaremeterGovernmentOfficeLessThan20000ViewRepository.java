package com.example.vvpdomain;

import com.example.vvpdomain.view.SalesPerSquaremeterGovernmentOfficeLessThan20000View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesPerSquaremeterGovernmentOfficeLessThan20000ViewRepository extends JpaRepository<SalesPerSquaremeterGovernmentOfficeLessThan20000View, String>, JpaSpecificationExecutor<SalesPerSquaremeterGovernmentOfficeLessThan20000View> {

    List<SalesPerSquaremeterGovernmentOfficeLessThan20000View> findAllByOrderByCountDate();
}


