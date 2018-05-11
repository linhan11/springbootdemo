package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DemoDataRepository  extends JpaRepository<DemoData, Long> {
	public List<DemoData> findByNameContainsOrderByIdAsc(String name);
}
