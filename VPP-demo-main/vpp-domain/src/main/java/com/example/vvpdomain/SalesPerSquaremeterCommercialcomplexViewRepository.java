package com.example.vvpdomain;

import com.example.vvpdomain.view.SalesPerSquaremeterCommercialcomplexView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SalesPerSquaremeterCommercialcomplexViewRepository extends JpaRepository<SalesPerSquaremeterCommercialcomplexView, String>, JpaSpecificationExecutor<SalesPerSquaremeterCommercialcomplexView> {


    List<SalesPerSquaremeterCommercialcomplexView> findAllByOrderByCountDate();

}

