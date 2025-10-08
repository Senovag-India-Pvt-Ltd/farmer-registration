package com.sericulture.registration.repository;


import com.sericulture.registration.model.entity.UserMaster;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface UserMasterRepository extends PagingAndSortingRepository<UserMaster,Long> {

    public Page<UserMaster> findByActiveOrderByUserMasterIdAsc(boolean isActive, final Pageable pageable);

    public UserMaster save(UserMaster userMaster);

    public UserMaster findByUserMasterIdAndActive(long userMasterId, boolean isActive);

    UserMaster findByUsername(String username);

    public List<UserMaster> findByActiveAndUserTypeIdAndMarketMasterId(boolean isActive, long userTypeId, long marketMasterId);

    public List<UserMaster> findByTscMasterIdAndActive(long tscMasterId, boolean isActive);

    @Query("SELECT u.tscMasterId FROM UserMaster u WHERE u.userMasterId = :userMasterId AND u.active = :active")
    Long findTscMasterIdByUserMasterIdAndActive(@Param("userMasterId") Long userMasterId,
                                                @Param("active") boolean active);



    public List<UserMaster> findByActiveAndRoleId(boolean isActive, long roleId);

    public UserMaster findByUserMasterIdAndActiveIn(@Param("userMasterId") long userMasterId, @Param("active") Set<Boolean> active);

    public List<UserMaster> findByActive(boolean isActive);

    public UserMaster findByUsernameAndPasswordAndActive(String username, String password, boolean isActive);


    public UserMaster findByUsernameAndActive(String userName, boolean isActive);



    @Query(nativeQuery = true, value = """
            SELECT
                user_master_id ,
                first_name,
                last_name,
                username
            FROM
                user_master um
            WHERE
                manager_id is NULL
            AND um.active = 1;
            """)
    public List<Object[]> getUserManagerDetails();


    @Query(nativeQuery = true, value = """
            SELECT
              user_master_id ,
              manager_id ,
              first_name,
              last_name,
              username,
              um.phone_number,
              d.district_name,
              ds.name AS designation_name
          FROM
              user_master um
           LEFT JOIN
              district d ON um.district_id = d.district_id
          LEFT JOIN
              designation ds ON um.designation_id = ds.designation_id
          WHERE
              manager_id = :managerId
            AND um.active = 1;
          """)
    public List<Object[]> getDirectReporteeDetails(Long managerId);

    @Query(nativeQuery = true, value = """
              WITH user_hierarchy AS (
              SELECT
                  um.user_master_id,
                  um.manager_id,
                  um.first_name,
                  um.last_name,
                  um.username,
                  um.phone_number,
                  d.district_name,
                  ds.name AS designation_name,
                  1 AS level
              FROM
                  user_master um
              LEFT JOIN
                  district d ON um.district_id = d.district_id
              LEFT JOIN
                  designation ds ON um.designation_id = ds.designation_id
              WHERE
                  um.manager_id = :managerId

              UNION ALL
              SELECT
                  e.user_master_id,
                  e.manager_id,
                  e.first_name,
                  e.last_name,
                  e.username,
                  e.phone_number,
                  NULL AS district_name,
                  NULL AS designation_name,
                  uh.level + 1 AS level
              FROM
                  user_master e
              INNER JOIN
                  user_hierarchy uh ON e.manager_id = uh.user_master_id
          )
          SELECT
              uh.user_master_id,
              uh.manager_id,
              uh.first_name,
              uh.last_name,
              uh.username,
              uh.phone_number,
              COALESCE(d.district_name, '') AS district_name,
              COALESCE(ds.name, '') AS designation_name,
              uh.level
          FROM
              user_hierarchy uh
          LEFT JOIN
              district d ON uh.user_master_id IN (SELECT user_master_id FROM user_master WHERE district_id = d.district_id)
          LEFT JOIN
              designation ds ON uh.user_master_id IN (SELECT user_master_id FROM user_master WHERE designation_id = ds.designation_id)
          ORDER BY
              uh.level, uh.user_master_id;
          """)
    public List<Object[]> getAllReporteeDetails(Long managerId);



}
