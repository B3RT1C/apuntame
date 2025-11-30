package com.apuntame.backend.repository;

import com.apuntame.backend.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.categories c WHERE c.id IN :categoryIds")
    List<Item> findByCategoryIdsOr(@Param("categoryIds") List<Integer> categoryIds);

    @Query("SELECT DISTINCT i FROM Item i JOIN i.categories c WHERE c.id IN :categoryIds " +
           "GROUP BY i.id HAVING COUNT(DISTINCT c.id) = :categoryCount")
    List<Item> findByCategoryIdsAnd(@Param("categoryIds") List<Integer> categoryIds,
                                     @Param("categoryCount") long categoryCount);

    @Query("SELECT DISTINCT i FROM Item i LEFT JOIN FETCH i.sections s WHERE s.id IN :sectionIds")
    List<Item> findBySectionIdsOr(@Param("sectionIds") List<Integer> sectionIds);

    @Query("SELECT DISTINCT i FROM Item i JOIN i.sections s WHERE s.id IN :sectionIds " +
           "GROUP BY i.id HAVING COUNT(DISTINCT s.id) = :sectionCount")
    List<Item> findBySectionIdsAnd(@Param("sectionIds") List<Integer> sectionIds,
                                    @Param("sectionCount") long sectionCount);
}