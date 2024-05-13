package dev.project.userservice.repository;

import dev.project.userservice.entity.Refresh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefreshRepository extends JpaRepository<Refresh, Long> {

    // 주어진 refresh 토큰이 데이터베이스에 존재하는지 확인합니다.
    Boolean existsByRefresh(String refresh);

    // 주어진 refresh 토큰을 데이터베이스에서 삭제합니다.
    @Transactional
    void deleteByRefresh(String refresh);

}