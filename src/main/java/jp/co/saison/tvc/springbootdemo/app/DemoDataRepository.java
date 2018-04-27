package jp.co.saison.tvc.springbootdemo.app;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoDataRepository  extends JpaRepository<DemoData, Long> {
}
