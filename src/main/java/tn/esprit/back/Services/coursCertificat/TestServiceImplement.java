package tn.esprit.back.Services.coursCertificat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tn.esprit.back.Entities.coursCertificat.Test;
import tn.esprit.back.Repository.coursCertificat.CertificatRepository;
import tn.esprit.back.Repository.coursCertificat.TestRepository;

import java.util.List;

@Service
public class TestServiceImplement implements ITestService {

    @Autowired
    TestRepository testRepository;


    @Override
    public Test addTest(Test test) {
        return testRepository.save(test);
    }

    @Override
    public Test updateTest(Test test) {
        return testRepository.save(test);
    }

    @Override
    public void deleteTest(long idtest) {
        testRepository.deleteById(idtest);

    }

    @Override
    public List<Test> getAllTest() {
        return testRepository.findAll();
    }

    @Override
    public Test getTestById(long idtest) {
        return testRepository.findById(idtest).get();
    }
}
