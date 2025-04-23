package tn.esprit.back.Services.coursCertificat;

import tn.esprit.back.Entities.coursCertificat.Test;

import java.util.List;

public interface ITestService {
    Test addTest(Test test);
    Test updateTest(Test test);
    void deleteTest(long idtest);
    List<Test> getAllTest();
    Test getTestById(long idtest);
}
