package com.example.vvpdomain;

import com.example.vvpdomain.entity.Menu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zph
 * @description 菜单权限对象
 * @date 2022-07-21
 */
@Repository
public interface MenuRepository extends JpaRepository<Menu, String>, JpaSpecificationExecutor<Menu> {

    List<Menu> findAllByMenuIdInOrderByOrderNum(List<String> menuIds);

    List<Menu> findAllByMenuIdInAndOsTypeInAndStatusOrderByOrderNum(List<String> menuIds,List<String> osType,String status);

    Menu findByMenuName(String menuName);

    List<Menu> findAllByMenuNameLike(String menuName);

    @Query(value =
            "select * from sys_menu sm join sys_menu sm2 on sm.parent_id = sm2.menu_id where sm2.parent_id = '0' and sm2.menu_name like :type",
            nativeQuery = true)
    List<Menu> findAllByType(@Param("type") String type);

    List<Menu> findAllByParentId(String parentId);

    List<Menu> findAllByOsTypeInAndStatusOrderByOrderNum(List<String> osType,String status);

}