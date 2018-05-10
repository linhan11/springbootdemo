package jp.co.saison.tvc.springbootdemo.app;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;;

@Service
@Transactional
public class DemoDataService {

	@Autowired
	DemoDataRepository repository;

	public List<DemoData> findAll() {
		return repository.findAll();
	}

	public List<DemoData> findBySession(String session) {
		List<DemoData> datas = repository.findAll();

		return datas.stream().filter(a -> session.equals(a.getSession())).collect(Collectors.toList());
	}

	public DemoData save(String session, String name, String message) {
		DemoData data = new DemoData();
		data.setSession(session);
		data.setName(name);
		data.setMessage(message);
        return save(data);
	}

    public DemoData save(DemoData data) {
        return repository.save(data);
    }

}
