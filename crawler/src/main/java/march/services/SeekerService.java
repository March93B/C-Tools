package march.services;

import march.entities.Seeker;

import java.util.List;

public interface SeekerService {
    void createSeeker(Seeker seeker);
    void updateSeeker(Seeker seeker);
    void deleteSeeker(Integer id);
    Seeker getBSeekerId(Integer id);
    Seeker getSeekerByApiName(String apiName);
    Seeker getSeekerByUrl(String url);
    List<Seeker> getSeekers();
    List<Seeker> getSeekersActiveProd();
//    void foundyey(List<BlackDuck> blackDucks, String cookie, String cookie2, String envv, int a, int b) throws Exception;
//    int getProgress();

}
