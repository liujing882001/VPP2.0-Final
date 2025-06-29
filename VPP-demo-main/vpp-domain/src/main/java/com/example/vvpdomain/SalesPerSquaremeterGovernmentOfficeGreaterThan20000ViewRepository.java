package com.example.vvpdomain;

import com.example.vvpdomain.view.SalesPerSquaremeterGovernmentOfficeGreaterThan20000View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SalesPerSquaremeterGovernmentOfficeGreaterThan20000ViewRepository extends JpaRepository<SalesPerSquaremeterGovernmentOfficeGreaterThan20000View, String>, JpaSpecificationExecutor<SalesPerSquaremeterGovernmentOfficeGreaterThan20000View> {

    List<SalesPerSquaremeterGovernmentOfficeGreaterThan20000View> findAllByOrderByCountDate();
}


